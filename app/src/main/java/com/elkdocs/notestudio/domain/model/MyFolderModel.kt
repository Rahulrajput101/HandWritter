package com.elkdocs.notestudio.domain.model

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "my_folders")
data class MyFolderModel(
    @PrimaryKey(autoGenerate = true)
    val folderId: Long? = null,
    val folderName: String,
    val folderIcon: Bitmap,
    var pageCount: Int,
    val lastUpdated: Long,
    var isSelected: Boolean = false
) : Parcelable
