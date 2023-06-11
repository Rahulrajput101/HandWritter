package com.elkdocs.handwritter.util

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditEvent
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Math.min
import java.text.SimpleDateFormat
import java.util.Locale

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

    fun createBitmapFromCanvas(canvas: Canvas): Bitmap {
        val bitmap = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
        val canvasBitmap = Canvas(bitmap)
        canvasBitmap.drawBitmap(bitmap, 0f, 0f, null)
        return bitmap
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

  fun spToPx(sp: Float, context: Context): Int {
      return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toInt()
  }

    private fun pickDate(callback: (dateTime: String) -> Unit,context: Context) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, { _, year, month, day ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, day)
            val dateTime = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(selectedCalendar.time)
            callback(dateTime) // call the callback function after completing the work
        }, currentYear, currentMonth, currentDay)

        datePickerDialog.show()
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



    @SuppressLint("ClickableViewAccessibility")
    fun TextTouchListener(
        view: View,
        parentView: View,
        updatePositionCallback: (Float, Float) -> Unit
    ){
        var offsetX = 0f
        var offsetY = 0f

        view.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    offsetX = event.rawX - v.x
                    offsetY = event.rawY - v.y
                   val startX = v.x
                   val startY = v.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX - offsetX
                    val newY = event.rawY - offsetY

                    // Calculate the boundaries based on the parent view's dimensions
                    val minX = 0f
                    val maxX = parentView.width - v.width
                    val minY = 0f
                    val maxY = parentView.height - v.height

                    // Constrain the new coordinates within the boundaries
                    val constrainedX = newX.coerceIn(minX, maxX.toFloat())
                    val constrainedY = newY.coerceIn(minY, maxY.toFloat())

                    v.x = constrainedX
                    v.y = constrainedY
                }
                MotionEvent.ACTION_UP -> {

                    updatePositionCallback(v.x + 50f,v.y)
                    // Implement any additional logic after dragging ends
                }
            }
            true
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
}