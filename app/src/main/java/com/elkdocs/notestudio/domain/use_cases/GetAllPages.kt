package com.elkdocs.notestudio.domain.use_cases

import com.elkdocs.notestudio.domain.model.MyPageModel
import com.elkdocs.notestudio.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPages @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(folderId : Long?= null) : Flow<List<MyPageModel>> {
        return if(folderId != null){
            repository.getAllPages(folderId)
        }else{
            repository.getPages()
        }

    }
}