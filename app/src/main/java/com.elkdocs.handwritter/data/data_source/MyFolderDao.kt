package com.elkdocs.handwritter.data.data_source

import androidx.room.*
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MyFolderDao {
    
    @Upsert
    suspend fun addMyFolder(myFolderModel: MyFolderModel): Long
    
    @Delete
    suspend fun deleteMyFolder(myFolderModel: MyFolderModel)
    
    
    @Query("Select * from my_folders")
    fun getAllFolder(): Flow<List<MyFolderModel>>
    
    @Query("SELECT * FROM my_folders WHERE folderId = :id")
    suspend fun getMyFolder(id: Long): MyFolderModel
    
    @Query("SELECT * FROM my_folders WHERE folderName = :folderName")
    suspend fun getMyFolderByName(folderName: String): MyFolderModel
    
    @Upsert
    suspend fun addMyPage(myPageModel: MyPageModel): Long
    
    @Delete
    suspend fun deleteMyPage(myPageModel: MyPageModel)

    @Query("DELETE FROM my_pages WHERE folderId = :folderId")
    suspend fun deletePagesByFolderId(folderId: Long)
    
    @Query("SELECT * FROM my_pages WHERE folderId = :folderId")
    fun getAllPages(folderId: Long): Flow<List<MyPageModel>>

    @Query("SELECT * FROM  my_pages WHERE pageId = :pageId")
    suspend fun getPageById(pageId : Long): MyPageModel

    @Query("SELECT * FROM my_pages")
    fun getPages() : Flow<List<MyPageModel>>

    @Transaction
    suspend fun deleteMyFolderWithPages(folderId: Long) {
        // Delete all pages associated with the folder
        deletePagesByFolderId(folderId)
        // Delete the folder itself
        // Delete the folder itself
        val folder = getMyFolder(folderId)
        deleteMyFolder(folder)

    }
    @Query("UPDATE my_folders SET pageCount = :pageCount WHERE folderId = :folderId")
    suspend fun updateFolderPageCount(folderId: Long, pageCount: Int)


    
}