package com.tempo.helmetdemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import org.tensorflow.lite.Interpreter
import java.io.Closeable
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.max
import kotlin.math.min

/**
 * Runs Qualcomm's GearGuardNet (PPE-Detection) on one person crop and reports whether a
 * helmet is on the head and whether a vest is visible.
 *
 * Model input: [1, 320, 192, 3] float RGB in 0..1. Outputs are already decoded:
 * boxes [1, N, 4] (x1, y1, x2, y2 in input pixels), scores [1, N], class_idx [1, N] (0 helmet, 1 vest).
 */
class HelmetClassifier(context: Context) : Closeable {

    data class Gear(val hasHelmet: Boolean, val helmetScore: Float, val hasVest: Boolean)

    private val interpreter = Interpreter(
        loadModel(context, MODEL),
        Interpreter.Options().setNumThreads(4)
    )

    private val boxesIndex: Int
    private val scoresIndex: Int
    private val classIndex: Int
    private val numBoxes: Int

    private val input = ByteBuffer.allocateDirect(4 * IN_W * IN_H * 3).order(ByteOrder.nativeOrder())
    private val letterbox = Bitmap.createBitmap(IN_W, IN_H, Bitmap.Config.ARGB_8888)
    private val letterboxCanvas = Canvas(letterbox)
    private val scalePaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val pixels = IntArray(IN_W * IN_H)

    private val boxes: Array<Array<FloatArray>>
    private val scores: Array<FloatArray>
    private val classes: Array<ByteArray>

    init {
        val names = (0 until interpreter.outputTensorCount).associateBy { interpreter.getOutputTensor(it).name() }
        boxesIndex = names.getValue("boxes")
        scoresIndex = names.getValue("scores")
        classIndex = names.getValue("class_idx")
        numBoxes = interpreter.getOutputTensor(boxesIndex).shape()[1]
        boxes = Array(1) { Array(numBoxes) { FloatArray(4) } }
        scores = Array(1) { FloatArray(numBoxes) }
        classes = Array(1) { ByteArray(numBoxes) }
    }

    fun check(crop: Bitmap): Gear {
        // Letterbox the crop into the 192x320 portrait input, keeping its aspect ratio.
        val scale = min(IN_W.toFloat() / crop.width, IN_H.toFloat() / crop.height)
        val w = crop.width * scale
        val h = crop.height * scale
        val padX = (IN_W - w) / 2f
        val padY = (IN_H - h) / 2f
        letterboxCanvas.drawColor(Color.rgb(114, 114, 114))
        letterboxCanvas.drawBitmap(crop, null, RectF(padX, padY, padX + w, padY + h), scalePaint)

        letterbox.getPixels(pixels, 0, IN_W, 0, 0, IN_W, IN_H)
        input.rewind()
        for (p in pixels) {
            input.putFloat(((p shr 16) and 0xFF) / 255f)
            input.putFloat(((p shr 8) and 0xFF) / 255f)
            input.putFloat((p and 0xFF) / 255f)
        }
        input.rewind()

        interpreter.runForMultipleInputsOutputs(
            arrayOf(input),
            mapOf(boxesIndex to boxes, scoresIndex to scores, classIndex to classes)
        )

        // A helmet only counts if it sits on the head: centre in the top part of the crop.
        val headLimit = HEAD_ZONE * crop.height
        var helmetScore = 0f
        var hasVest = false
        for (i in 0 until numBoxes) {
            val score = scores[0][i]
            when (classes[0][i].toInt() and 0xFF) {
                CLASS_HELMET -> if (score >= HELMET_SCORE) {
                    val b = boxes[0][i]
                    val centreY = ((b[1] + b[3]) / 2f - padY) / scale
                    if (centreY < headLimit) helmetScore = max(helmetScore, score)
                }
                CLASS_VEST -> if (score >= VEST_SCORE) hasVest = true
            }
        }
        return Gear(helmetScore > 0f, helmetScore, hasVest)
    }

    override fun close() = interpreter.close()

    companion object {
        const val MODEL = "gear_guard_net.tflite"
        private const val IN_W = 192
        private const val IN_H = 320
        private const val CLASS_HELMET = 0
        private const val CLASS_VEST = 1
        // Tuned on test photos: lower catches white helmets seen side-on, higher adds misses.
        private const val HELMET_SCORE = 0.35f
        private const val VEST_SCORE = 0.4f
        private const val HEAD_ZONE = 0.45f

        private fun loadModel(context: Context, name: String): MappedByteBuffer =
            context.assets.openFd(name).use { fd ->
                FileInputStream(fd.fileDescriptor).channel.use { channel ->
                    channel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
                }
            }
    }
}
