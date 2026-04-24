package xyz.appmaker.pbyvul.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.appmaker.pbyvul.data.local.dao.FavoriteDao
import xyz.appmaker.pbyvul.data.local.entity.FavoriteLeagueEntity
import xyz.appmaker.pbyvul.data.local.entity.FavoriteTeamEntity

@Database(
    entities = [FavoriteTeamEntity::class, FavoriteLeagueEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BetFlagsDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
