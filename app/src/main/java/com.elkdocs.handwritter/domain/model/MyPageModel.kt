package com.elkdocs.handwritter.domain.model

import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditState
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "my_pages")
data class MyPageModel(
    @PrimaryKey(autoGenerate = true)
    val pageId: Long? = null,
    val folderId: Long? = null,
    val uriIndex: Int,
    val notesText: String,
    val textAlignment : Int,
    val fontStyle: Int,
    val fontType: Int,
    val fontSize: Float,
    val letterSpace: Float,
    val textAndLineSpace: Float,
    val addLines: Boolean,
    val lineColor: Int,
    val pageColor: Int,
    val bitmap: Bitmap,
    val underline : Boolean,
    var isSelected: Boolean = false,
    val date : String = "",
    val dateTextViewX : Float = 0f,
    val dateTextViewY: Float = 0f

    //val underlineSpans: MutableList<Pair<Int, Int>> = mutableListOf()
    ) : Parcelable{
    companion object {
        fun fromMyPageModel(pageDetail: MyPageModel): PageEditState {
            return PageEditState(
                pageId = pageDetail.pageId,
                folderId = pageDetail.folderId,
                uriIndex = pageDetail.uriIndex,
                notesText = pageDetail.notesText,
                textAlignment = pageDetail.textAlignment,
                fontStyle = pageDetail.fontStyle,
                letterSpace = pageDetail.letterSpace,
                fontSize = pageDetail.fontSize,
                textAndLineSpace = pageDetail.textAndLineSpace,
                addLines = pageDetail.addLines,
                lineColor = pageDetail.lineColor,
                pageBitmap = pageDetail.bitmap
            )
        }
    }
}
