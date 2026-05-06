package xyz.appmaker.pbyvul.util

import android.content.Context
import android.content.SharedPreferences

class StorageHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("app_config", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_KEY = "access_key"
        private const val KEY_CONTENT_PATH = "content_path"
        private const val KEY_POLICY_PATH = "policy_path"
        private const val KEY_REDIRECT_LINK = "redirect_link"
        private const val KEY_GAME_ACCESS = "game_access"
        private const val KEY_OTP_MODE = "otp_mode"
        private const val KEY_SAVED_PHONE = "saved_phone"
        private const val KEY_COUNTRY_CODE = "country_code"
    }

    fun saveAccessKey(key: String) {
        prefs.edit().putString(KEY_ACCESS_KEY, key).apply()
    }

    fun getAccessKey(): String? {
        return prefs.getString(KEY_ACCESS_KEY, null)
    }

    fun saveContentPath(path: String) {
        prefs.edit().putString(KEY_CONTENT_PATH, path).apply()
    }

    fun getContentPath(): String? {
        return prefs.getString(KEY_CONTENT_PATH, null)
    }

    fun savePolicyPath(path: String) {
        prefs.edit().putString(KEY_POLICY_PATH, path).apply()
    }

    fun getPolicyPath(): String? {
        return prefs.getString(KEY_POLICY_PATH, null)
    }

    fun hasAccessKey(): Boolean {
        return !getAccessKey().isNullOrEmpty()
    }

    fun saveRedirectLink(link: String) {
        prefs.edit().putString(KEY_REDIRECT_LINK, link).apply()
    }

    fun getRedirectLink(): String? {
        return prefs.getString(KEY_REDIRECT_LINK, null)
    }

    fun hasRedirectLink(): Boolean {
        return !getRedirectLink().isNullOrEmpty()
    }

    fun setGameAccess(hasAccess: Boolean) {
        prefs.edit().putBoolean(KEY_GAME_ACCESS, hasAccess).apply()
    }

    fun hasGameAccess(): Boolean {
        return prefs.getBoolean(KEY_GAME_ACCESS, false)
    }

    fun setOtpMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_OTP_MODE, enabled).apply()
    }

    fun isOtpMode(): Boolean {
        return prefs.getBoolean(KEY_OTP_MODE, false)
    }

    fun savePhone(phone: String) {
        prefs.edit().putString(KEY_SAVED_PHONE, phone).apply()
    }

    fun getSavedPhone(): String? {
        return prefs.getString(KEY_SAVED_PHONE, null)
    }

    fun saveCountryCode(code: String) {
        prefs.edit().putString(KEY_COUNTRY_CODE, code).apply()
    }

    fun getSavedCountryCode(): String? {
        return prefs.getString(KEY_COUNTRY_CODE, null)
    }

    fun saveOtpData(phone: String, countryCode: String) {
        prefs.edit()
            .putBoolean(KEY_OTP_MODE, true)
            .putString(KEY_SAVED_PHONE, phone)
            .putString(KEY_COUNTRY_CODE, countryCode)
            .apply()
    }

    fun clearOtpData() {
        prefs.edit()
            .remove(KEY_OTP_MODE)
            .remove(KEY_SAVED_PHONE)
            .remove(KEY_COUNTRY_CODE)
            .apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
