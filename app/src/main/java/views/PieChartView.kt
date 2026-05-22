package com.example.veteica.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private var data = listOf<PieData>()

    data class PieData(
        val name: String,
        val value: Float,
        val color: Int
    )

    fun setData(data: List<PieData>) {
        this.data = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()
        val size = Math.min(width, height)
        val left = (width - size) / 2
        val top = (height - size) / 2
        val right = left + size
        val bottom = top + size

        rectF.set(left, top, right, bottom)

        var startAngle = 0f

        // Calcular el total usando fold para evitar ambigüedad
        val total = data.fold(0f) { acc, item -> acc + item.value }

        for (item in data) {
            val sweepAngle = (item.value / total) * 360f
            paint.color = item.color
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint)
            startAngle += sweepAngle
        }
    }
}