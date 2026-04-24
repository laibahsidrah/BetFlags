package xyz.appmaker.pbyvul.data.api.models

import com.squareup.moshi.Json

data class StandingsResponse(
    @Json(name = "league") val league: StandingsLeague? = null
)

data class StandingsLeague(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "logo") val logo: String? = null,
    @Json(name = "flag") val flag: String? = null,
    @Json(name = "season") val season: Int? = null,
    @Json(name = "standings") val standings: List<List<StandingEntry>>? = null
)

data class StandingEntry(
    @Json(name = "rank") val rank: Int? = null,
    @Json(name = "team") val team: TeamCompact? = null,
    @Json(name = "points") val points: Int? = null,
    @Json(name = "goalsDiff") val goalsDiff: Int? = null,
    @Json(name = "group") val group: String? = null,
    @Json(name = "form") val form: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "all") val all: StandingStats? = null,
    @Json(name = "home") val home: StandingStats? = null,
    @Json(name = "away") val away: StandingStats? = null,
    @Json(name = "update") val update: String? = null
)

data class StandingStats(
    @Json(name = "played") val played: Int? = null,
    @Json(name = "win") val win: Int? = null,
    @Json(name = "draw") val draw: Int? = null,
    @Json(name = "lose") val lose: Int? = null,
    @Json(name = "goals") val goals: StandingGoals? = null
)

data class StandingGoals(
    @Json(name = "for") val goalsFor: Int? = null,
    @Json(name = "against") val goalsAgainst: Int? = null
)
