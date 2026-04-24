package xyz.appmaker.pbyvul.data.api

import xyz.appmaker.pbyvul.data.api.models.*
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApi {

    @GET("fixtures")
    suspend fun getLiveFixtures(
        @Query("live") live: String = "all"
    ): ApiResponse<List<FixtureResponse>>

    @GET("fixtures")
    suspend fun getFixturesByDate(
        @Query("date") date: String
    ): ApiResponse<List<FixtureResponse>>

    @GET("fixtures")
    suspend fun getFixtureById(
        @Query("id") id: Int
    ): ApiResponse<List<FixtureResponse>>

    @GET("fixtures")
    suspend fun getFixturesByTeam(
        @Query("team") teamId: Int,
        @Query("season") season: Int,
        @Query("last") last: Int = 10
    ): ApiResponse<List<FixtureResponse>>

    @GET("fixtures")
    suspend fun getFixturesByLeague(
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): ApiResponse<List<FixtureResponse>>

    @GET("fixtures/events")
    suspend fun getFixtureEvents(
        @Query("fixture") fixtureId: Int
    ): ApiResponse<List<FixtureEvent>>

    @GET("fixtures/lineups")
    suspend fun getFixtureLineups(
        @Query("fixture") fixtureId: Int
    ): ApiResponse<List<LineupResponse>>

    @GET("fixtures/statistics")
    suspend fun getFixtureStatistics(
        @Query("fixture") fixtureId: Int
    ): ApiResponse<List<FixtureStatisticsResponse>>

    @GET("fixtures/headtohead")
    suspend fun getHeadToHead(
        @Query("h2h") h2h: String,
        @Query("last") last: Int = 10
    ): ApiResponse<List<FixtureResponse>>

    @GET("leagues")
    suspend fun getLeagues(
        @Query("current") current: String = "true"
    ): ApiResponse<List<LeagueResponse>>

    @GET("leagues")
    suspend fun getLeagueById(
        @Query("id") id: Int
    ): ApiResponse<List<LeagueResponse>>

    @GET("standings")
    suspend fun getStandings(
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): ApiResponse<List<StandingsResponse>>

    @GET("teams")
    suspend fun getTeamInfo(
        @Query("id") teamId: Int
    ): ApiResponse<List<TeamResponse>>

    @GET("players/squads")
    suspend fun getSquad(
        @Query("team") teamId: Int
    ): ApiResponse<List<SquadResponse>>

    @GET("players")
    suspend fun getPlayers(
        @Query("team") teamId: Int,
        @Query("season") season: Int,
        @Query("page") page: Int = 1
    ): ApiResponse<List<PlayerResponse>>

    @GET("players")
    suspend fun getPlayerById(
        @Query("id") playerId: Int,
        @Query("season") season: Int
    ): ApiResponse<List<PlayerResponse>>

    @GET("players/topscorers")
    suspend fun getTopScorers(
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): ApiResponse<List<PlayerResponse>>

    @GET("players/topassists")
    suspend fun getTopAssists(
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): ApiResponse<List<PlayerResponse>>

    @GET("predictions")
    suspend fun getPredictions(
        @Query("fixture") fixtureId: Int
    ): ApiResponse<List<PredictionResponse>>

    @GET("odds")
    suspend fun getOdds(
        @Query("fixture") fixtureId: Int
    ): ApiResponse<List<OddsResponse>>

    @GET("injuries")
    suspend fun getInjuries(
        @Query("team") teamId: Int,
        @Query("season") season: Int
    ): ApiResponse<List<InjuryResponse>>

    @GET("transfers")
    suspend fun getTransfers(
        @Query("team") teamId: Int
    ): ApiResponse<List<TransferResponse>>

    @GET("coachs")
    suspend fun getCoach(
        @Query("team") teamId: Int
    ): ApiResponse<List<CoachResponse>>
}
