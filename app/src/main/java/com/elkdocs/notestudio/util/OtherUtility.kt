package com.elkdocs.notestudio.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import java.lang.Math.min

object OtherUtility {
    fun provideBackgroundColorPrimary(context: Context): Int {
        val primaryColorAttr = android.R.attr.colorPrimary
        val primaryColorValue = TypedValue()
        context.theme.resolveAttribute(primaryColorAttr, primaryColorValue, true)
        return primaryColorValue.data
    }

    fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
        }
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }




    fun spToPx(sp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toInt()
    }



    fun updateTextPosition(view: View, x: Float, y: Float) {
        if (x != 0f || y != 0f) {
            view.x = x
            view.y = y
        }
    }
    fun updateHeadingTextPosition(view: View,x: Float,y: Float){
        if (x != -1f || y != -1f){
            view.x = x
            view.y = y
        }

    }




    fun updateHeadingUnderlineEditText(text: String, underline: Boolean, length: Int, spanString : (SpannableString) -> Unit) {
        val spannableString = SpannableString(text)
        val underlineSpans = spannableString.getSpans(0, length, UnderlineSpan::class.java)
        if (underline) {
            // Add underline effect
            spannableString.setSpan(UnderlineSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            // Remove underline effect
            for (span in underlineSpans) {
                spannableString.removeSpan(span)
            }
        }

        spanString(spannableString)
    }

    // Function to convert dp to pixels


    fun setTypeface(textView: TextView, typeface: Typeface?) {
        textView.typeface = typeface
    }


    /* Scale factor is a number by which the size of any geometrical figure or shape
     can be changed with respect to its original size.
     It is used to draw the enlarged or reduced shape of any given figure
     and to find the missing length, area, or volume of an enlarged or reduced figure
     formula :-
     Scale factor = Dimensions of the new shape ÷ Dimensions of the original shape.*/
    fun resizeBitmap(originalBitmap: Bitmap, isLayoutFlipped: Boolean): Bitmap {
        val desiredWidth = 1024
        val desiredHeight = 1832

        val originalWidth = originalBitmap.width
        val originalHeight = originalBitmap.height

        val scaleFactor =
            min(desiredWidth / originalWidth.toFloat(), desiredHeight / originalHeight.toFloat())
        val newWidth = (originalWidth * scaleFactor).toInt()
        val newHeight = (originalHeight * scaleFactor).toInt()

        val matrix = Matrix()
        matrix.setScale(scaleFactor, scaleFactor)
        if (isLayoutFlipped) {
            matrix.postScale(-1f, 1f)
        }

        val resizedBitmap =
            Bitmap.createBitmap(originalBitmap, 0, 0, originalWidth, originalHeight, matrix, true)
        val finalBitmap = Bitmap.createScaledBitmap(resizedBitmap, newWidth, newHeight, true)

        return finalBitmap
    }

    fun reverseFontStyleMap(fontStyleMap: Map<String, Int>): Map<Int, String> {
        return fontStyleMap.entries.associate { (key, value) -> value to key }
    }

}