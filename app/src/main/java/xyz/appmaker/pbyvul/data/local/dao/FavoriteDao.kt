package xyz.appmaker.pbyvul.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xyz.appmaker.pbyvul.data.local.entity.FavoriteLeagueEntity
import xyz.appmaker.pbyvul.data.local.entity.FavoriteTeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_teams ORDER BY addedAt DESC")
    fun getAllFavoriteTeams(): Flow<List<FavoriteTeamEntity>>

    @Query("SELECT * FROM favorite_leagues ORDER BY addedAt DESC")
    fun getAllFavoriteLeagues(): Flow<List<FavoriteLeagueEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_teams WHERE teamId = :teamId)")
    fun isTeamFavorite(teamId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_leagues WHERE leagueId = :leagueId)")
    fun isLeagueFavorite(leagueId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTeam(team: FavoriteTeamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteLeague(league: FavoriteLeagueEntity)

    @Delete
    suspend fun deleteFavoriteTeam(team: FavoriteTeamEntity)

    @Delete
    suspend fun deleteFavoriteLeague(league: FavoriteLeagueEntity)

    @Query("DELETE FROM favorite_teams WHERE teamId = :teamId")
    suspend fun deleteFavoriteTeamById(teamId: Int)

    @Query("DELETE FROM favorite_leagues WHERE leagueId = :leagueId")
    suspend fun deleteFavoriteLeagueById(leagueId: Int)

    @Query("DELETE FROM favorite_teams")
    suspend fun deleteAllFavoriteTeams()

    @Query("DELETE FROM favorite_leagues")
    suspend fun deleteAllFavoriteLeagues()
}
