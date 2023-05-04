package com.elkdocs.handwritter.presentation.page_edit_screen

import android.graphics.Color
import android.graphics.Typeface
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_LIGHT_BEIGE
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_LIGHT_GRAY
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_OFF_WHITE
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_PALE_BLUE
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_PALE_LAVENDER

data class PageEditState(
    val pageId: Long? = null,
    val folderId: Long? = null,
    val uriIndex: Int =0,
    val notesText: String ="",
    val fontStyle: Int = R.font.zeyada_regular,
    val fontSize: Float= 20f,
    val fontType : Int = Typeface.NORMAL,
    val charSpace: String = "",
    val wordSpace: String = "",
    val addLines: Boolean = true,
    val lineColor: Int = Color.BLACK
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
