package com.elkdocs.notestudio.domain.repository


import android.graphics.Bitmap
import com.elkdocs.notestudio.domain.model.MyFolderModel
import com.elkdocs.notestudio.domain.model.MyPageModel
import kotlinx.coroutines.flow.Flow

interface MyRepository {

    suspend fun addMyFolder(myFolderModel: MyFolderModel) : Long
    
    fun getAllFolder(): Flow<List<MyFolderModel>>

    suspend fun updateMyFolder(myFolderModel: MyFolderModel)

    suspend fun deleteMyFolder(myFolderModel: MyFolderModel)

    suspend fun getMyFolder(id: Long): MyFolderModel


    suspend fun getMyFolderByName(folderName: String): MyFolderModel?


    suspend fun addMyPage(myPageModel: MyPageModel) : Long
    suspend fun deleteMyPage(myPageModel: MyPageModel)

    fun  getAllPages(folderId : Long) : Flow<List<MyPageModel>>
    fun getPages() : Flow<List<MyPageModel>>
    suspend fun deleteMyFolderWithPages(folderId : Long)

    suspend fun updateFolderIcon(folderId: Long, folderIcon : Bitmap)

    suspend fun updateFolderPageCount(folderId: Long, pageCount: Int)

   suspend fun getPageById(pageId : Long) : MyPageModel

   suspend fun updateFolderName(folderName : String, folderId : Long)

   fun searchFolderByName(folderName: String) : Flow<List<MyFolderModel>>


}