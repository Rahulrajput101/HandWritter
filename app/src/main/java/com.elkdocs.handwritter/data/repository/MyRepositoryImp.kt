package com.elkdocs.handwritter.data.repository

import com.elkdocs.handwritter.data.data_source.MyDao
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow

class MyRepositoryImp(
    private val myDao: MyDao,
) : MyRepository {
    override suspend fun addMyFolder(myFolderModel: MyFolderModel) : Long{
        return myDao.addMyFolder(myFolderModel)
    }
    
    override fun getAllFolder(): Flow<List<MyFolderModel>> {
        return myDao.getAllFolder()
    }
    
    override suspend fun updateMyFolder(myFolderModel: MyFolderModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMyFolder(myFolderModel: MyFolderModel) {
    }
    

    override suspend fun getMyFolder(id: Long): MyFolderModel {
       return myDao.getMyFolder(id)
    }

    override suspend fun getMyFolderByName(folderName: String): MyFolderModel? {
        return myDao.getMyFolderByName(folderName)
    }

    override suspend fun addMyPage(myPageModel: MyPageModel): Long {
        return myDao.addMyPage(myPageModel)
    }

    override suspend fun deleteMyPage(myPageModel: MyPageModel) {
        myDao.deleteMyPage(myPageModel)
    }



    override  fun getAllPages(folderId: Long): Flow<List<MyPageModel>> {
      return myDao.getAllPages(folderId)
    }

    override fun getPages(): Flow<List<MyPageModel>> {
        return myDao.getPages()
    }

    override suspend fun deleteMyFolderWithPages(folderId : Long) {
        myDao.deleteMyFolderWithPages(folderId)
    }

    override suspend fun updateFolderPageCount(folderId: Long, pageCount: Int) {
        myDao.updateFolderPageCount(folderId,pageCount)
    }

    override suspend fun getPageById(pageId: Long): MyPageModel {
       return myDao.getPageById(pageId)
    }

    override suspend fun updateFolderName(folderName: String, folderId: Long) {
         myDao.updateFolderName(folderName,folderId)
    }

    override fun searchFolderByName(folderName: String): Flow<List<MyFolderModel>> {
        return myDao.searchFolderByQuery(folderName)
    }


}