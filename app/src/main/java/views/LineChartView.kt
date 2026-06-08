package com.example.veteica.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var data = listOf<Float>()
    private var labels = listOf<String>()
    private var lineColor = Color.parseColor("#2E7D32")

    init {
        pointPaint.style = Paint.Style.FILL
        pointPaint.color = Color.parseColor("#FF9800")

        textPaint.color = Color.parseColor("#666666")
        textPaint.textSize = 32f

        gridPaint.color = Color.parseColor("#E0E0E0")
        gridPaint.strokeWidth = 1f
    }

    fun setData(data: List<Float>, labels: List<String>) {
        this.data = data
        this.labels = labels
        invalidate()
    }

    fun setLineColor(color: Int) {
        this.lineColor = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()
        val marginLeft = 60f
        val marginRight = 40f
        val marginTop = 40f
        val marginBottom = 60f
        val graphWidth = width - marginLeft - marginRight
        val graphHeight = height - marginTop - marginBottom

        val maxValue = data.maxOrNull()?.toFloat() ?: 1f
        val stepX = graphWidth / (data.size - 1)

        // Dibujar grid horizontal
        for (i in 0..4) {
            val y = marginTop + (graphHeight / 4) * i
            canvas.drawLine(marginLeft, y, width - marginRight, y, gridPaint)

            val value = (maxValue * (1 - i / 4f)).toInt()
            textPaint.textSize = 32f
            canvas.drawText("$value%", marginLeft - 50, y + 10, textPaint)
        }

        // Dibujar labels en X
        textPaint.textSize = 36f
        for (i in labels.indices) {
            val x = marginLeft + stepX * i
            canvas.drawText(labels[i], x - 30, height - marginBottom + 30, textPaint)
        }

        // Dibujar la línea
        paint.color = lineColor
        paint.strokeWidth = 6f
        paint.style = Paint.Style.STROKE

        val path = Path()
        var firstPoint = true

        for (i in data.indices) {
            val x = marginLeft + stepX * i
            val y = marginTop + graphHeight * (1 - (data[i] / maxValue))

            if (firstPoint) {
                path.moveTo(x, y)
                firstPoint = false
            } else {
                path.lineTo(x, y)
            }
        }
        canvas.drawPath(path, paint)

        // Dibujar puntos
        pointPaint.style = Paint.Style.FILL
        for (i in data.indices) {
            val x = marginLeft + stepX * i
            val y = marginTop + graphHeight * (1 - (data[i] / maxValue))
            canvas.drawCircle(x, y, 12f, pointPaint)
        }
    }
}