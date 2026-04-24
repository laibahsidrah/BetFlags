package xyz.appmaker.pbyvul.data.repository

import xyz.appmaker.pbyvul.data.api.FootballApi
import xyz.appmaker.pbyvul.data.api.models.*
import xyz.appmaker.pbyvul.data.local.dao.FavoriteDao
import xyz.appmaker.pbyvul.data.local.entity.FavoriteLeagueEntity
import xyz.appmaker.pbyvul.data.local.entity.FavoriteTeamEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FootballRepository @Inject constructor(
    private val api: FootballApi,
    private val favoriteDao: FavoriteDao
) {

    suspend fun getLiveFixtures(): Result<List<FixtureResponse>> = apiCall {
        api.getLiveFixtures()
    }

    suspend fun getFixturesByDate(date: String): Result<List<FixtureResponse>> = apiCall {
        api.getFixturesByDate(date)
    }

    suspend fun getFixtureById(id: Int): Result<FixtureResponse?> = apiCall {
        api.getFixtureById(id)
    }.map { it.firstOrNull() }

    suspend fun getFixturesByTeam(teamId: Int, season: Int): Result<List<FixtureResponse>> = apiCall {
        api.getFixturesByTeam(teamId, season)
    }

    suspend fun getFixtureEvents(fixtureId: Int): Result<List<FixtureEvent>> = apiCall {
        api.getFixtureEvents(fixtureId)
    }

    suspend fun getFixtureLineups(fixtureId: Int): Result<List<LineupResponse>> = apiCall {
        api.getFixtureLineups(fixtureId)
    }

    suspend fun getFixtureStatistics(fixtureId: Int): Result<List<FixtureStatisticsResponse>> = apiCall {
        api.getFixtureStatistics(fixtureId)
    }

    suspend fun getHeadToHead(team1Id: Int, team2Id: Int): Result<List<FixtureResponse>> = apiCall {
        api.getHeadToHead("$team1Id-$team2Id")
    }

    suspend fun getPredictions(fixtureId: Int): Result<PredictionResponse?> = apiCall {
        api.getPredictions(fixtureId)
    }.map { it.firstOrNull() }

    suspend fun getOdds(fixtureId: Int): Result<OddsResponse?> = apiCall {
        api.getOdds(fixtureId)
    }.map { it.firstOrNull() }

    suspend fun getLeagues(): Result<List<LeagueResponse>> = apiCall {
        api.getLeagues()
    }

    suspend fun getStandings(leagueId: Int, season: Int): Result<List<StandingsResponse>> = apiCall {
        api.getStandings(leagueId, season)
    }

    suspend fun getTeamInfo(teamId: Int): Result<TeamResponse?> = apiCall {
        api.getTeamInfo(teamId)
    }.map { it.firstOrNull() }

    suspend fun getSquad(teamId: Int): Result<SquadResponse?> = apiCall {
        api.getSquad(teamId)
    }.map { it.firstOrNull() }

    suspend fun getCoach(teamId: Int): Result<CoachResponse?> = apiCall {
        api.getCoach(teamId)
    }.map { it.firstOrNull() }

    suspend fun getInjuries(teamId: Int, season: Int): Result<List<InjuryResponse>> = apiCall {
        api.getInjuries(teamId, season)
    }

    suspend fun getTransfers(teamId: Int): Result<List<TransferResponse>> = apiCall {
        api.getTransfers(teamId)
    }

    suspend fun getPlayerById(playerId: Int, season: Int): Result<PlayerResponse?> = apiCall {
        api.getPlayerById(playerId, season)
    }.map { it.firstOrNull() }

    suspend fun getTopScorers(leagueId: Int, season: Int): Result<List<PlayerResponse>> = apiCall {
        api.getTopScorers(leagueId, season)
    }

    suspend fun getTopAssists(leagueId: Int, season: Int): Result<List<PlayerResponse>> = apiCall {
        api.getTopAssists(leagueId, season)
    }

    fun getFavoriteTeams(): Flow<List<FavoriteTeamEntity>> = favoriteDao.getAllFavoriteTeams()

    fun getFavoriteLeagues(): Flow<List<FavoriteLeagueEntity>> = favoriteDao.getAllFavoriteLeagues()

    fun isTeamFavorite(teamId: Int): Flow<Boolean> = favoriteDao.isTeamFavorite(teamId)

    fun isLeagueFavorite(leagueId: Int): Flow<Boolean> = favoriteDao.isLeagueFavorite(leagueId)

    suspend fun addFavoriteTeam(teamId: Int, name: String, logo: String) {
        favoriteDao.insertFavoriteTeam(FavoriteTeamEntity(teamId, name, logo))
    }

    suspend fun removeFavoriteTeam(teamId: Int) {
        favoriteDao.deleteFavoriteTeamById(teamId)
    }

    suspend fun addFavoriteLeague(entity: FavoriteLeagueEntity) {
        favoriteDao.insertFavoriteLeague(entity)
    }

    suspend fun removeFavoriteLeague(leagueId: Int) {
        favoriteDao.deleteFavoriteLeagueById(leagueId)
    }

    suspend fun clearAllFavorites() {
        favoriteDao.deleteAllFavoriteTeams()
        favoriteDao.deleteAllFavoriteLeagues()
    }

    private suspend fun <T> apiCall(call: suspend () -> ApiResponse<T>): Result<T> {
        return try {
            val response = call()
            val data = response.response
            if (data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception("Empty response"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
