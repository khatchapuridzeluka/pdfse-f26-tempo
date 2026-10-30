package com.tempo.helmetdemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import java.io.Closeable
import kotlin.math.max
import kotlin.math.min

data class PersonResult(
    val box: RectF,
    /** False when the box starts at the top edge of the frame, so the head may be out of view. */
    val headVisible: Boolean,
    val hasHelmet: Boolean,
    val helmetScore: Float,
    val hasVest: Boolean,
)

/** People first, then a helmet check on each person's crop. */
class SafetyAnalyzer(context: Context) : Closeable {

    private val people = PersonDetector(context)
    private val gear = HelmetClassifier(context)

    fun analyze(frame: Bitmap): List<PersonResult> =
        people.detect(frame)
            // Very small boxes are mostly false people, and too blurry to judge a helmet anyway.
            .filter { it.height() >= MIN_PERSON_HEIGHT * frame.height }
            .sortedByDescending { it.width() * it.height() }
            .take(MAX_CHECKED)
            .mapNotNull { box -> checkPerson(frame, box) }

    private fun checkPerson(frame: Bitmap, box: RectF): PersonResult? {
        // A body cut off at the top of the frame has no head to check: judging it gave false alarms.
        if (box.top <= HEAD_EDGE * frame.height) {
            return PersonResult(box, headVisible = false, hasHelmet = false, helmetScore = 0f, hasVest = false)
        }

        // Widen the crop a little and extend it upward so a helmet is never cut off.
        val left = max(0, (box.left - SIDE_PAD * box.width()).toInt())
        val top = max(0, (box.top - TOP_PAD * box.height()).toInt())
        val right = min(frame.width, (box.right + SIDE_PAD * box.width()).toInt())
        val bottom = min(frame.height, box.bottom.toInt())
        if (right - left < MIN_CROP_W || bottom - top < MIN_CROP_H) return null

        val crop = Bitmap.createBitmap(frame, left, top, right - left, bottom - top)
        val result = gear.check(crop)
        crop.recycle()
        return PersonResult(box, headVisible = true, result.hasHelmet, result.helmetScore, result.hasVest)
    }

    override fun close() {
        people.close()
        gear.close()
    }

    companion object {
        private const val MAX_CHECKED = 6
        private const val MIN_PERSON_HEIGHT = 0.10f
        private const val HEAD_EDGE = 0.02f
        private const val SIDE_PAD = 0.10f
        private const val TOP_PAD = 0.15f
        private const val MIN_CROP_W = 16
        private const val MIN_CROP_H = 32
    }
}
