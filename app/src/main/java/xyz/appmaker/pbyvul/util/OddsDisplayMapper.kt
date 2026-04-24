package xyz.appmaker.pbyvul.util

import xyz.appmaker.pbyvul.data.api.models.Bet
import xyz.appmaker.pbyvul.data.api.models.OddsResponse

data class CompactOddsRow(
    val o1: String,
    val ox: String,
    val o2: String,
    val f1: String,
    val k1: String,
    val f2: String,
    val k2: String,
    val tot: String
) {
    companion object {
        fun empty() = CompactOddsRow(
            o1 = "—",
            ox = "—",
            o2 = "—",
            f1 = "—",
            k1 = "—",
            f2 = "—",
            k2 = "—",
            tot = "—"
        )
    }
}

fun extractCompactOdds(odds: OddsResponse?): CompactOddsRow {
    val bookmaker = odds?.bookmakers?.firstOrNull() ?: return CompactOddsRow.empty()
    val bets = bookmaker.bets.orEmpty()

    val matchWinner = bets.findMatchWinner()
    var o1 = "—"
    var ox = "—"
    var o2 = "—"
    matchWinner?.values.orEmpty().forEach { v ->
        val label = v.value?.lowercase().orEmpty()
        when {
            label == "home" || label.startsWith("home ") -> o1 = v.odd ?: "—"
            label == "draw" -> ox = v.odd ?: "—"
            label == "away" || label.startsWith("away ") -> o2 = v.odd ?: "—"
        }
    }

    val ah = bets.findAsianHandicap()
    var f1 = "—"
    var k1 = "—"
    var f2 = "—"
    var k2 = "—"
    ah?.values.orEmpty().take(2).forEachIndexed { index, v ->
        val hand = extractHandicapLabel(v.value)
        val odd = v.odd ?: "—"
        if (index == 0) {
            f1 = hand
            k1 = odd
        } else {
            f2 = hand
            k2 = odd
        }
    }

    val totals = bets.findGoalsOverUnder()
    val tot = totals?.values.orEmpty()
        .firstOrNull { v ->
            val l = v.value?.lowercase().orEmpty()
            l.contains("over") && (l.contains("2.5") || l.contains("2,5"))
        }
        ?.odd ?: totals?.values.orEmpty().firstOrNull { it.value?.lowercase()?.contains("over") == true }?.odd
        ?: "—"

    return CompactOddsRow(
        o1 = o1,
        ox = ox,
        o2 = o2,
        f1 = f1,
        k1 = k1,
        f2 = f2,
        k2 = k2,
        tot = tot
    )
}

private fun List<Bet>.findMatchWinner(): Bet? {
    val byId = firstOrNull { it.id == 1 }
    if (byId != null) return byId
    return firstOrNull { bet ->
        val n = bet.name?.lowercase().orEmpty()
        n.contains("match winner") || n == "winner" || n.contains("3-way")
    }
}

private fun List<Bet>.findAsianHandicap(): Bet? {
    return firstOrNull { bet ->
        val n = bet.name?.lowercase().orEmpty()
        n.contains("asian handicap") || (n.contains("handicap") && !n.contains("european"))
    }
}

private fun List<Bet>.findGoalsOverUnder(): Bet? {
    return firstOrNull { bet ->
        val n = bet.name?.lowercase().orEmpty()
        (n.contains("over") && n.contains("under")) || n.contains("goals over/under") || n == "goals over/under"
    }
}

private fun extractHandicapLabel(value: String?): String {
    if (value.isNullOrBlank()) return "—"
    val parts = value.trim().split(" ", limit = 2)
    return if (parts.size >= 2) {
        parts[1].trim()
    } else {
        value
    }
}
