package com.elkdocs.handwritter.data.data_source.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel
import kotlinx.parcelize.Parcelize
//
//@Parcelize
//data class FolderWithPages(
//    @Embedded val myFolderModel : MyFolderModel,
//    @Relation(
//        parentColumn = "folderId",
//        entityColumn = "folderId"
//    )
//    val pages : List<MyPageModel>
//) : Parcelable
