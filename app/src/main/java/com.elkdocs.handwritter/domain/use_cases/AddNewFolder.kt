package com.elkdocs.handwritter.domain.use_cases

import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.repository.MyRepository
import javax.inject.Inject

class AddNewFolder @Inject constructor(
    private val repository: MyRepository
) {
    suspend operator fun invoke(folder: MyFolderModel): Long {
        return repository.addMyFolder(folder)
    }
}