package xyz.appmaker.pbyvul.util

import android.content.Context
import android.webkit.WebSettings

@Volatile
private var cachedApplicationUserAgent: String? = null

/**
 * Strips embedded-client markers from the client hint string.
 */
fun String.normalizeApplicationUserAgent(): String {
    var userAgentString = this
    userAgentString = userAgentString.replace("; wv", "").replace("Version/4.0", "")
    return userAgentString
}

/**
 * System default client hint, normalized and cached for all HTTP clients (Coil, Retrofit, etc.).
 */
fun Context.resolveApplicationUserAgent(): String {
    cachedApplicationUserAgent?.let { return it }
    synchronized(applicationContext) {
        cachedApplicationUserAgent?.let { return it }
        val ua = WebSettings.getDefaultUserAgent(applicationContext).normalizeApplicationUserAgent()
        cachedApplicationUserAgent = ua
        return ua
    }
}
