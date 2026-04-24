package xyz.appmaker.pbyvul.data.api.models

import com.squareup.moshi.Json

data class TeamResponse(
    @Json(name = "team") val team: TeamInfo? = null,
    @Json(name = "venue") val venue: VenueInfo? = null
)

data class TeamInfo(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "code") val code: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "founded") val founded: Int? = null,
    @Json(name = "national") val national: Boolean? = null,
    @Json(name = "logo") val logo: String? = null
)

data class VenueInfo(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "capacity") val capacity: Int? = null,
    @Json(name = "surface") val surface: String? = null,
    @Json(name = "image") val image: String? = null
)

data class SquadResponse(
    @Json(name = "team") val team: TeamCompact? = null,
    @Json(name = "players") val players: List<SquadPlayer>? = null
)

data class SquadPlayer(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "age") val age: Int? = null,
    @Json(name = "number") val number: Int? = null,
    @Json(name = "position") val position: String? = null,
    @Json(name = "photo") val photo: String? = null
)

data class CoachResponse(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "firstname") val firstname: String? = null,
    @Json(name = "lastname") val lastname: String? = null,
    @Json(name = "age") val age: Int? = null,
    @Json(name = "nationality") val nationality: String? = null,
    @Json(name = "height") val height: String? = null,
    @Json(name = "weight") val weight: String? = null,
    @Json(name = "photo") val photo: String? = null,
    @Json(name = "team") val team: TeamCompact? = null,
    @Json(name = "career") val career: List<CareerEntry>? = null
)

data class CareerEntry(
    @Json(name = "team") val team: TeamCompact? = null,
    @Json(name = "start") val start: String? = null,
    @Json(name = "end") val end: String? = null
)

data class InjuryResponse(
    @Json(name = "player") val player: InjuredPlayer? = null,
    @Json(name = "team") val team: TeamCompact? = null,
    @Json(name = "fixture") val fixture: FixtureInfo? = null,
    @Json(name = "league") val league: LeagueCompact? = null
)

data class InjuredPlayer(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "photo") val photo: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "reason") val reason: String? = null
)

data class TransferResponse(
    @Json(name = "player") val player: PlayerCompact? = null,
    @Json(name = "update") val update: String? = null,
    @Json(name = "transfers") val transfers: List<Transfer>? = null
)

data class Transfer(
    @Json(name = "date") val date: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "teams") val teams: TransferTeams? = null
)

data class TransferTeams(
    @Json(name = "in") val teamIn: TeamCompact? = null,
    @Json(name = "out") val teamOut: TeamCompact? = null
)
