package xyz.appmaker.pbyvul.data.api.models

import com.squareup.moshi.Json

data class LeagueResponse(
    @Json(name = "league") val league: LeagueInfo? = null,
    @Json(name = "country") val country: Country? = null,
    @Json(name = "seasons") val seasons: List<Season>? = null
)

data class LeagueInfo(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "logo") val logo: String? = null
)

data class Country(
    @Json(name = "name") val name: String? = null,
    @Json(name = "code") val code: String? = null,
    @Json(name = "flag") val flag: String? = null
)

data class Season(
    @Json(name = "year") val year: Int? = null,
    @Json(name = "start") val start: String? = null,
    @Json(name = "end") val end: String? = null,
    @Json(name = "current") val current: Boolean? = null
)
