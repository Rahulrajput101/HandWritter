package com.elkdocs.handwritter.presentation.page_edit_screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.util.Constant.BLUE_LINE_COLOR
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_LIGHT_BEIGE
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_LIGHT_GRAY
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_OFF_WHITE
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_PALE_BLUE
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_PALE_LAVENDER
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_WHITE
import com.google.mlkit.nl.translate.TranslateLanguage
import java.text.SimpleDateFormat
import java.util.Locale

data class PageEditState(
    val pageId: Long? = null,
    val folderId: Long? = null,
    val uriIndex: Int =0,
    val notesText: String ="",
    val fontStyle: Int = R.font.caveat_variablefont_wght,
    val fontSize: Float= 20f,
    val fontType : Int = Typeface.NORMAL,
    val letterSpace: Float = 3f,
    val textAndLineSpace : Float = 10f,
    val addLines: Boolean = true,
    val lineColor: Int = BLUE_LINE_COLOR,
    val pageColor: Int = PAGE_COLOR_LIGHT_BEIGE,
    val underline : Boolean = false,
    val pageBitmap: Bitmap = Bitmap.createBitmap(1024,1485,Bitmap.Config.ARGB_8888),
    val date :String = ""
) {
    companion object{
        val pageColorList = mutableListOf(
            PAGE_COLOR_LIGHT_BEIGE,
            PAGE_COLOR_LIGHT_GRAY,
            PAGE_COLOR_OFF_WHITE,
            PAGE_COLOR_PALE_BLUE,
            PAGE_COLOR_PALE_LAVENDER,
            PAGE_COLOR_WHITE,
        )

        val inputDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
    }
}
