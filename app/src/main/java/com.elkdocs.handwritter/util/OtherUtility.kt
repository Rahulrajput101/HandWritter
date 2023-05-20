package com.elkdocs.handwritter.util

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException
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




}