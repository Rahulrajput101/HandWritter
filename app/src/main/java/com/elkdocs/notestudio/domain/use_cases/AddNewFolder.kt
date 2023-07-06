package com.elkdocs.notestudio.domain.use_cases

import com.elkdocs.notestudio.domain.model.MyFolderModel
import com.elkdocs.notestudio.domain.repository.MyRepository
import javax.inject.Inject

class AddNewFolder @Inject constructor(
    private val repository: MyRepository
) {
    suspend operator fun invoke(folder: MyFolderModel): Long {
        return repository.addMyFolder(folder)
    }
}