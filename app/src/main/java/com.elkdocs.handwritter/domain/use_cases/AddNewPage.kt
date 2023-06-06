package com.elkdocs.handwritter.domain.use_cases

import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.repository.MyRepository
import javax.inject.Inject

class AddNewPage @Inject constructor(
    private val repository: MyRepository
) {
    
    suspend operator fun invoke(page: MyPageModel) {
        repository.addMyPage(page)

    }
    
}