package com.elkdocs.handwritter.presentation.folder_screen

import com.elkdocs.handwritter.domain.model.MyFolderModel


data class FolderState(
    val folderListWithPages : List<MyFolderModel> = emptyList()
)
