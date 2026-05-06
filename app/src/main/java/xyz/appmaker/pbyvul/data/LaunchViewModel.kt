package xyz.appmaker.pbyvul.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import xyz.appmaker.pbyvul.util.HtmlLangParser
import xyz.appmaker.pbyvul.util.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class LaunchViewModel(application: Application) : AndroidViewModel(application) {

    private val serviceRetryTimeoutsSeconds = listOf(15L, 15L, 30L)

    private val storage = StorageHelper(application)

    private val _startupPhase = MutableStateFlow<StartupPhase>(StartupPhase.CheckingPolicy)
    val startupPhase: StateFlow<StartupPhase> = _startupPhase.asStateFlow()

    private val _launchState = MutableStateFlow<LaunchState>(LaunchState.Loading)
    val launchState: StateFlow<LaunchState> = _launchState.asStateFlow()

    init {
        viewModelScope.launch {
            runPolicyGateThenRoute()
        }
    }

    private suspend fun runPolicyGateThenRoute() {
        _startupPhase.value = StartupPhase.CheckingPolicy
        _launchState.value = LaunchState.Loading

        val html = fetchPrivacyPolicyHtml()
        val lang = html?.let { HtmlLangParser.parseRootHtmlLang(it) }
        val isEnglishPolicy = when {
            html == null -> true
            else -> lang.equals("en", ignoreCase = true)
        }

        _startupPhase.value = StartupPhase.PolicyLoaded(isEnglishPolicy)

        if (isEnglishPolicy) {
            checkInitialState()
        }
    }

    private suspend fun fetchPrivacyPolicyHtml(): String? {
        return withContext(Dispatchers.IO) {
            for (timeoutSec in serviceRetryTimeoutsSeconds) {
                try {
                    val client = OkHttpClient.Builder()
                        .cache(null)
                        .connectTimeout(timeoutSec, TimeUnit.SECONDS)
                        .readTimeout(timeoutSec, TimeUnit.SECONDS)
                        .build()
                    val request = Request.Builder()
                        .url(AppRemoteEndpoints.PRIVACY_POLICY)
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .header("Cache-Control", "no-cache, no-store, must-revalidate")
                        .header("Pragma", "no-cache")
                        .get()
                        .build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (!body.isNullOrEmpty()) {
                            return@withContext body
                        }
                    }
                } catch (_: Exception) {
                }
            }
            null
        }
    }

    private fun checkInitialState() {
        val redirectLink = storage.getRedirectLink()
        if (!redirectLink.isNullOrEmpty()) {
            _launchState.value = LaunchState.Remote(redirectLink)
            return
        }

        if (storage.hasGameAccess()) {
            _launchState.value = LaunchState.Local
            return
        }

        if (storage.isOtpMode()) {
            val countryCode = storage.getSavedCountryCode() ?: ""
            val phone = storage.getSavedPhone() ?: ""
            _launchState.value = LaunchState.OtpWaiting("$countryCode$phone")
            return
        }

        _launchState.value = LaunchState.PhoneEntry
    }

    fun updateState(newState: LaunchState) {
        _launchState.value = newState
    }

    fun getPolicyPath(): String? = storage.getPolicyPath()
}
