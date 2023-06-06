package com.elkdocs.handwritter.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel

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