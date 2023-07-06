package com.elkdocs.notestudio.presentation.folder_screen

import com.elkdocs.notestudio.domain.model.MyFolderModel


data class FolderState(
    val folderListWithPages : List<MyFolderModel> = emptyList(),
)
