package com.elkdocs.handwritter.domain.use_cases

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.elkdocs.handwritter.util.Constant

class DrawLine {
    operator fun invoke(canvas: Canvas, fontSize: Float, lineColor: Int){

        val lineSpacing = fontSize * 2.5f // or any other ratio you prefer

        val linePaint = Paint()
        linePaint.strokeWidth = 2f
        linePaint.color = lineColor

        val yOffset = (fontSize - lineSpacing) / 2 // Center the lines vertically

        val paddingTop = canvas.height * 0.10f

        for (i in paddingTop.toInt() until canvas.height step lineSpacing.toInt()) {
            if (i == paddingTop.toInt()) {
                linePaint.color = Color.parseColor("#D1C2E1")
            } else {
                linePaint.color = lineColor
            }

            canvas.drawLine(0f, i + yOffset, canvas.width.toFloat(), i + yOffset, linePaint)
        }
    }
}