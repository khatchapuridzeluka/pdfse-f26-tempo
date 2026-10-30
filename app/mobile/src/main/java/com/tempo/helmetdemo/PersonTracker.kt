package com.tempo.helmetdemo

import android.graphics.RectF
import kotlin.math.max
import kotlin.math.min

enum class HelmetStatus { CHECKING, HELMET, NO_HELMET }

data class TrackedPerson(
    val box: RectF,
    val status: HelmetStatus,
    val helmetScore: Float,
    val hasVest: Boolean,
)

/**
 * Follows each person from frame to frame (by box overlap) and decides helmet / no helmet by
 * majority vote over their last few frames, so one bad frame does not flip the label.
 */
class PersonTracker {

    private class Track(var box: RectF) {
        val helmetVotes = ArrayDeque<Boolean>()
        val vestVotes = ArrayDeque<Boolean>()
        val helmetScores = ArrayDeque<Float>()
        var status = HelmetStatus.CHECKING
        var missed = 0
    }

    private val tracks = mutableListOf<Track>()

    fun reset() = tracks.clear()

    fun update(detections: List<PersonResult>): List<TrackedPerson> {
        // Greedy matching: best-overlapping detection/track pairs first.
        val pairs = mutableListOf<Triple<Int, Track, Float>>()
        detections.forEachIndexed { i, d ->
            for (t in tracks) {
                val overlap = iou(d.box, t.box)
                if (overlap >= MIN_IOU) pairs += Triple(i, t, overlap)
            }
        }
        pairs.sortByDescending { it.third }

        val matchedDetections = mutableSetOf<Int>()
        val seen = mutableSetOf<Track>()
        for ((i, track, _) in pairs) {
            if (i in matchedDetections || track in seen) continue
            matchedDetections += i
            seen += track
            observe(track, detections[i])
        }
        detections.forEachIndexed { i, d ->
            if (i !in matchedDetections) {
                val track = Track(RectF(d.box))
                tracks += track
                seen += track
                observe(track, d)
            }
        }

        tracks.filter { it !in seen }.forEach { it.missed++ }
        tracks.removeAll { it.missed > MAX_MISSED }

        return tracks.filter { it in seen }.map { t ->
            val helmetFrames = t.helmetScores.filter { it > 0f }
            TrackedPerson(
                box = RectF(t.box),
                status = t.status,
                helmetScore = if (helmetFrames.isEmpty()) 0f else helmetFrames.average().toFloat(),
                hasVest = t.vestVotes.count { it } * 2 > t.vestVotes.size,
            )
        }
    }

    private fun observe(track: Track, d: PersonResult) {
        track.missed = 0
        // Smooth the box a little so it does not jitter.
        track.box.set(
            lerp(track.box.left, d.box.left),
            lerp(track.box.top, d.box.top),
            lerp(track.box.right, d.box.right),
            lerp(track.box.bottom, d.box.bottom),
        )
        track.helmetVotes.push(d.hasHelmet)
        track.vestVotes.push(d.hasVest)
        track.helmetScores.push(d.helmetScore)

        val votes = track.helmetVotes.size
        if (votes < MIN_VOTES) return
        val yes = track.helmetVotes.count { it }
        track.status = when {
            yes * 2 > votes -> HelmetStatus.HELMET
            yes * 2 < votes -> HelmetStatus.NO_HELMET
            else -> track.status // a tie keeps the last decision
        }
    }

    private fun <T> ArrayDeque<T>.push(value: T) {
        addLast(value)
        if (size > WINDOW) removeFirst()
    }

    private fun lerp(old: Float, new: Float) = old + (new - old) * BOX_SMOOTHING

    private fun iou(a: RectF, b: RectF): Float {
        val w = min(a.right, b.right) - max(a.left, b.left)
        val h = min(a.bottom, b.bottom) - max(a.top, b.top)
        if (w <= 0f || h <= 0f) return 0f
        val inter = w * h
        return inter / (a.width() * a.height() + b.width() * b.height() - inter)
    }

    companion object {
        /** Frames remembered per person. */
        private const val WINDOW = 5
        /** Frames needed before a person gets a verdict (shown as "CHECKING" until then). */
        private const val MIN_VOTES = 3
        private const val MIN_IOU = 0.3f
        /** Frames a person may be missing before they are forgotten. */
        private const val MAX_MISSED = 3
        private const val BOX_SMOOTHING = 0.6f
    }
}
