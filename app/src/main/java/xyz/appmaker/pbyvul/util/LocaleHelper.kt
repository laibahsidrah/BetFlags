package xyz.appmaker.pbyvul.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleHelper {

    private val languageToDefaultCountry = mapOf(
        "ru" to "RU",
        "en" to "US",
        "es" to "ES",
        "it" to "IT",
        "de" to "DE",
        "fr" to "FR",
        "pt" to "BR",
        "ja" to "JP",
        "ko" to "KR",
        "zh" to "CN",
        "ar" to "SA",
        "hi" to "IN",
        "tr" to "TR",
        "pl" to "PL",
        "nl" to "NL",
        "sv" to "SE",
        "da" to "DK",
        "no" to "NO",
        "fi" to "FI",
        "cs" to "CZ",
        "el" to "GR",
        "hu" to "HU",
        "ro" to "RO",
        "uk" to "UA",
        "th" to "TH",
        "vi" to "VN",
        "id" to "ID",
        "ms" to "MY",
        "he" to "IL",
        "fa" to "IR",
        "bg" to "BG",
        "hr" to "HR",
        "sk" to "SK",
        "sl" to "SI",
        "sr" to "RS",
        "lt" to "LT",
        "lv" to "LV",
        "et" to "EE",
        "sq" to "AL",
        "mk" to "MK",
        "bs" to "BA",
        "ka" to "GE",
        "hy" to "AM",
        "az" to "AZ",
        "kk" to "KZ",
        "uz" to "UZ",
        "tg" to "TJ",
        "ky" to "KG",
        "mn" to "MN",
        "be" to "BY",
        "bn" to "BD",
        "ta" to "IN",
        "te" to "IN",
        "ml" to "IN",
        "kn" to "IN",
        "gu" to "IN",
        "mr" to "IN",
        "pa" to "IN",
        "ne" to "NP",
        "si" to "LK",
        "my" to "MM",
        "km" to "KH",
        "lo" to "LA",
        "sw" to "KE",
        "am" to "ET",
        "ur" to "PK",
        "fil" to "PH",
        "ca" to "AD"
    )

    fun getDeviceLanguage(): String {
        return Locale.getDefault().language
    }

    fun getDeviceCountryCode(): String {
        return Locale.getDefault().country
    }

    fun getDefaultCountryForLanguage(languageCode: String): String {
        return languageToDefaultCountry[languageCode] ?: "US"
    }

    fun getCountryForDevice(): Country {
        val deviceLanguage = getDeviceLanguage()

        val defaultCountryCode = getDefaultCountryForLanguage(deviceLanguage)
        var country = CountryData.getCountryByIsoCode(defaultCountryCode)

        if (country == null) {
            val deviceCountry = getDeviceCountryCode()
            country = CountryData.getCountryByIsoCode(deviceCountry)
        }

        return country ?: CountryData.getCountryByIsoCode("US")!!
    }

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    fun updateResources(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun wrapContext(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        }

        return context.createConfigurationContext(config)
    }

    fun isRussianLocale(languageCode: String): Boolean {
        return languageCode.equals("ru", ignoreCase = true)
    }

    fun isEnglishLocale(languageCode: String): Boolean {
        return languageCode.equals("en", ignoreCase = true)
    }

    fun getSupportedLanguageCodes(): List<String> {
        return languageToDefaultCountry.keys.toList()
    }
}
