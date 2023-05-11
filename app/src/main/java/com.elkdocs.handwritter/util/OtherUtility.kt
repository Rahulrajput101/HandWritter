package com.elkdocs.handwritter.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.IOException

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



}