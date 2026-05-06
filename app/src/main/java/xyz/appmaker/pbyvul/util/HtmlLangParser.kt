package xyz.appmaker.pbyvul.util

object HtmlLangParser {

    private val htmlLangRegex = Regex(
        """<html[^>]*\blang\s*=\s*["']?([a-zA-Z]{2,3}(?:-[a-zA-Z]{2,4})?)["']?""",
        RegexOption.IGNORE_CASE
    )

    fun parseRootHtmlLang(html: String): String? {
        val match = htmlLangRegex.find(html) ?: return null
        val raw = match.groupValues.getOrNull(1)?.trim()?.lowercase() ?: return null
        return raw.substringBefore('-').substringBefore('_')
    }
}
