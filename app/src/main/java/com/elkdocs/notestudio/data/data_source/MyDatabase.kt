package com.elkdocs.notestudio.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.elkdocs.notestudio.domain.model.MyFolderModel
import com.elkdocs.notestudio.domain.model.MyPageModel

@Database(
    entities = [MyFolderModel::class,MyPageModel::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MyDatabase : RoomDatabase(){

    abstract fun myFolderDao() : MyDao
    companion object{
        const val DATABASE_NAME = "my_db"
    }
}