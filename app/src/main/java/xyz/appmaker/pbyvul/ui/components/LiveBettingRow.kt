package xyz.appmaker.pbyvul.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.appmaker.pbyvul.data.api.models.FixtureResponse
import xyz.appmaker.pbyvul.ui.theme.NavyPrimary
import xyz.appmaker.pbyvul.ui.theme.OddsCellBackground
import xyz.appmaker.pbyvul.ui.theme.ScoreBlockNavy
import xyz.appmaker.pbyvul.ui.theme.TealLiveTime
import xyz.appmaker.pbyvul.ui.theme.TextPrimary
import xyz.appmaker.pbyvul.ui.theme.White
import xyz.appmaker.pbyvul.util.CompactOddsRow

private val OddsCellWidth = 34.dp
private val OddsCellHeight = 28.dp

@Composable
fun LiveBettingRow(
    fixture: FixtureResponse,
    odds: CompactOddsRow,
    onClick: () -> Unit,
    rowBackground: Color = Color.White
) {
    val isLive = fixture.fixture?.status?.short in listOf("1H", "HT", "2H", "ET", "BT", "P", "SUSP", "INT", "LIVE")
    val elapsed = fixture.fixture?.status?.elapsed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(rowBackground)
            .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(118.dp)
        ) {
            Text(
                text = buildTimeLabel(fixture, isLive, elapsed),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = if (isLive && elapsed != null) TealLiveTime else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = fixture.teams?.home?.name ?: "",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = fixture.teams?.away?.name ?: "",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        ScoreColumn(fixture)

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OddsCell(odds.o1)
            OddsCell(odds.ox)
            OddsCell(odds.o2)
            OddsCell(odds.f1)
            OddsCell(odds.k1)
            OddsCell(odds.f2)
            OddsCell(odds.k2)
            OddsCell(odds.tot)
        }
    }
}

@Composable
private fun ScoreColumn(fixture: FixtureResponse) {
    val gh = fixture.goals?.home
    val ga = fixture.goals?.away
    Box(
        modifier = Modifier
            .width(40.dp)
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (gh != null && ga != null) {
            Column(
                modifier = Modifier
                    .width(36.dp)
                    .background(ScoreBlockNavy, RoundedCornerShape(3.dp))
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = gh.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Bold,
                    color = White,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = ga.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Bold,
                    color = White,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        } else {
            Text(
                text = "—",
                modifier = Modifier.width(40.dp),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

private fun buildTimeLabel(fixture: FixtureResponse, isLive: Boolean, elapsed: Int?): String {
    return when {
        isLive && elapsed != null -> "${elapsed}'"
        else -> fixture.fixture?.date?.let { d ->
            try {
                d.substring(11, 16)
            } catch (_: Exception) {
                "—"
            }
        } ?: "—"
    }
}

@Composable
private fun OddsCell(value: String) {
    Box(
        modifier = Modifier
            .width(OddsCellWidth)
            .height(OddsCellHeight)
            .background(OddsCellBackground, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            maxLines = 1,
            color = White,
            fontWeight = FontWeight.SemiBold
        )
    }
}
