package com.elkdocs.handwritter.domain.use_cases

import com.elkdocs.handwritter.domain.repository.MyRepository
import javax.inject.Inject

class DeleteMyFolderWithPages  @Inject constructor(
    private val repository: MyRepository
){
    suspend operator fun invoke(folderId : Long){
        repository.deleteMyFolderWithPages(folderId)
    }
}