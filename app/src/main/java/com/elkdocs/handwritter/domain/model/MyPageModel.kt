package com.elkdocs.handwritter.domain.model

import android.graphics.Typeface
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditState
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "my_pages")
data class MyPageModel(
    @PrimaryKey(autoGenerate = true)
    val pageId: Long? = null,
    val folderId: Long? = null,
    val uriIndex: Int,
    val notesText: String,
    val fontStyle: Int,
    val fontType: Int ,
    val fontSize: Float,
    val charSpace: String,
    val wordSpace: String,
    val addLines: Boolean,
    val lineColor: Int,
    val pageColor: Int,
) : Parcelable{
    companion object {
        fun fromMyPageModel(pageDetail: MyPageModel): PageEditState {
            return PageEditState(
                pageId = pageDetail.pageId,
                folderId = pageDetail.folderId,
                uriIndex = pageDetail.uriIndex,
                notesText = pageDetail.notesText,
                fontStyle = pageDetail.fontStyle,
                charSpace = pageDetail.charSpace,
                fontSize = pageDetail.fontSize,
                wordSpace = pageDetail.wordSpace,
                addLines = pageDetail.addLines,
                lineColor = pageDetail.lineColor
            )
        }
    }
}
