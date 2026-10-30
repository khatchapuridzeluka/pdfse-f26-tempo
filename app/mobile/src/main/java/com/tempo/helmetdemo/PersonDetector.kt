package com.tempo.helmetdemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import java.io.Closeable

/** Finds people in a frame with MediaPipe's EfficientDet-Lite0 (COCO "person" class). */
class PersonDetector(context: Context) : Closeable {

    private val detector: ObjectDetector = ObjectDetector.createFromOptions(
        context,
        ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(BaseOptions.builder().setModelAssetPath(MODEL).build())
            .setRunningMode(RunningMode.IMAGE)
            .setScoreThreshold(MIN_SCORE)
            .setCategoryAllowlist(listOf("person"))
            .setMaxResults(MAX_PEOPLE)
            .build()
    )

    /** Returns person boxes in [bitmap] pixel coordinates. [bitmap] must be ARGB_8888. */
    fun detect(bitmap: Bitmap): List<RectF> =
        detector.detect(BitmapImageBuilder(bitmap).build())
            .detections()
            .map { it.boundingBox() }

    override fun close() = detector.close()

    companion object {
        const val MODEL = "efficientdet_lite0.tflite"
        const val MIN_SCORE = 0.4f
        const val MAX_PEOPLE = 8
    }
}
