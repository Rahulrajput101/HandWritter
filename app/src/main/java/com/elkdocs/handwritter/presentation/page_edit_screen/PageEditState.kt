package com.elkdocs.handwritter.presentation.page_edit_screen

import android.graphics.Color

data class PageEditState(
    val pageId: Long? = null,
    val folderId: Long? = null,
    val uriIndex: Int =0,
    val notesText: String ="",
    val fontStyle: Int? = null,
    val fontSize: Float= 8f,
    val charSpace: String = "",
    val wordSpace: String = "",
    val addLines: Boolean = false,
    val lineColor: Int = Color.BLACK
)
