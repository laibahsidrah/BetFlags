package xyz.appmaker.pbyvul.data.api.models

import com.squareup.moshi.Json

data class FixtureResponse(
    @Json(name = "fixture") val fixture: FixtureInfo? = null,
    @Json(name = "league") val league: LeagueCompact? = null,
    @Json(name = "teams") val teams: TeamsHolder? = null,
    @Json(name = "goals") val goals: Goals? = null,
    @Json(name = "score") val score: Score? = null
)

data class FixtureInfo(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "referee") val referee: String? = null,
    @Json(name = "timezone") val timezone: String? = null,
    @Json(name = "date") val date: String? = null,
    @Json(name = "timestamp") val timestamp: Long? = null,
    @Json(name = "periods") val periods: Periods? = null,
    @Json(name = "venue") val venue: Venue? = null,
    @Json(name = "status") val status: FixtureStatus? = null
)

data class Periods(
    @Json(name = "first") val first: Long? = null,
    @Json(name = "second") val second: Long? = null
)

data class Venue(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "city") val city: String? = null
)

data class FixtureStatus(
    @Json(name = "long") val long: String? = null,
    @Json(name = "short") val short: String? = null,
    @Json(name = "elapsed") val elapsed: Int? = null
)

data class LeagueCompact(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "logo") val logo: String? = null,
    @Json(name = "flag") val flag: String? = null,
    @Json(name = "season") val season: Int? = null,
    @Json(name = "round") val round: String? = null
)

data class TeamsHolder(
    @Json(name = "home") val home: TeamCompact? = null,
    @Json(name = "away") val away: TeamCompact? = null
)

data class TeamCompact(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "logo") val logo: String? = null,
    @Json(name = "winner") val winner: Boolean? = null
)

data class Goals(
    @Json(name = "home") val home: Int? = null,
    @Json(name = "away") val away: Int? = null
)

data class Score(
    @Json(name = "halftime") val halftime: Goals? = null,
    @Json(name = "fulltime") val fulltime: Goals? = null,
    @Json(name = "extratime") val extratime: Goals? = null,
    @Json(name = "penalty") val penalty: Goals? = null
)

data class FixtureEvent(
    @Json(name = "time") val time: EventTime? = null,
    @Json(name = "team") val team: TeamCompact? = null,
    @Json(name = "player") val player: PlayerCompact? = null,
    @Json(name = "assist") val assist: PlayerCompact? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "detail") val detail: String? = null,
    @Json(name = "comments") val comments: String? = null
)

data class EventTime(
    @Json(name = "elapsed") val elapsed: Int? = null,
    @Json(name = "extra") val extra: Int? = null
)

data class PlayerCompact(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null
)

data class LineupResponse(
    @Json(name = "team") val team: TeamCompact? = null,
    @Json(name = "coach") val coach: CoachCompact? = null,
    @Json(name = "formation") val formation: String? = null,
    @Json(name = "startXI") val startXI: List<LineupPlayerHolder>? = null,
    @Json(name = "substitutes") val substitutes: List<LineupPlayerHolder>? = null
)

data class CoachCompact(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "photo") val photo: String? = null
)

data class LineupPlayerHolder(
    @Json(name = "player") val player: LineupPlayer? = null
)

data class LineupPlayer(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "number") val number: Int? = null,
    @Json(name = "pos") val pos: String? = null,
    @Json(name = "grid") val grid: String? = null
)

data class FixtureStatisticsResponse(
    @Json(name = "team") val team: TeamCompact? = null,
    @Json(name = "statistics") val statistics: List<StatisticItem>? = null
)

data class StatisticItem(
    @Json(name = "type") val type: String? = null,
    @Json(name = "value") val value: Any? = null
)
