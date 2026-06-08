package com.example.veteica.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var data = mutableListOf<BarData>()
    private val colors = listOf(
        "#4CAF50", "#FF9800", "#2196F3", "#E91E63", "#9C27B0",
        "#00BCD4", "#FF5722", "#607D8B", "#795548", "#CDDC39"
    )

    data class BarData(
        val label: String,
        val value: Float,
        val color: Int
    )

    fun setData(data: List<Pair<String, Float>>) {
        this.data.clear()
        data.forEachIndexed { index, pair ->
            this.data.add(BarData(
                label = pair.first,
                value = pair.second,
                color = Color.parseColor(colors[index % colors.size])
            ))
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()
        val marginLeft = 80f
        val marginRight = 40f
        val marginTop = 40f
        val marginBottom = 60f
        val graphWidth = width - marginLeft - marginRight
        val graphHeight = height - marginTop - marginBottom

        val maxValue = data.maxOfOrNull { it.value } ?: 1f
        val barWidth = graphWidth / data.size * 0.7f
        val barSpacing = (graphWidth / data.size) * 0.3f

        textPaint.color = Color.parseColor("#666666")
        textPaint.textSize = 32f

        gridPaint.color = Color.parseColor("#E0E0E0")
        gridPaint.strokeWidth = 1f

        // Dibujar grid horizontal
        for (i in 0..4) {
            val y = marginTop + (graphHeight / 4) * i
            canvas.drawLine(marginLeft, y, width - marginRight, y, gridPaint)

            val value = (maxValue * (1 - i / 4f)).toInt()
            canvas.drawText("$value%", marginLeft - 50, y + 10, textPaint)
        }

        // Dibujar barras
        for (i in data.indices) {
            val barHeight = (data[i].value / maxValue) * graphHeight
            val x = marginLeft + (i * (barWidth + barSpacing)) + barSpacing / 2
            val y = marginTop + graphHeight - barHeight

            barPaint.color = data[i].color
            barPaint.style = Paint.Style.FILL
            canvas.drawRect(x, y, x + barWidth, marginTop + graphHeight, barPaint)

            // Etiquetas debajo de las barras
            textPaint.textSize = 32f
            val label = data[i].label
            val labelWidth = textPaint.measureText(label)
            canvas.drawText(label, x + (barWidth / 2) - (labelWidth / 2), height - marginBottom + 25, textPaint)

            // Valor encima de la barra
            textPaint.textSize = 28f
            val valueText = "${data[i].value.toInt()}%"
            val valueWidth = textPaint.measureText(valueText)
            canvas.drawText(valueText, x + (barWidth / 2) - (valueWidth / 2), y - 10, textPaint)
        }
    }
}