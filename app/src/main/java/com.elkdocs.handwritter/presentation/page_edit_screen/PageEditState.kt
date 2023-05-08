package com.elkdocs.handwritter.presentation.page_edit_screen

import android.graphics.Color
import android.graphics.Typeface
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.util.Constant.BLUE_LINE_COLOR
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_LIGHT_BEIGE
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_LIGHT_GRAY
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_OFF_WHITE
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_PALE_BLUE
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_PALE_LAVENDER
import com.google.mlkit.nl.translate.TranslateLanguage

data class PageEditState(
    val pageId: Long? = null,
    val folderId: Long? = null,
    val uriIndex: Int =0,
    val notesText: String ="",
    val fontStyle: Int = R.font.caveat_variablefont_wght,
    val fontSize: Float= 20f,
    val fontType : Int = Typeface.NORMAL,
    val letterSpace: Float = 3f,
    val wordSpace: String = "",
    val addLines: Boolean = true,
    val lineColor: Int = BLUE_LINE_COLOR,
    val pageColor: Int = PAGE_COLOR_LIGHT_BEIGE,

) {
    companion object{
        val pageColorList = mutableListOf(
            PAGE_COLOR_LIGHT_BEIGE,
            PAGE_COLOR_LIGHT_GRAY,
            PAGE_COLOR_OFF_WHITE,
            PAGE_COLOR_PALE_BLUE,
            PAGE_COLOR_PALE_LAVENDER
        )
    }
}
