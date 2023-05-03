package com.elkdocs.handwritter.presentation.page_edit_screen

import android.graphics.Color
import android.graphics.Typeface
import com.elkdocs.handwritter.R

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
)
