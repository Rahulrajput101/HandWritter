package com.elkdocs.handwritter.presentation.page_edit_screen

import android.graphics.Bitmap
import android.graphics.Canvas
import com.elkdocs.handwritter.domain.model.MyPageModel

sealed interface PageEditEvent{
        object UpdatePage : PageEditEvent
        data class UpdateFontStyle(val fontStyle: Int) : PageEditEvent
        data class UpdateFontType(val fontType: Int) : PageEditEvent
        data class UpdateFontSize(val fontSize: Float) : PageEditEvent
        data class UpdateLetterSpacing(val letterSpacing: Float) : PageEditEvent

        data class UpdateTextAndLineSpacing(val textAndLineSpacing: Float) : PageEditEvent
        data class UpdateAddLine(val addLine : Boolean) : PageEditEvent
        data class UpdateLineColor(val lineColor: Int) : PageEditEvent
        data class UpdatePageColor(val pageColor: Int) : PageEditEvent
        data class UpdateNote(val text : String) : PageEditEvent
        data class UpdateBitmap(val bitmap: Bitmap) : PageEditEvent
        data class UpdateUnderLine(val underLine: Boolean) : PageEditEvent
        data class UpdateDate(val date : String) : PageEditEvent
        data class DrawLine(val canvas: Canvas) : PageEditEvent
}