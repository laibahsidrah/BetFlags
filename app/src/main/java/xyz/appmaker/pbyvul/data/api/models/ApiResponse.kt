package xyz.appmaker.pbyvul.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class ApiResponse<T>(
    @Json(name = "get") val get: String? = null,
    @Json(name = "parameters") val parameters: Map<String, String>? = null,
    @Json(name = "errors") val errors: Any? = null,
    @Json(name = "results") val results: Int? = null,
    @Json(name = "paging") val paging: Paging? = null,
    @Json(name = "response") val response: T? = null
)

data class Paging(
    @Json(name = "current") val current: Int? = null,
    @Json(name = "total") val total: Int? = null
)
