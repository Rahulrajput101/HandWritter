package com.elkdocs.handwritter.domain.use_cases

import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.repository.MyFolderRepository
import javax.inject.Inject

class AddNewFolder @Inject constructor(
    private val repository: MyFolderRepository
) {
    suspend operator fun invoke(folder: MyFolderModel): Long {
        return repository.addMyFolder(folder.copy(folderName = System.currentTimeMillis().toString()))
    }
}