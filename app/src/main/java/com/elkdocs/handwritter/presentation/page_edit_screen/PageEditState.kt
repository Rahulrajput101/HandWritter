package com.elkdocs.handwritter.presentation.page_edit_screen

data class PageEditState(
    val pageId: Long? = null,
    val folderId: Long? = null,
    val uriIndex: Int =0,
    val notesText: String ="",
    val fontStyle: Int = 0,
    val fontSize: String = "",
    val charSpace: String = "",
    val wordSpace: String = "",
    val addHrLines: String = "",
    val addVrLines: String = "",
    val lineColor: String = ""
)
