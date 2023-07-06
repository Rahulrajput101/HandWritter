package com.elkdocs.notestudio.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.elkdocs.notestudio.data.data_source.MyDatabase
import com.elkdocs.notestudio.data.repository.MyRepositoryImp
import com.elkdocs.notestudio.domain.repository.MyRepository
import com.elkdocs.notestudio.util.Constant.APP_THEME_PREF
import com.elkdocs.notestudio.util.Constant.IS_LINEAR
import com.elkdocs.notestudio.util.Constant.SHARED_PREFERENCE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideMyDatabase(app: Application): MyDatabase {
        return Room.databaseBuilder(
            app,
            MyDatabase::class.java,
            MyDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideMyFolderRepository(db : MyDatabase) : MyRepository {
        return MyRepositoryImp(db.myFolderDao())
    }


    @Provides
    @Singleton
    fun provideSharedPreferneces(
        @ApplicationContext app : Context
    ) = app.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideTypeofRecyclerView(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(IS_LINEAR,true)
    @Provides
    @Singleton
    @Named("theme")
    fun provideAppThemeSharedPreferences(
        @ApplicationContext app: Context
    ): SharedPreferences {
        return app.getSharedPreferences(APP_THEME_PREF, Context.MODE_PRIVATE)
    }


}

