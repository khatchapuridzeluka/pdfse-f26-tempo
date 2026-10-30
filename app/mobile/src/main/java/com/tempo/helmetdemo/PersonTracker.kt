package com.tempo.helmetdemo

import android.graphics.RectF
import kotlin.math.max
import kotlin.math.min

enum class HelmetStatus {
    /** Not enough frames yet. */
    CHECKING,
    HELMET,
    /** Looks helmet-less, but not for long enough to raise the alarm. */
    SUSPECTED,
    /** Helmet-less for at least [PersonTracker.CONFIRM_MS]: a real violation. */
    NO_HELMET,
    /** Head is outside the frame, so the person is not judged. */
    HEAD_HIDDEN,
}

data class TrackedPerson(
    val box: RectF,
    val status: HelmetStatus,
    val helmetScore: Float,
    val hasVest: Boolean,
)

/**
 * Follows each person from frame to frame (by box overlap) and decides helmet / no helmet by
 * majority vote over their last few frames. A "no helmet" verdict only becomes a violation after
 * it has held for [CONFIRM_MS], so a few bad frames never raise a false alarm.
 */
class PersonTracker {

    private enum class Verdict { NONE, HELMET, NO_HELMET }

    private class Track(var box: RectF, var lastSeenMs: Long) {
        val helmetVotes = ArrayDeque<Boolean>()
        val vestVotes = ArrayDeque<Boolean>()
        val helmetScores = ArrayDeque<Float>()
        var verdict = Verdict.NONE
        var headVisible = true
        /** When the current unbroken NO_HELMET verdict started, or null. */
        var noHelmetSinceMs: Long? = null
    }

    private val tracks = mutableListOf<Track>()

    fun reset() = tracks.clear()

    fun update(detections: List<PersonResult>, nowMs: Long): List<TrackedPerson> {
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
            observe(track, detections[i], nowMs)
        }
        detections.forEachIndexed { i, d ->
            if (i !in matchedDetections) {
                val track = Track(RectF(d.box), nowMs)
                tracks += track
                seen += track
                observe(track, d, nowMs)
            }
        }

        // People hidden for a moment (bending down, walking behind something) keep their history.
        tracks.removeAll { nowMs - it.lastSeenMs > FORGET_MS }

        return tracks.filter { it in seen }.map { t ->
            val helmetFrames = t.helmetScores.filter { it > 0f }
            TrackedPerson(
                box = RectF(t.box),
                status = status(t, nowMs),
                helmetScore = if (helmetFrames.isEmpty()) 0f else helmetFrames.average().toFloat(),
                hasVest = t.vestVotes.count { it } * 2 > t.vestVotes.size,
            )
        }
    }

    private fun status(t: Track, nowMs: Long): HelmetStatus {
        if (!t.headVisible) return HelmetStatus.HEAD_HIDDEN
        val since = t.noHelmetSinceMs
        return when (t.verdict) {
            Verdict.NONE -> HelmetStatus.CHECKING
            Verdict.HELMET -> HelmetStatus.HELMET
            Verdict.NO_HELMET ->
                if (since != null && nowMs - since >= CONFIRM_MS) HelmetStatus.NO_HELMET else HelmetStatus.SUSPECTED
        }
    }

    private fun observe(track: Track, d: PersonResult, nowMs: Long) {
        track.lastSeenMs = nowMs
        // Smooth the box a little so it does not jitter.
        track.box.set(
            lerp(track.box.left, d.box.left),
            lerp(track.box.top, d.box.top),
            lerp(track.box.right, d.box.right),
            lerp(track.box.bottom, d.box.bottom),
        )

        track.headVisible = d.headVisible
        if (!d.headVisible) {
            // No vote without a head, and the violation timer starts over once it is back.
            track.noHelmetSinceMs = null
            return
        }

        track.helmetVotes.push(d.hasHelmet)
        track.vestVotes.push(d.hasVest)
        track.helmetScores.push(d.helmetScore)

        val votes = track.helmetVotes.size
        if (votes >= MIN_VOTES) {
            val yes = track.helmetVotes.count { it }
            track.verdict = when {
                yes * 2 > votes -> Verdict.HELMET
                yes * 2 < votes -> Verdict.NO_HELMET
                else -> track.verdict // a tie keeps the last decision
            }
        }
        if (track.verdict == Verdict.NO_HELMET) {
            if (track.noHelmetSinceMs == null) track.noHelmetSinceMs = nowMs
        } else {
            track.noHelmetSinceMs = null
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
        /** How long a person must look helmet-less before it counts (tested on 10 site videos). */
        const val CONFIRM_MS = 1500L
        /** Frames remembered per person. */
        private const val WINDOW = 5
        /** Frames needed before a person gets a verdict (shown as "CHECKING" until then). */
        private const val MIN_VOTES = 3
        private const val MIN_IOU = 0.3f
        /** How long a person may be missing before they are forgotten. */
        private const val FORGET_MS = 1000L
        private const val BOX_SMOOTHING = 0.6f
    }
}
