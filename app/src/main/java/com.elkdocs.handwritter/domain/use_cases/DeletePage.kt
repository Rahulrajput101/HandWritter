package com.elkdocs.handwritter.domain.use_cases

import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.repository.MyFolderRepository
import javax.inject.Inject

class DeletePage @Inject constructor(
    private val repository: MyFolderRepository
){
    suspend operator fun invoke(page : MyPageModel){
        repository.deleteMyPage(page)

        // Increment the folder's page count
        val folder = repository.getMyFolder(page.folderId!!)
        val updatedFolder = folder.copy(pageCount = folder.pageCount + 1)
        repository.updateFolderPageCount(folder.folderId!!,folder.pageCount-1)
    }

}