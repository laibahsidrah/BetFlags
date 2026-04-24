package xyz.appmaker.pbyvul.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.appmaker.pbyvul.data.api.models.FixtureResponse
import xyz.appmaker.pbyvul.R
import xyz.appmaker.pbyvul.ui.theme.GreenLive
import xyz.appmaker.pbyvul.ui.theme.TextPrimary
import xyz.appmaker.pbyvul.ui.theme.White
import xyz.appmaker.pbyvul.ui.theme.NavyPrimary

@Composable
fun MatchCard(
    fixture: FixtureResponse,
    onClick: () -> Unit
) {
    val isLive = fixture.fixture?.status?.short in listOf("1H", "HT", "2H", "ET", "BT", "P", "SUSP", "INT", "LIVE")
    val isFinished = fixture.fixture?.status?.short in listOf("FT", "AET", "PEN")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, NavyPrimary.copy(alpha = 0.14f), RoundedCornerShape(8.dp))
            .background(White)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fixture.league?.name ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (isLive) {
                    LiveBadge()
                } else {
                    Text(
                        text = getStatusText(fixture),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isFinished) MaterialTheme.colorScheme.onSurfaceVariant else TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = fixture.teams?.home?.name ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (fixture.teams?.home?.winner == true) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TeamLogo(imageLink = fixture.teams?.home?.logo, size = 28.dp)
                }

                Text(
                    text = if (fixture.goals?.home != null) {
                        "${fixture.goals.home} - ${fixture.goals.away}"
                    } else {
                        stringResource(R.string.vs)
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.Bold,
                    color = if (isLive) NavyPrimary else TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamLogo(imageLink = fixture.teams?.away?.logo, size = 28.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = fixture.teams?.away?.name ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (fixture.teams?.away?.winner == true) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

        if (isLive && fixture.fixture?.status?.elapsed != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${fixture.fixture.status.elapsed}'",
                style = MaterialTheme.typography.labelSmall,
                color = GreenLive,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun getStatusText(fixture: FixtureResponse): String {
    val status = fixture.fixture?.status
    return when (status?.short) {
        "TBD" -> stringResource(R.string.status_tbd)
        "NS" -> {
            fixture.fixture.date?.let { date ->
                try {
                    date.substring(11, 16)
                } catch (_: Exception) {
                    stringResource(R.string.status_not_started)
                }
            } ?: stringResource(R.string.status_not_started)
        }
        "FT" -> stringResource(R.string.status_finished)
        "AET" -> stringResource(R.string.status_finished_et)
        "PEN" -> stringResource(R.string.status_finished_pen)
        "PST" -> stringResource(R.string.status_postponed)
        "CANC" -> stringResource(R.string.status_cancelled)
        "ABD" -> stringResource(R.string.status_abandoned)
        "HT" -> stringResource(R.string.status_halftime)
        else -> status?.long ?: ""
    }
}
