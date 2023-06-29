package com.elkdocs.handwritter.presentation.page_edit_screen

import android.graphics.Bitmap


sealed interface PageEditEvent{
        object UpdatePage : PageEditEvent

        data class UpdatePageNumber(val pageNumber: String) : PageEditEvent
        data class UpdateLanguage(val language: String) : PageEditEvent
        data class UpdateFontStyle(val fontStyle: Int) : PageEditEvent
        data class UpdateFontType(val fontType: Int) : PageEditEvent
        data class UpdateHeadingFontType(val headingFontType: Int) : PageEditEvent
        data class UpdateFontSize(val fontSize: Float) : PageEditEvent
        data class UpdateLetterSpacing(val letterSpacing: Float) : PageEditEvent
        data class UpdateTextAndLineSpacing(val textAndLineSpacing: Float) : PageEditEvent

        data class UpdateTextAlignment(val alignment:  Int) : PageEditEvent
        data class UpdateAddLine(val addLine : Boolean) : PageEditEvent
        data class UpdateLineColor(val lineColor: Int) : PageEditEvent
        data class UpdateInkColor(val inkColor: Int) : PageEditEvent
        data class UpdatePageColor(val pageColor: Int) : PageEditEvent
        data class UpdateNote(val text : String) : PageEditEvent
        data class UpdateBitmap(val bitmap: Bitmap) : PageEditEvent
        data class UpdateUnderLine(val underLine: Boolean) : PageEditEvent
        data class UpdateHeadingUnderline(val headingUnderline: Boolean) : PageEditEvent
        data class UpdateDate(val date : String) : PageEditEvent
        data class UpdateLayoutFlipped( val isLayoutFlipped: Boolean) : PageEditEvent
        data class UpdateHeading(val heading : String) : PageEditEvent
        data class UpdateDateTextPosition(val dateTextViewX : Float , val dateTextViewY : Float) : PageEditEvent
        data class UpdateHeadingTextPosition(val headingTextViewX : Float , val headingTextViewY : Float) : PageEditEvent

}