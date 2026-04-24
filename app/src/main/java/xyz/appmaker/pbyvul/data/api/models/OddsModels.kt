package xyz.appmaker.pbyvul.data.api.models

import com.squareup.moshi.Json

data class OddsResponse(
    @Json(name = "league") val league: LeagueCompact? = null,
    @Json(name = "fixture") val fixture: FixtureInfo? = null,
    @Json(name = "update") val update: String? = null,
    @Json(name = "bookmakers") val bookmakers: List<Bookmaker>? = null
)

data class Bookmaker(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "bets") val bets: List<Bet>? = null
)

data class Bet(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "values") val values: List<BetValue>? = null
)

data class BetValue(
    @Json(name = "value") val value: String? = null,
    @Json(name = "odd") val odd: String? = null
)

data class PredictionResponse(
    @Json(name = "predictions") val predictions: Prediction? = null,
    @Json(name = "league") val league: LeagueCompact? = null,
    @Json(name = "teams") val teams: PredictionTeams? = null,
    @Json(name = "comparison") val comparison: Map<String, ComparisonItem>? = null,
    @Json(name = "h2h") val h2h: List<FixtureResponse>? = null
)

data class Prediction(
    @Json(name = "winner") val winner: PredictionWinner? = null,
    @Json(name = "win_or_draw") val winOrDraw: Boolean? = null,
    @Json(name = "under_over") val underOver: String? = null,
    @Json(name = "goals") val goals: PredictionGoals? = null,
    @Json(name = "advice") val advice: String? = null,
    @Json(name = "percent") val percent: PredictionPercent? = null
)

data class PredictionWinner(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "comment") val comment: String? = null
)

data class PredictionGoals(
    @Json(name = "home") val home: String? = null,
    @Json(name = "away") val away: String? = null
)

data class PredictionPercent(
    @Json(name = "home") val home: String? = null,
    @Json(name = "draw") val draw: String? = null,
    @Json(name = "away") val away: String? = null
)

data class PredictionTeams(
    @Json(name = "home") val home: PredictionTeamStats? = null,
    @Json(name = "away") val away: PredictionTeamStats? = null
)

data class PredictionTeamStats(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "logo") val logo: String? = null,
    @Json(name = "last_5") val last5: PredictionLast5? = null
)

data class PredictionLast5(
    @Json(name = "form") val form: String? = null,
    @Json(name = "att") val att: String? = null,
    @Json(name = "def") val def: String? = null,
    @Json(name = "goals") val goals: PredictionLast5Goals? = null
)

data class PredictionLast5Goals(
    @Json(name = "for") val goalsFor: PredictionGoalStats? = null,
    @Json(name = "against") val goalsAgainst: PredictionGoalStats? = null
)

data class PredictionGoalStats(
    @Json(name = "total") val total: Int? = null,
    @Json(name = "average") val average: String? = null
)

data class ComparisonItem(
    @Json(name = "home") val home: String? = null,
    @Json(name = "away") val away: String? = null
)
