package com.elkdocs.handwritter.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val fontSize: String,
    val charSpace: String,
    val wordSpace: String,
    val addHrLines: String,
    val addVrLines: String,
    val lineColor: String
) : Parcelable
