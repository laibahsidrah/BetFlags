package xyz.appmaker.pbyvul.util

/**
 * API responses sometimes use protocol-relative paths; Android cannot load those as-is.
 */
fun String?.normalizeForImageLoad(): String? {
    val raw = this?.trim().orEmpty()
    if (raw.isEmpty()) return null
    return when {
        raw.startsWith("//") -> "https:$raw"
        else -> raw
    }
}
