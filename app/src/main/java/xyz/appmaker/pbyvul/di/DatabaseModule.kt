package xyz.appmaker.pbyvul.di

import android.content.Context
import androidx.room.Room
import xyz.appmaker.pbyvul.data.local.BetFlagsDatabase
import xyz.appmaker.pbyvul.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BetFlagsDatabase =
        Room.databaseBuilder(
            context,
            BetFlagsDatabase::class.java,
            "betflags_db"
        ).build()

    @Provides
    @Singleton
    fun provideFavoriteDao(db: BetFlagsDatabase): FavoriteDao = db.favoriteDao()
}
