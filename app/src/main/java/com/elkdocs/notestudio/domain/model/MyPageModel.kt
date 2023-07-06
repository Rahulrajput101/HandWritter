package com.elkdocs.notestudio.domain.model

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elkdocs.notestudio.presentation.page_edit_screen.PageEditState

import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "my_pages")
data class MyPageModel(
    @PrimaryKey(autoGenerate = true)
    val pageId: Long? = null,
    val folderId: Long? = null,
    val pageNumber: String = "",
    val uriIndex: Int,
    val notesText: String,
    val headingText : String,
    val textAlignment : Int,
    val language: String,
    val fontStyle: Int,
    val fontType: Int,
    val fontSize: Float,
    val headingFontType : Int,
    val letterSpace: Float,
    val textAndLineSpace: Float,
    val addLines: Boolean,
    val lineColor: Int,
    val inkColor: Int,
    val pageColor: Int,
    val bitmap: Bitmap,
    val underline : Boolean,
    val headingUnderline : Boolean,
    var isSelected: Boolean = false,
    val date : String = "",
    val dateTextViewX : Float = 0f,
    val dateTextViewY: Float = 0f,
    val headingTextViewX: Float = -1f,
    val headingTextViewY: Float = -1f,
    val isLayoutFlipped : Boolean = false,
    ) : Parcelable{
    companion object {
        fun fromMyPageModel(pageDetail: MyPageModel): PageEditState {
            return PageEditState(
                pageId = pageDetail.pageId,
                folderId = pageDetail.folderId,
                pageNumber = pageDetail.pageNumber,
                uriIndex = pageDetail.uriIndex,
                notesText = pageDetail.notesText,
                headingText = pageDetail.headingText,
                textAlignment = pageDetail.textAlignment,
                fontStyle = pageDetail.fontStyle,
                letterSpace = pageDetail.letterSpace,
                fontSize = pageDetail.fontSize,
                textAndLineSpace = pageDetail.textAndLineSpace,
                addLines = pageDetail.addLines,
                lineColor = pageDetail.lineColor,
                inkColor = pageDetail.inkColor,
                pageColor = pageDetail.pageColor,
                pageBitmap = pageDetail.bitmap,
                headingUnderline = pageDetail.headingUnderline,
                date = pageDetail.date,
                dateTextViewX = pageDetail.dateTextViewX,
                dateTextViewY = pageDetail.dateTextViewY,
                headingTextViewX = pageDetail.headingTextViewX,
                headingTextViewY = pageDetail.headingTextViewY,
                isLayoutFlipped = pageDetail.isLayoutFlipped
            )
        }
    }
}
