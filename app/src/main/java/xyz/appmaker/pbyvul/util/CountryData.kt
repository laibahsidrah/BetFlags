package xyz.appmaker.pbyvul.util

data class PhoneValidationRule(
    val length: Int,
    val minLength: Int = length,
    val maxLength: Int = length,
    val startsWith: List<String> = emptyList(),
    val notStartsWith: List<String> = emptyList()
)

data class Country(
    val isoCode: String,
    val name: String,
    val phoneCode: String,
    val languageCode: String,
    val flagEmoji: String,
    val validationRule: PhoneValidationRule? = null
) {
    fun getDisplayName(): String = "$flagEmoji $name"
    fun getPhonePrefix(): String = phoneCode

    fun isPhoneValid(phoneNumber: String): Boolean {
        val digits = phoneNumber.filter { it.isDigit() }
        val rule = validationRule ?: return digits.length >= 5

        if (digits.length < rule.minLength || digits.length > rule.maxLength) {
            return false
        }

        if (rule.startsWith.isNotEmpty()) {
            val matchesStart = rule.startsWith.any { digits.startsWith(it) }
            if (!matchesStart) return false
        }

        if (rule.notStartsWith.isNotEmpty()) {
            val matchesForbidden = rule.notStartsWith.any { digits.startsWith(it) }
            if (matchesForbidden) return false
        }

        return true
    }

    fun getExpectedLength(): Int = validationRule?.length ?: 10
}

object CountryData {

    val countries: List<Country> = listOf(
        Country("RU", "Россия", "+7", "ru", "🇷🇺", PhoneValidationRule(10, startsWith = listOf("9"))),
        Country("KZ", "Қазақстан", "+7", "kk", "🇰🇿", PhoneValidationRule(10, startsWith = listOf("7"))),
        Country("UA", "Україна", "+380", "uk", "🇺🇦", PhoneValidationRule(9, startsWith = listOf("39", "50", "63", "66", "67", "68", "73", "91", "92", "93", "94", "95", "96", "97", "98", "99"))),
        Country("BY", "Беларусь", "+375", "be", "🇧🇾", PhoneValidationRule(9, startsWith = listOf("25", "29", "33", "44"))),
        Country("UZ", "Oʻzbekiston", "+998", "uz", "🇺🇿", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("TJ", "Тоҷикистон", "+992", "tg", "🇹🇯", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("KG", "Кыргызстан", "+996", "ky", "🇰🇬", PhoneValidationRule(9, startsWith = listOf("5", "7"))),
        Country("TM", "Türkmenistan", "+993", "tk", "🇹🇲", PhoneValidationRule(8, startsWith = listOf("6"))),
        Country("AZ", "Azərbaycan", "+994", "az", "🇦🇿", PhoneValidationRule(9, startsWith = listOf("40", "50", "51", "55", "60", "70", "77", "99"))),
        Country("AM", "Հdelays", "+374", "hy", "🇦🇲", PhoneValidationRule(8, startsWith = listOf("41", "43", "44", "55", "77", "91", "93", "94", "95", "96", "98", "99"))),
        Country("GE", "საქართველო", "+995", "ka", "🇬🇪", PhoneValidationRule(9, startsWith = listOf("5"))),
        Country("MD", "Moldova", "+373", "ro", "🇲🇩", PhoneValidationRule(8, startsWith = listOf("6", "7"))),
        Country("LV", "Latvija", "+371", "lv", "🇱🇻", PhoneValidationRule(8, startsWith = listOf("2"))),
        Country("LT", "Lietuva", "+370", "lt", "🇱🇹", PhoneValidationRule(8, startsWith = listOf("6"))),
        Country("EE", "Eesti", "+372", "et", "🇪🇪", PhoneValidationRule(8, minLength = 7, maxLength = 8, startsWith = listOf("5"))),
        Country("US", "United States", "+1", "en", "🇺🇸", PhoneValidationRule(10, startsWith = listOf("2", "3", "4", "5", "6", "7", "8", "9"))),
        Country("CA", "Canada", "+1", "en", "🇨🇦", PhoneValidationRule(10, startsWith = listOf("2", "3", "4", "5", "6", "7", "8", "9"))),
        Country("GB", "United Kingdom", "+44", "en", "🇬🇧", PhoneValidationRule(10, startsWith = listOf("7"))),
        Country("DE", "Deutschland", "+49", "de", "🇩🇪", PhoneValidationRule(11, minLength = 10, maxLength = 11, startsWith = listOf("15", "16", "17"))),
        Country("FR", "France", "+33", "fr", "🇫🇷", PhoneValidationRule(9, startsWith = listOf("6", "7"))),
        Country("IT", "Italia", "+39", "it", "🇮🇹", PhoneValidationRule(10, minLength = 9, maxLength = 10, startsWith = listOf("3"))),
        Country("ES", "España", "+34", "es", "🇪🇸", PhoneValidationRule(9, startsWith = listOf("6", "7"))),
        Country("PL", "Polska", "+48", "pl", "🇵🇱", PhoneValidationRule(9, startsWith = listOf("45", "50", "51", "53", "57", "60", "66", "69", "72", "73", "78", "79", "88"))),
        Country("TR", "Türkiye", "+90", "tr", "🇹🇷", PhoneValidationRule(10, startsWith = listOf("5"))),
        Country("CN", "中国", "+86", "zh", "🇨🇳", PhoneValidationRule(11, startsWith = listOf("1"))),
        Country("IN", "भारत", "+91", "hi", "🇮🇳", PhoneValidationRule(10, startsWith = listOf("6", "7", "8", "9"))),
        Country("BR", "Brasil", "+55", "pt", "🇧🇷", PhoneValidationRule(11, minLength = 10, maxLength = 11, startsWith = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9"))),
        Country("MX", "México", "+52", "es", "🇲🇽", PhoneValidationRule(10, startsWith = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9"))),
        Country("AE", "الإمارات", "+971", "ar", "🇦🇪", PhoneValidationRule(9, startsWith = listOf("5"))),
        Country("SA", "السعودية", "+966", "ar", "🇸🇦", PhoneValidationRule(9, startsWith = listOf("5"))),
        Country("IL", "ישראל", "+972", "he", "🇮🇱", PhoneValidationRule(9, startsWith = listOf("5"))),
        Country("AU", "Australia", "+61", "en", "🇦🇺", PhoneValidationRule(9, startsWith = listOf("4"))),
        Country("JP", "日本", "+81", "ja", "🇯🇵", PhoneValidationRule(10, startsWith = listOf("70", "80", "90"))),
        Country("KR", "대한민국", "+82", "ko", "🇰🇷", PhoneValidationRule(10, minLength = 9, maxLength = 10, startsWith = listOf("10", "11"))),
        Country("NL", "Nederland", "+31", "nl", "🇳🇱", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("BE", "België", "+32", "nl", "🇧🇪", PhoneValidationRule(9, startsWith = listOf("4"))),
        Country("AT", "Österreich", "+43", "de", "🇦🇹", PhoneValidationRule(11, minLength = 10, maxLength = 12, startsWith = listOf("6"))),
        Country("CH", "Schweiz", "+41", "de", "🇨🇭", PhoneValidationRule(9, startsWith = listOf("7"))),
        Country("CZ", "Česko", "+420", "cs", "🇨🇿", PhoneValidationRule(9, startsWith = listOf("6", "7"))),
        Country("SK", "Slovensko", "+421", "sk", "🇸🇰", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("RO", "România", "+40", "ro", "🇷🇴", PhoneValidationRule(9, startsWith = listOf("7"))),
        Country("BG", "България", "+359", "bg", "🇧🇬", PhoneValidationRule(9, startsWith = listOf("87", "88", "89", "98", "99"))),
        Country("RS", "Србија", "+381", "sr", "🇷🇸", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("HR", "Hrvatska", "+385", "hr", "🇭🇷", PhoneValidationRule(9, minLength = 8, maxLength = 9, startsWith = listOf("9"))),
        Country("SI", "Slovenija", "+386", "sl", "🇸🇮", PhoneValidationRule(8, startsWith = listOf("30", "31", "40", "41", "51", "64", "68", "70", "71"))),
        Country("HU", "Magyarország", "+36", "hu", "🇭🇺", PhoneValidationRule(9, startsWith = listOf("20", "30", "31", "70"))),
        Country("GR", "Ελλάδα", "+30", "el", "🇬🇷", PhoneValidationRule(10, startsWith = listOf("6"))),
        Country("PT", "Portugal", "+351", "pt", "🇵🇹", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("SE", "Sverige", "+46", "sv", "🇸🇪", PhoneValidationRule(9, minLength = 9, maxLength = 10, startsWith = listOf("7"))),
        Country("NO", "Norge", "+47", "no", "🇳🇴", PhoneValidationRule(8, startsWith = listOf("4", "9"))),
        Country("DK", "Danmark", "+45", "da", "🇩🇰", PhoneValidationRule(8, startsWith = listOf("2", "3", "4", "5", "6", "7", "8", "9"))),
        Country("FI", "Suomi", "+358", "fi", "🇫🇮", PhoneValidationRule(10, minLength = 9, maxLength = 10, startsWith = listOf("4", "5"))),
        Country("IE", "Ireland", "+353", "en", "🇮🇪", PhoneValidationRule(9, startsWith = listOf("8"))),
        Country("AF", "افغانستان", "+93", "fa", "🇦🇫", PhoneValidationRule(9, startsWith = listOf("7"))),
        Country("BD", "বাংলাদেশ", "+880", "bn", "🇧🇩", PhoneValidationRule(10, startsWith = listOf("1"))),
        Country("KH", "កម្ពុជា", "+855", "km", "🇰🇭", PhoneValidationRule(9, minLength = 8, maxLength = 9, startsWith = listOf("1", "6", "7", "8", "9"))),
        Country("HK", "香港", "+852", "zh", "🇭🇰", PhoneValidationRule(8, startsWith = listOf("5", "6", "9"))),
        Country("ID", "Indonesia", "+62", "id", "🇮🇩", PhoneValidationRule(11, minLength = 9, maxLength = 11, startsWith = listOf("8"))),
        Country("IR", "ایران", "+98", "fa", "🇮🇷", PhoneValidationRule(10, startsWith = listOf("9"))),
        Country("IQ", "العراق", "+964", "ar", "🇮🇶", PhoneValidationRule(10, startsWith = listOf("7"))),
        Country("JO", "الأردن", "+962", "ar", "🇯🇴", PhoneValidationRule(9, startsWith = listOf("7"))),
        Country("KW", "الكويت", "+965", "ar", "🇰🇼", PhoneValidationRule(8, startsWith = listOf("5", "6", "9"))),
        Country("LA", "ລາວ", "+856", "lo", "🇱🇦", PhoneValidationRule(10, startsWith = listOf("20"))),
        Country("LB", "لبنان", "+961", "ar", "🇱🇧", PhoneValidationRule(8, startsWith = listOf("3", "7", "8"))),
        Country("MO", "澳門", "+853", "zh", "🇲🇴", PhoneValidationRule(8, startsWith = listOf("6"))),
        Country("MY", "Malaysia", "+60", "ms", "🇲🇾", PhoneValidationRule(10, minLength = 9, maxLength = 10, startsWith = listOf("1"))),
        Country("MV", "ދިވެހިރާއްޖެ", "+960", "dv", "🇲🇻", PhoneValidationRule(7, startsWith = listOf("7", "9"))),
        Country("MN", "Монгол", "+976", "mn", "🇲🇳", PhoneValidationRule(8, startsWith = listOf("8", "9"))),
        Country("MM", "မြန်မာ", "+95", "my", "🇲🇲", PhoneValidationRule(9, minLength = 8, maxLength = 9, startsWith = listOf("9"))),
        Country("NP", "नेपाल", "+977", "ne", "🇳🇵", PhoneValidationRule(10, startsWith = listOf("9"))),
        Country("KP", "조선", "+850", "ko", "🇰🇵", PhoneValidationRule(10)),
        Country("OM", "عُمان", "+968", "ar", "🇴🇲", PhoneValidationRule(8, startsWith = listOf("9"))),
        Country("PK", "پاکستان", "+92", "ur", "🇵🇰", PhoneValidationRule(10, startsWith = listOf("3"))),
        Country("PH", "Pilipinas", "+63", "fil", "🇵🇭", PhoneValidationRule(10, startsWith = listOf("9"))),
        Country("QA", "قطر", "+974", "ar", "🇶🇦", PhoneValidationRule(8, startsWith = listOf("3", "5", "6", "7"))),
        Country("SG", "Singapore", "+65", "en", "🇸🇬", PhoneValidationRule(8, startsWith = listOf("8", "9"))),
        Country("LK", "ශ්‍රී ලංකාව", "+94", "si", "🇱🇰", PhoneValidationRule(9, startsWith = listOf("7"))),
        Country("SY", "سوريا", "+963", "ar", "🇸🇾", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("TW", "台灣", "+886", "zh", "🇹🇼", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("TH", "ประเทศไทย", "+66", "th", "🇹🇭", PhoneValidationRule(9, startsWith = listOf("6", "8", "9"))),
        Country("TL", "Timor-Leste", "+670", "pt", "🇹🇱", PhoneValidationRule(8, startsWith = listOf("7"))),
        Country("VN", "Việt Nam", "+84", "vi", "🇻🇳", PhoneValidationRule(9, minLength = 9, maxLength = 10, startsWith = listOf("3", "5", "7", "8", "9"))),
        Country("YE", "اليمن", "+967", "ar", "🇾🇪", PhoneValidationRule(9, startsWith = listOf("7"))),
        Country("BH", "البحرين", "+973", "ar", "🇧🇭", PhoneValidationRule(8, startsWith = listOf("3", "6"))),
        Country("BT", "འབྲུག", "+975", "dz", "🇧🇹", PhoneValidationRule(8, startsWith = listOf("17"))),
        Country("BN", "Brunei", "+673", "ms", "🇧🇳", PhoneValidationRule(7, startsWith = listOf("7", "8"))),
        Country("PS", "فلسطين", "+970", "ar", "🇵🇸", PhoneValidationRule(9, startsWith = listOf("5"))),
        Country("DZ", "الجزائر", "+213", "ar", "🇩🇿", PhoneValidationRule(9, startsWith = listOf("5", "6", "7"))),
        Country("AO", "Angola", "+244", "pt", "🇦🇴", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("BJ", "Bénin", "+229", "fr", "🇧🇯", PhoneValidationRule(8, startsWith = listOf("9"))),
        Country("BW", "Botswana", "+267", "en", "🇧🇼", PhoneValidationRule(8, startsWith = listOf("7"))),
        Country("BF", "Burkina Faso", "+226", "fr", "🇧🇫", PhoneValidationRule(8, startsWith = listOf("5", "6", "7"))),
        Country("BI", "Burundi", "+257", "fr", "🇧🇮", PhoneValidationRule(8, startsWith = listOf("7"))),
        Country("CV", "Cabo Verde", "+238", "pt", "🇨🇻", PhoneValidationRule(7, startsWith = listOf("9"))),
        Country("CM", "Cameroun", "+237", "fr", "🇨🇲", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("CF", "Centrafrique", "+236", "fr", "🇨🇫", PhoneValidationRule(8, startsWith = listOf("7"))),
        Country("TD", "Tchad", "+235", "fr", "🇹🇩", PhoneValidationRule(8, startsWith = listOf("6", "9"))),
        Country("KM", "Comores", "+269", "fr", "🇰🇲", PhoneValidationRule(7, startsWith = listOf("3"))),
        Country("CG", "Congo", "+242", "fr", "🇨🇬", PhoneValidationRule(9, startsWith = listOf("0"))),
        Country("CD", "RD Congo", "+243", "fr", "🇨🇩", PhoneValidationRule(9, startsWith = listOf("8", "9"))),
        Country("CI", "Côte d'Ivoire", "+225", "fr", "🇨🇮", PhoneValidationRule(10, startsWith = listOf("0"))),
        Country("DJ", "Djibouti", "+253", "fr", "🇩🇯", PhoneValidationRule(8, startsWith = listOf("77"))),
        Country("EG", "مصر", "+20", "ar", "🇪🇬", PhoneValidationRule(10, startsWith = listOf("1"))),
        Country("GQ", "Guinea Ecuatorial", "+240", "es", "🇬🇶", PhoneValidationRule(9, startsWith = listOf("2", "5"))),
        Country("ER", "ኤርትራ", "+291", "ti", "🇪🇷", PhoneValidationRule(7, startsWith = listOf("7"))),
        Country("SZ", "Eswatini", "+268", "en", "🇸🇿", PhoneValidationRule(8, startsWith = listOf("7"))),
        Country("ET", "ኢትዮጵያ", "+251", "am", "🇪🇹", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("GA", "Gabon", "+241", "fr", "🇬🇦", PhoneValidationRule(7, startsWith = listOf("0"))),
        Country("GM", "Gambia", "+220", "en", "🇬🇲", PhoneValidationRule(7, startsWith = listOf("7", "9"))),
        Country("GH", "Ghana", "+233", "en", "🇬🇭", PhoneValidationRule(9, startsWith = listOf("2", "5"))),
        Country("GN", "Guinée", "+224", "fr", "🇬🇳", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("GW", "Guiné-Bissau", "+245", "pt", "🇬🇼", PhoneValidationRule(7, startsWith = listOf("9"))),
        Country("KE", "Kenya", "+254", "sw", "🇰🇪", PhoneValidationRule(9, startsWith = listOf("7"))),
        Country("LS", "Lesotho", "+266", "en", "🇱🇸", PhoneValidationRule(8, startsWith = listOf("5", "6"))),
        Country("LR", "Liberia", "+231", "en", "🇱🇷", PhoneValidationRule(9, minLength = 7, maxLength = 9, startsWith = listOf("7", "8"))),
        Country("LY", "ليبيا", "+218", "ar", "🇱🇾", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("MG", "Madagasikara", "+261", "mg", "🇲🇬", PhoneValidationRule(9, startsWith = listOf("3"))),
        Country("MW", "Malawi", "+265", "en", "🇲🇼", PhoneValidationRule(9, startsWith = listOf("8", "9"))),
        Country("ML", "Mali", "+223", "fr", "🇲🇱", PhoneValidationRule(8, startsWith = listOf("6", "7"))),
        Country("MR", "موريتانيا", "+222", "ar", "🇲🇷", PhoneValidationRule(8, startsWith = listOf("2", "3", "4"))),
        Country("MU", "Mauritius", "+230", "en", "🇲🇺", PhoneValidationRule(8, startsWith = listOf("5"))),
        Country("MA", "المغرب", "+212", "ar", "🇲🇦", PhoneValidationRule(9, startsWith = listOf("6", "7"))),
        Country("MZ", "Moçambique", "+258", "pt", "🇲🇿", PhoneValidationRule(9, startsWith = listOf("8"))),
        Country("NA", "Namibia", "+264", "en", "🇳🇦", PhoneValidationRule(9, startsWith = listOf("8"))),
        Country("NE", "Niger", "+227", "fr", "🇳🇪", PhoneValidationRule(8, startsWith = listOf("9"))),
        Country("NG", "Nigeria", "+234", "en", "🇳🇬", PhoneValidationRule(10, startsWith = listOf("7", "8", "9"))),
        Country("RW", "Rwanda", "+250", "rw", "🇷🇼", PhoneValidationRule(9, startsWith = listOf("7"))),
        Country("SN", "Sénégal", "+221", "fr", "🇸🇳", PhoneValidationRule(9, startsWith = listOf("7"))),
        Country("SC", "Seychelles", "+248", "en", "🇸🇨", PhoneValidationRule(7, startsWith = listOf("2"))),
        Country("SL", "Sierra Leone", "+232", "en", "🇸🇱", PhoneValidationRule(8, startsWith = listOf("7"))),
        Country("SO", "Soomaaliya", "+252", "so", "🇸🇴", PhoneValidationRule(8, startsWith = listOf("6"))),
        Country("ZA", "South Africa", "+27", "en", "🇿🇦", PhoneValidationRule(9, startsWith = listOf("6", "7", "8"))),
        Country("SS", "South Sudan", "+211", "en", "🇸🇸", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("SD", "السودان", "+249", "ar", "🇸🇩", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("TZ", "Tanzania", "+255", "sw", "🇹🇿", PhoneValidationRule(9, startsWith = listOf("6", "7"))),
        Country("TG", "Togo", "+228", "fr", "🇹🇬", PhoneValidationRule(8, startsWith = listOf("9"))),
        Country("TN", "تونس", "+216", "ar", "🇹🇳", PhoneValidationRule(8, startsWith = listOf("2", "5", "9"))),
        Country("UG", "Uganda", "+256", "en", "🇺🇬", PhoneValidationRule(9, startsWith = listOf("7"))),
        Country("ZM", "Zambia", "+260", "en", "🇿🇲", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("ZW", "Zimbabwe", "+263", "en", "🇿🇼", PhoneValidationRule(9, startsWith = listOf("7"))),
        Country("AR", "Argentina", "+54", "es", "🇦🇷", PhoneValidationRule(10, startsWith = listOf("9"))),
        Country("BO", "Bolivia", "+591", "es", "🇧🇴", PhoneValidationRule(8, startsWith = listOf("6", "7"))),
        Country("CL", "Chile", "+56", "es", "🇨🇱", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("CO", "Colombia", "+57", "es", "🇨🇴", PhoneValidationRule(10, startsWith = listOf("3"))),
        Country("CR", "Costa Rica", "+506", "es", "🇨🇷", PhoneValidationRule(8, startsWith = listOf("5", "6", "7", "8"))),
        Country("CU", "Cuba", "+53", "es", "🇨🇺", PhoneValidationRule(8, startsWith = listOf("5"))),
        Country("EC", "Ecuador", "+593", "es", "🇪🇨", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("SV", "El Salvador", "+503", "es", "🇸🇻", PhoneValidationRule(8, startsWith = listOf("6", "7"))),
        Country("GT", "Guatemala", "+502", "es", "🇬🇹", PhoneValidationRule(8, startsWith = listOf("3", "4", "5"))),
        Country("HT", "Haïti", "+509", "fr", "🇭🇹", PhoneValidationRule(8, startsWith = listOf("3", "4"))),
        Country("HN", "Honduras", "+504", "es", "🇭🇳", PhoneValidationRule(8, startsWith = listOf("3", "7", "8", "9"))),
        Country("NI", "Nicaragua", "+505", "es", "🇳🇮", PhoneValidationRule(8, startsWith = listOf("5", "7", "8"))),
        Country("PA", "Panamá", "+507", "es", "🇵🇦", PhoneValidationRule(8, startsWith = listOf("6"))),
        Country("PY", "Paraguay", "+595", "es", "🇵🇾", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("PE", "Perú", "+51", "es", "🇵🇪", PhoneValidationRule(9, startsWith = listOf("9"))),
        Country("UY", "Uruguay", "+598", "es", "🇺🇾", PhoneValidationRule(8, startsWith = listOf("9"))),
        Country("VE", "Venezuela", "+58", "es", "🇻🇪", PhoneValidationRule(10, startsWith = listOf("4"))),
        Country("GY", "Guyana", "+592", "en", "🇬🇾", PhoneValidationRule(7, startsWith = listOf("6"))),
        Country("SR", "Suriname", "+597", "nl", "🇸🇷", PhoneValidationRule(7, startsWith = listOf("7", "8"))),
        Country("NZ", "New Zealand", "+64", "en", "🇳🇿", PhoneValidationRule(9, startsWith = listOf("2"))),
        Country("FJ", "Fiji", "+679", "en", "🇫🇯", PhoneValidationRule(7, startsWith = listOf("7", "8", "9"))),
        Country("PG", "Papua New Guinea", "+675", "en", "🇵🇬", PhoneValidationRule(8, startsWith = listOf("7"))),
        Country("WS", "Samoa", "+685", "sm", "🇼🇸", PhoneValidationRule(7, startsWith = listOf("7"))),
        Country("SB", "Solomon Islands", "+677", "en", "🇸🇧", PhoneValidationRule(7, startsWith = listOf("7", "8"))),
        Country("TO", "Tonga", "+676", "to", "🇹🇴", PhoneValidationRule(7, startsWith = listOf("7", "8"))),
        Country("VU", "Vanuatu", "+678", "bi", "🇻🇺", PhoneValidationRule(7, startsWith = listOf("5", "7"))),
        Country("KI", "Kiribati", "+686", "en", "🇰🇮", PhoneValidationRule(8)),
        Country("MH", "Marshall Islands", "+692", "en", "🇲🇭", PhoneValidationRule(7)),
        Country("FM", "Micronesia", "+691", "en", "🇫🇲", PhoneValidationRule(7)),
        Country("NR", "Nauru", "+674", "en", "🇳🇷", PhoneValidationRule(7, startsWith = listOf("5"))),
        Country("PW", "Palau", "+680", "en", "🇵🇼", PhoneValidationRule(7, startsWith = listOf("7"))),
        Country("TV", "Tuvalu", "+688", "en", "🇹🇻", PhoneValidationRule(6)),
        Country("AL", "Shqipëria", "+355", "sq", "🇦🇱", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("AD", "Andorra", "+376", "ca", "🇦🇩", PhoneValidationRule(6, startsWith = listOf("3", "4", "6"))),
        Country("BA", "Bosna i Hercegovina", "+387", "bs", "🇧🇦", PhoneValidationRule(8, startsWith = listOf("6"))),
        Country("CY", "Κύπρος", "+357", "el", "🇨🇾", PhoneValidationRule(8, startsWith = listOf("9"))),
        Country("IS", "Ísland", "+354", "is", "🇮🇸", PhoneValidationRule(7, startsWith = listOf("6", "7", "8"))),
        Country("XK", "Kosovë", "+383", "sq", "🇽🇰", PhoneValidationRule(8, startsWith = listOf("4"))),
        Country("LI", "Liechtenstein", "+423", "de", "🇱🇮", PhoneValidationRule(7, startsWith = listOf("7"))),
        Country("LU", "Luxembourg", "+352", "fr", "🇱🇺", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("MT", "Malta", "+356", "mt", "🇲🇹", PhoneValidationRule(8, startsWith = listOf("7", "9"))),
        Country("MC", "Monaco", "+377", "fr", "🇲🇨", PhoneValidationRule(8, startsWith = listOf("4", "6"))),
        Country("ME", "Crna Gora", "+382", "sr", "🇲🇪", PhoneValidationRule(8, startsWith = listOf("6"))),
        Country("MK", "Северна Македонија", "+389", "mk", "🇲🇰", PhoneValidationRule(8, startsWith = listOf("7"))),
        Country("SM", "San Marino", "+378", "it", "🇸🇲", PhoneValidationRule(10, startsWith = listOf("3"))),
        Country("VA", "Vaticano", "+39", "it", "🇻🇦", PhoneValidationRule(10, startsWith = listOf("3"))),
        Country("AG", "Antigua and Barbuda", "+1268", "en", "🇦🇬", PhoneValidationRule(7)),
        Country("BS", "Bahamas", "+1242", "en", "🇧🇸", PhoneValidationRule(7)),
        Country("BB", "Barbados", "+1246", "en", "🇧🇧", PhoneValidationRule(7)),
        Country("DM", "Dominica", "+1767", "en", "🇩🇲", PhoneValidationRule(7)),
        Country("DO", "República Dominicana", "+1809", "es", "🇩🇴", PhoneValidationRule(7)),
        Country("GD", "Grenada", "+1473", "en", "🇬🇩", PhoneValidationRule(7)),
        Country("JM", "Jamaica", "+1876", "en", "🇯🇲", PhoneValidationRule(7)),
        Country("KN", "Saint Kitts and Nevis", "+1869", "en", "🇰🇳", PhoneValidationRule(7)),
        Country("LC", "Saint Lucia", "+1758", "en", "🇱🇨", PhoneValidationRule(7)),
        Country("VC", "Saint Vincent", "+1784", "en", "🇻🇨", PhoneValidationRule(7)),
        Country("TT", "Trinidad and Tobago", "+1868", "en", "🇹🇹", PhoneValidationRule(7)),
        Country("BZ", "Belize", "+501", "en", "🇧🇿", PhoneValidationRule(7, startsWith = listOf("6"))),
        Country("ST", "São Tomé e Príncipe", "+239", "pt", "🇸🇹", PhoneValidationRule(7, startsWith = listOf("9"))),
        Country("PR", "Puerto Rico", "+1787", "es", "🇵🇷", PhoneValidationRule(7)),
        Country("GU", "Guam", "+1671", "en", "🇬🇺", PhoneValidationRule(7)),
        Country("VI", "US Virgin Islands", "+1340", "en", "🇻🇮", PhoneValidationRule(7)),
        Country("AS", "American Samoa", "+1684", "en", "🇦🇸", PhoneValidationRule(7)),
        Country("AW", "Aruba", "+297", "nl", "🇦🇼", PhoneValidationRule(7, startsWith = listOf("5", "6", "7", "9"))),
        Country("BM", "Bermuda", "+1441", "en", "🇧🇲", PhoneValidationRule(7)),
        Country("KY", "Cayman Islands", "+1345", "en", "🇰🇾", PhoneValidationRule(7)),
        Country("CW", "Curaçao", "+599", "nl", "🇨🇼", PhoneValidationRule(7, startsWith = listOf("9"))),
        Country("FO", "Føroyar", "+298", "fo", "🇫🇴", PhoneValidationRule(6)),
        Country("GI", "Gibraltar", "+350", "en", "🇬🇮", PhoneValidationRule(8, startsWith = listOf("5", "6"))),
        Country("GL", "Kalaallit Nunaat", "+299", "kl", "🇬🇱", PhoneValidationRule(6)),
        Country("GP", "Guadeloupe", "+590", "fr", "🇬🇵", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("MQ", "Martinique", "+596", "fr", "🇲🇶", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("NC", "Nouvelle-Calédonie", "+687", "fr", "🇳🇨", PhoneValidationRule(6)),
        Country("PF", "Polynésie française", "+689", "fr", "🇵🇫", PhoneValidationRule(6)),
        Country("RE", "La Réunion", "+262", "fr", "🇷🇪", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("SX", "Sint Maarten", "+1721", "nl", "🇸🇽", PhoneValidationRule(7)),
        Country("TC", "Turks and Caicos", "+1649", "en", "🇹🇨", PhoneValidationRule(7)),
        Country("VG", "British Virgin Islands", "+1284", "en", "🇻🇬", PhoneValidationRule(7)),
        Country("AI", "Anguilla", "+1264", "en", "🇦🇮", PhoneValidationRule(7)),
        Country("MS", "Montserrat", "+1664", "en", "🇲🇸", PhoneValidationRule(7)),
        Country("SH", "Saint Helena", "+290", "en", "🇸🇭", PhoneValidationRule(4)),
        Country("PM", "Saint-Pierre-et-Miquelon", "+508", "fr", "🇵🇲", PhoneValidationRule(6)),
        Country("WF", "Wallis-et-Futuna", "+681", "fr", "🇼🇫", PhoneValidationRule(6)),
        Country("YT", "Mayotte", "+262", "fr", "🇾🇹", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("GF", "Guyane française", "+594", "fr", "🇬🇫", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("BL", "Saint-Barthélemy", "+590", "fr", "🇧🇱", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("MF", "Saint-Martin", "+590", "fr", "🇲🇫", PhoneValidationRule(9, startsWith = listOf("6"))),
        Country("IM", "Isle of Man", "+44", "en", "🇮🇲", PhoneValidationRule(10, startsWith = listOf("7"))),
        Country("JE", "Jersey", "+44", "en", "🇯🇪", PhoneValidationRule(10, startsWith = listOf("7"))),
        Country("GG", "Guernsey", "+44", "en", "🇬🇬", PhoneValidationRule(10, startsWith = listOf("7"))),
        Country("AX", "Åland", "+358", "sv", "🇦🇽", PhoneValidationRule(10, startsWith = listOf("4", "5"))),
        Country("CK", "Cook Islands", "+682", "en", "🇨🇰", PhoneValidationRule(5)),
        Country("NU", "Niue", "+683", "en", "🇳🇺", PhoneValidationRule(4)),
        Country("TK", "Tokelau", "+690", "en", "🇹🇰", PhoneValidationRule(4)),
        Country("NF", "Norfolk Island", "+672", "en", "🇳🇫", PhoneValidationRule(6)),
        Country("CX", "Christmas Island", "+61", "en", "🇨🇽", PhoneValidationRule(9, startsWith = listOf("4"))),
        Country("CC", "Cocos Islands", "+61", "en", "🇨🇨", PhoneValidationRule(9, startsWith = listOf("4"))),
        Country("FK", "Falkland Islands", "+500", "en", "🇫🇰", PhoneValidationRule(5)),
        Country("GS", "South Georgia", "+500", "en", "🇬🇸", PhoneValidationRule(5)),
        Country("PN", "Pitcairn", "+64", "en", "🇵🇳", PhoneValidationRule(9, startsWith = listOf("2"))),
        Country("EH", "الصحراء الغربية", "+212", "ar", "🇪🇭", PhoneValidationRule(9, startsWith = listOf("6", "7")))
    )

    fun getCountryByIsoCode(isoCode: String): Country? {
        return countries.find { it.isoCode.equals(isoCode, ignoreCase = true) }
    }

    fun getCountryByLanguageCode(languageCode: String): Country? {
        return countries.find { it.languageCode.equals(languageCode, ignoreCase = true) }
    }

    fun getCountriesByLanguageCode(languageCode: String): List<Country> {
        return countries.filter { it.languageCode.equals(languageCode, ignoreCase = true) }
    }

    fun searchCountries(query: String): List<Country> {
        if (query.isBlank()) return countries
        val lowerQuery = query.lowercase()
        return countries.filter {
            it.name.lowercase().contains(lowerQuery) ||
            it.isoCode.lowercase().contains(lowerQuery) ||
            it.phoneCode.contains(lowerQuery)
        }
    }
}
