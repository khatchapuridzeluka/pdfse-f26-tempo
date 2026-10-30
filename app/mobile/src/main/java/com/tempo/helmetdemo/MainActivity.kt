package com.tempo.helmetdemo

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var overlay: OverlayView
    private lateinit var status: TextView
    private lateinit var banner: TextView

    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK

    // Created and used only on cameraExecutor.
    private var analyzer: SafetyAnalyzer? = null

    // Last analysed frame, kept for the "Save photo" button.
    @Volatile private var lastFrame: Bitmap? = null
    @Volatile private var lastResults: List<PersonResult> = emptyList()

    private var violationStreak = 0
    private var lastVibrationAt = 0L

    private val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startCamera() else status.setText(R.string.no_camera_permission)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        previewView = findViewById(R.id.preview)
        overlay = findViewById(R.id.overlay)
        status = findViewById(R.id.status)
        banner = findViewById(R.id.banner)
        keepClearOfSystemBars()

        findViewById<Button>(R.id.flip).setOnClickListener {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            bindCamera()
        }
        findViewById<Button>(R.id.snapshot).setOnClickListener { saveSnapshot() }

        cameraExecutor.execute {
            try {
                analyzer = SafetyAnalyzer(this)
                runOnUiThread { status.text = "Point the camera at people on site" }
            } catch (e: Exception) {
                Log.e(TAG, "Model load failed", e)
                runOnUiThread { status.text = "Model load failed: ${e.message}" }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestCamera.launch(Manifest.permission.CAMERA)
        }
    }

    /** Android 15+ draws apps edge to edge: keep the status text and buttons out of the system bars. */
    private fun keepClearOfSystemBars() {
        val controls = findViewById<View>(R.id.controls)
        val statusTop = status.paddingTop
        val controlsBottom = controls.paddingBottom
        val bannerMargin = (banner.layoutParams as FrameLayout.LayoutParams).bottomMargin
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            status.updatePadding(top = statusTop + bars.top)
            controls.updatePadding(bottom = controlsBottom + bars.bottom)
            banner.updateLayoutParams<FrameLayout.LayoutParams> { bottomMargin = bannerMargin + bars.bottom }
            insets
        }
    }

    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            cameraProvider = future.get()
            bindCamera()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCamera() {
        val provider = cameraProvider ?: return

        // Preview and analysis share a 4:3 aspect ratio so overlay boxes line up with the preview.
        val aspect = AspectRatioStrategy(AspectRatio.RATIO_4_3, AspectRatioStrategy.FALLBACK_RULE_AUTO)
        val preview = Preview.Builder()
            .setResolutionSelector(ResolutionSelector.Builder().setAspectRatioStrategy(aspect).build())
            .build()
            .also { it.surfaceProvider = previewView.surfaceProvider }

        val analysis = ImageAnalysis.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setAspectRatioStrategy(aspect)
                    .setResolutionStrategy(
                        ResolutionStrategy(Size(640, 480), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER)
                    )
                    .build()
            )
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also { it.setAnalyzer(cameraExecutor, ::analyzeFrame) }

        val selector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        try {
            provider.unbindAll()
            provider.bindToLifecycle(this, selector, preview, analysis)
        } catch (e: Exception) {
            Log.e(TAG, "Camera bind failed", e)
            status.text = "Camera error: ${e.message}"
        }
    }

    private fun analyzeFrame(image: ImageProxy) {
        val safety = analyzer
        if (safety == null) {
            image.close()
            return
        }
        val started = SystemClock.elapsedRealtime()

        // Turn the sensor frame upright (and mirrored for the selfie camera) to match the preview.
        val raw = image.toBitmap()
        val matrix = Matrix().apply {
            postRotate(image.imageInfo.rotationDegrees.toFloat())
            if (lensFacing == CameraSelector.LENS_FACING_FRONT) postScale(-1f, 1f)
        }
        image.close()
        val frame = Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, matrix, true)

        val results = try {
            safety.analyze(frame)
        } catch (e: Exception) {
            Log.e(TAG, "Analysis failed", e)
            emptyList()
        }
        val elapsed = SystemClock.elapsedRealtime() - started

        lastFrame = frame
        lastResults = results
        runOnUiThread {
            overlay.setResults(results, frame.width, frame.height)
            showStatus(results, elapsed)
        }
    }

    private fun showStatus(results: List<PersonResult>, elapsedMs: Long) {
        val withHelmet = results.count { it.hasHelmet }
        val without = results.size - withHelmet
        status.text = "People: ${results.size}   ✓ Helmet: $withHelmet   ✗ No helmet: $without\n$elapsedMs ms per frame"

        // Only raise the alarm after a few frames in a row, so one bad frame doesn't flash it.
        violationStreak = if (without > 0) violationStreak + 1 else 0
        val alarm = violationStreak >= ALARM_FRAMES
        banner.visibility = if (alarm) View.VISIBLE else View.GONE
        status.setBackgroundColor(if (alarm) BoxPainter.VIOLATION_RED and 0xCCFFFFFF.toInt() else 0xB3000000.toInt())

        val now = SystemClock.elapsedRealtime()
        if (alarm && now - lastVibrationAt > VIBRATE_EVERY_MS) {
            lastVibrationAt = now
            vibrate()
        }
    }

    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }
        vibrator?.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    /** Saves the last analysed frame with its boxes, as evidence of a violation. */
    private fun saveSnapshot() {
        val frame = lastFrame ?: return
        val results = lastResults
        val annotated = frame.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(annotated)
        BoxPainter(annotated.width / 400f).draw(canvas, results, 1f, 0f, 0f)

        val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val without = results.count { !it.hasHelmet }
        val caption = "TEMPO · $stamp · people ${results.size} · no helmet $without"
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = annotated.width / 28f
            isAntiAlias = true
            setShadowLayer(4f, 0f, 0f, Color.BLACK)
        }
        canvas.drawText(caption, textPaint.textSize / 2, annotated.height - textPaint.textSize / 2, textPaint)

        val name = "tempo_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".jpg"
        try {
            val where = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, name)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TEMPO")
                }
                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: error("Could not create image")
                contentResolver.openOutputStream(uri)!!.use { annotated.compress(Bitmap.CompressFormat.JPEG, 92, it) }
                "Pictures/TEMPO"
            } else {
                val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), name)
                file.outputStream().use { annotated.compress(Bitmap.CompressFormat.JPEG, 92, it) }
                file.parent ?: ""
            }
            Toast.makeText(this, "Saved to $where", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Snapshot failed", e)
            Toast.makeText(this, "Could not save photo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.execute { analyzer?.close() }
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "TempoSafety"
        private const val ALARM_FRAMES = 3
        private const val VIBRATE_EVERY_MS = 3000L
    }
}
