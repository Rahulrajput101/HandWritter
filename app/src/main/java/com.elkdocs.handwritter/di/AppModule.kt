package com.elkdocs.handwritter.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.data.data_source.MyDatabase
import com.elkdocs.handwritter.data.repository.MyRepositoryImp
import com.elkdocs.handwritter.domain.repository.MyRepository
import com.elkdocs.handwritter.domain.use_cases.DrawLine
import com.elkdocs.handwritter.util.Constant.APP_THEME_PREF
import com.elkdocs.handwritter.util.Constant.IS_LINEAR
import com.elkdocs.handwritter.util.Constant.PAGE_EDIT_STATE_PREF
import com.elkdocs.handwritter.util.Constant.SHARED_PREFERENCE_NAME
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
    fun provideDrawLine(): DrawLine {
        return DrawLine()
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

