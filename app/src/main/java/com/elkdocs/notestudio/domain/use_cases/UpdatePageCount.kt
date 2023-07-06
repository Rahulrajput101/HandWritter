package com.elkdocs.notestudio.domain.use_cases

import com.elkdocs.notestudio.domain.repository.MyRepository
import javax.inject.Inject

class UpdatePageCount @Inject constructor(
    val repository: MyRepository
){
    suspend operator fun invoke(folderId : Long, totalPages : Int? = null){
        val folder = repository.getMyFolder(folderId)
        if(totalPages != null){
            //for decrement the pageCount
             val pageCount = folder.pageCount - totalPages
            repository.updateFolderPageCount(folderId,pageCount)
        }else{
            val pageCount = folder.pageCount + 1
            repository.updateFolderPageCount(folderId , pageCount)
        }
    }
}