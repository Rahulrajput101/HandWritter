package com.elkdocs.handwritter.domain.use_cases

import android.graphics.Canvas
import android.graphics.Paint

class DrawLine {
    operator fun invoke(canvas: Canvas, fontSize: Float, lineColor: Int){

        val lineSpacing = fontSize * 2.5f // or any other ratio you prefer

        val linePaint = Paint()
        linePaint.strokeWidth = 4f
        linePaint.color = lineColor

        val yOffset = (fontSize - lineSpacing) / 2 // Center the lines vertically

        for (i in 0 until canvas.height step lineSpacing.toInt()) {
            canvas.drawLine(0f, i + yOffset, canvas.width.toFloat(), i + yOffset, linePaint)
        }
    }

}