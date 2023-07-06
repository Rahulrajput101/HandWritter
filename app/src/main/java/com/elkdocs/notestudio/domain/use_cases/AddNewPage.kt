package com.elkdocs.notestudio.domain.use_cases

import com.elkdocs.notestudio.domain.model.MyPageModel
import com.elkdocs.notestudio.domain.repository.MyRepository
import javax.inject.Inject

class AddNewPage @Inject constructor(
    private val repository: MyRepository
) {

    suspend operator fun invoke(page: MyPageModel): Long {
        return repository.addMyPage(page)
    }
    
}