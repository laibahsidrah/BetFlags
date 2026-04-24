package xyz.appmaker.pbyvul.data.api.models

import com.squareup.moshi.Json

data class PlayerResponse(
    @Json(name = "player") val player: PlayerInfo? = null,
    @Json(name = "statistics") val statistics: List<PlayerStatistics>? = null
)

data class PlayerInfo(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "firstname") val firstname: String? = null,
    @Json(name = "lastname") val lastname: String? = null,
    @Json(name = "age") val age: Int? = null,
    @Json(name = "birth") val birth: Birth? = null,
    @Json(name = "nationality") val nationality: String? = null,
    @Json(name = "height") val height: String? = null,
    @Json(name = "weight") val weight: String? = null,
    @Json(name = "injured") val injured: Boolean? = null,
    @Json(name = "photo") val photo: String? = null
)

data class Birth(
    @Json(name = "date") val date: String? = null,
    @Json(name = "place") val place: String? = null,
    @Json(name = "country") val country: String? = null
)

data class PlayerStatistics(
    @Json(name = "team") val team: TeamCompact? = null,
    @Json(name = "league") val league: LeagueCompact? = null,
    @Json(name = "games") val games: PlayerGames? = null,
    @Json(name = "shots") val shots: PlayerShots? = null,
    @Json(name = "goals") val goals: PlayerGoals? = null,
    @Json(name = "passes") val passes: PlayerPasses? = null,
    @Json(name = "tackles") val tackles: PlayerTackles? = null,
    @Json(name = "dribbles") val dribbles: PlayerDribbles? = null,
    @Json(name = "fouls") val fouls: PlayerFouls? = null,
    @Json(name = "cards") val cards: PlayerCards? = null,
    @Json(name = "penalty") val penalty: PlayerPenalty? = null
)

data class PlayerGames(
    @Json(name = "appearences") val appearances: Int? = null,
    @Json(name = "lineups") val lineups: Int? = null,
    @Json(name = "minutes") val minutes: Int? = null,
    @Json(name = "number") val number: Int? = null,
    @Json(name = "position") val position: String? = null,
    @Json(name = "rating") val rating: String? = null,
    @Json(name = "captain") val captain: Boolean? = null
)

data class PlayerShots(
    @Json(name = "total") val total: Int? = null,
    @Json(name = "on") val on: Int? = null
)

data class PlayerGoals(
    @Json(name = "total") val total: Int? = null,
    @Json(name = "conceded") val conceded: Int? = null,
    @Json(name = "assists") val assists: Int? = null,
    @Json(name = "saves") val saves: Int? = null
)

data class PlayerPasses(
    @Json(name = "total") val total: Int? = null,
    @Json(name = "key") val key: Int? = null,
    @Json(name = "accuracy") val accuracy: Int? = null
)

data class PlayerTackles(
    @Json(name = "total") val total: Int? = null,
    @Json(name = "blocks") val blocks: Int? = null,
    @Json(name = "interceptions") val interceptions: Int? = null
)

data class PlayerDribbles(
    @Json(name = "attempts") val attempts: Int? = null,
    @Json(name = "success") val success: Int? = null,
    @Json(name = "past") val past: Int? = null
)

data class PlayerFouls(
    @Json(name = "drawn") val drawn: Int? = null,
    @Json(name = "committed") val committed: Int? = null
)

data class PlayerCards(
    @Json(name = "yellow") val yellow: Int? = null,
    @Json(name = "yellowred") val yellowred: Int? = null,
    @Json(name = "red") val red: Int? = null
)

data class PlayerPenalty(
    @Json(name = "won") val won: Int? = null,
    @Json(name = "commited") val committed: Int? = null,
    @Json(name = "scored") val scored: Int? = null,
    @Json(name = "missed") val missed: Int? = null,
    @Json(name = "saved") val saved: Int? = null
)
