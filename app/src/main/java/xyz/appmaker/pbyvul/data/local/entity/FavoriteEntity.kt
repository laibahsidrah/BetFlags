package xyz.appmaker.pbyvul.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_teams")
data class FavoriteTeamEntity(
    @PrimaryKey val teamId: Int,
    val name: String,
    val logo: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_leagues")
data class FavoriteLeagueEntity(
    @PrimaryKey val leagueId: Int,
    val name: String,
    val logo: String,
    val country: String,
    val countryFlag: String?,
    val currentSeason: Int,
    val addedAt: Long = System.currentTimeMillis()
)
