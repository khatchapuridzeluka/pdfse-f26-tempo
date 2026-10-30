package com.tempo.helmetdemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

/**
 * Draws a box over each person: green helmet, solid red confirmed violation, dashed red suspected,
 * dashed amber still checking, dashed grey head not visible.
 */
class OverlayView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var results: List<TrackedPerson> = emptyList()
    private var sourceWidth = 1
    private var sourceHeight = 1
    private val painter = BoxPainter(resources.displayMetrics.density)

    fun setResults(results: List<TrackedPerson>, sourceWidth: Int, sourceHeight: Int) {
        this.results = results
        this.sourceWidth = sourceWidth
        this.sourceHeight = sourceHeight
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Same mapping as PreviewView's default FILL_CENTER: scale to cover, centre, crop.
        val scale = max(width.toFloat() / sourceWidth, height.toFloat() / sourceHeight)
        val dx = (width - sourceWidth * scale) / 2f
        val dy = (height - sourceHeight * scale) / 2f
        painter.draw(canvas, results, scale, dx, dy)
    }
}

/** Shared by the live overlay and the saved snapshot. */
class BoxPainter(density: Float) {

    private val box = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f * density
        isAntiAlias = true
    }
    private val labelBg = Paint().apply { style = Paint.Style.FILL }
    private val text = Paint().apply {
        color = Color.WHITE
        textSize = 15f * density
        isFakeBoldText = true
        isAntiAlias = true
    }
    private val pad = 4f * density
    private val dashes = DashPathEffect(floatArrayOf(10f * density, 6f * density), 0f)

    fun draw(canvas: Canvas, results: List<TrackedPerson>, scale: Float, dx: Float, dy: Float) {
        for (r in results) {
            val color = when (r.status) {
                HelmetStatus.HELMET -> OK_GREEN
                HelmetStatus.NO_HELMET, HelmetStatus.SUSPECTED -> VIOLATION_RED
                HelmetStatus.CHECKING -> CHECKING_AMBER
                HelmetStatus.HEAD_HIDDEN -> HIDDEN_GREY
            }
            val rect = RectF(
                r.box.left * scale + dx,
                r.box.top * scale + dy,
                r.box.right * scale + dx,
                r.box.bottom * scale + dy,
            )
            box.color = color
            // Dashed until confirmed: only a solid red box is a real violation.
            box.pathEffect = if (r.status == HelmetStatus.NO_HELMET || r.status == HelmetStatus.HELMET) null else dashes
            canvas.drawRect(rect, box)

            var label = when (r.status) {
                HelmetStatus.HELMET -> "HELMET ${(r.helmetScore * 100).toInt()}%"
                HelmetStatus.NO_HELMET -> "NO HELMET"
                HelmetStatus.SUSPECTED -> "NO HELMET?"
                HelmetStatus.CHECKING -> "CHECKING…"
                HelmetStatus.HEAD_HIDDEN -> "HEAD NOT VISIBLE"
            }
            if (r.hasVest) label += " · vest"
            val textWidth = text.measureText(label)
            val labelTop = max(0f, rect.top - text.textSize - 2 * pad)
            labelBg.color = color
            canvas.drawRect(rect.left, labelTop, rect.left + textWidth + 2 * pad, labelTop + text.textSize + 2 * pad, labelBg)
            canvas.drawText(label, rect.left + pad, labelTop + pad + text.textSize * 0.85f, text)
        }
    }

    companion object {
        val OK_GREEN = Color.rgb(0x43, 0xA0, 0x47)
        val VIOLATION_RED = Color.rgb(0xE5, 0x39, 0x35)
        val CHECKING_AMBER = Color.rgb(0xFF, 0xA0, 0x00)
        val HIDDEN_GREY = Color.rgb(0x9E, 0x9E, 0x9E)
    }
}
