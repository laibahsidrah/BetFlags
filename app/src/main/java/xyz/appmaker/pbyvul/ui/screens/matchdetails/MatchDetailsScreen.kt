package xyz.appmaker.pbyvul.ui.screens.matchdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.appmaker.pbyvul.data.api.models.FixtureEvent
import xyz.appmaker.pbyvul.data.api.models.FixtureResponse
import xyz.appmaker.pbyvul.data.api.models.FixtureStatisticsResponse
import xyz.appmaker.pbyvul.data.api.models.LineupResponse
import xyz.appmaker.pbyvul.ui.components.ErrorScreen
import xyz.appmaker.pbyvul.ui.components.LiveBadge
import xyz.appmaker.pbyvul.ui.components.LoadingScreen
import xyz.appmaker.pbyvul.ui.components.MatchCard
import xyz.appmaker.pbyvul.ui.components.SectionHeader
import xyz.appmaker.pbyvul.ui.components.TeamLogo
import xyz.appmaker.pbyvul.ui.theme.TextPrimary
import xyz.appmaker.pbyvul.ui.theme.GreenLive
import xyz.appmaker.pbyvul.ui.theme.LightGray
import xyz.appmaker.pbyvul.ui.theme.MediumGray
import xyz.appmaker.pbyvul.ui.theme.NavyPrimary
import xyz.appmaker.pbyvul.ui.theme.PrimaryRed
import xyz.appmaker.pbyvul.ui.theme.RedCard
import xyz.appmaker.pbyvul.ui.theme.White
import xyz.appmaker.pbyvul.ui.theme.YellowCard
import xyz.appmaker.pbyvul.R

private val matchDetailTabResIds = listOf(
    R.string.tab_overview,
    R.string.tab_statistics,
    R.string.tab_events,
    R.string.tab_lineups,
    R.string.tab_h2h,
    R.string.tab_predictions
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailsScreen(
    onBack: () -> Unit,
    onTeamClick: (Int) -> Unit,
    onPlayerClick: (Int, Int) -> Unit,
    onFixtureClick: (Int) -> Unit = {},
    viewModel: MatchDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    val title = remember(state.fixture) {
        val h = state.fixture?.teams?.home?.name ?: "—"
        val a = state.fixture?.teams?.away?.name ?: "—"
        "$h vs $a"
    }

    val season = state.fixture?.league?.season ?: 2024

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = White,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = White
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = NavyPrimary,
                titleContentColor = White,
                navigationIconContentColor = White,
                actionIconContentColor = White
            )
        )

        when {
            state.isLoading && state.fixture == null -> LoadingScreen()
            state.error != null && state.fixture == null ->
                ErrorScreen(state.error ?: "", onRetry = viewModel::loadAll)
            else -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            edgePadding = 8.dp,
                            containerColor = LightGray,
                            contentColor = TextPrimary
                        ) {
                            matchDetailTabResIds.forEachIndexed { index, resId ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            text = stringResource(resId),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 16.dp)
                        ) {
                            when (selectedTab) {
                                0 -> OverviewTab(state.fixture)
                                1 -> StatisticsTab(state.statistics)
                                2 -> EventsTab(state.events)
                                3 -> LineupsTab(
                                    lineups = state.lineups,
                                    onTeamClick = onTeamClick,
                                    onPlayerClick = { pid -> onPlayerClick(pid, season) }
                                )
                                4 -> H2HTab(
                                    fixtures = state.headToHead,
                                    onFixtureClick = onFixtureClick
                                )
                                5 -> PredictionsTab(state.prediction, state.odds)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(fixture: FixtureResponse?) {
    if (fixture == null) {
        Text(stringResource(R.string.no_match_data), modifier = Modifier.padding(16.dp))
        return
    }
    val isLive = fixture.fixture?.status?.short in listOf(
        "1H", "HT", "2H", "ET", "BT", "P", "SUSP", "INT", "LIVE"
    )
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                TeamLogo(imageLink = fixture.teams?.home?.logo, size = 48.dp)
                Text(
                    fixture.teams?.home?.name ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isLive) LiveBadge()
                Text(
                    text = if (fixture.goals?.home != null) {
                        "${fixture.goals.home} : ${fixture.goals.away}"
                    } else {
                        stringResource(R.string.vs)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryRed,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = fixture.fixture?.status?.long ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MediumGray
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                TeamLogo(imageLink = fixture.teams?.away?.logo, size = 48.dp)
                Text(
                    fixture.teams?.away?.name ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(stringResource(R.string.stadium))
        Text(
            text = listOfNotNull(
                fixture.fixture?.venue?.name,
                fixture.fixture?.venue?.city
            ).joinToString(", ").ifEmpty { "—" },
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        fixture.fixture?.referee?.let { ref ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.referee_label, ref),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun StatisticsTab(statistics: List<FixtureStatisticsResponse>) {
    if (statistics.size < 2) {
        Text(
            stringResource(R.string.statistics_unavailable),
            modifier = Modifier.padding(16.dp),
            color = MediumGray
        )
        return
    }
    val home = statistics[0].team
    val away = statistics[1].team
    val homeStats = statistics[0].statistics.orEmpty()
    val awayStats = statistics[1].statistics.orEmpty()
    val types = buildSet {
        homeStats.forEach { add(it.type ?: "") }
        awayStats.forEach { add(it.type ?: "") }
    }.filter { it.isNotEmpty() }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamLogo(imageLink = home?.logo, size = 28.dp)
            Text(stringResource(R.string.statistics_label), style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            TeamLogo(imageLink = away?.logo, size = 28.dp)
        }
        types.forEach { type ->
            val hv = homeStats.find { it.type == type }?.value
            val av = awayStats.find { it.type == type }?.value
            StatisticCompareRow(label = type, homeValue = hv, awayValue = av)
        }
    }
}

@Composable
private fun StatisticCompareRow(label: String, homeValue: Any?, awayValue: Any?) {
    val h = parseStatFraction(homeValue)
    val a = parseStatFraction(awayValue)
    val total = (h + a).coerceAtLeast(0.0001f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(statValueToString(homeValue), style = MaterialTheme.typography.labelMedium)
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MediumGray,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(statValueToString(awayValue), style = MaterialTheme.typography.labelMedium)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val hw = if (h + a > 0f) h / total else 0.5f
            val aw = if (h + a > 0f) a / total else 0.5f
            Box(
                modifier = Modifier
                    .weight(hw.coerceIn(0.01f, 1f))
                    .height(8.dp)
                    .background(PrimaryRed, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .weight(aw.coerceIn(0.01f, 1f))
                    .height(8.dp)
                    .background(TextPrimary, RoundedCornerShape(4.dp))
            )
        }
    }
}

private fun statValueToString(v: Any?): String = when (v) {
    null -> "—"
    is String -> v
    else -> v.toString()
}

private fun parseStatFraction(v: Any?): Float {
    val s = when (v) {
        null -> return 0f
        is Number -> return v.toFloat().coerceAtLeast(0f)
        else -> v.toString()
    }
    val cleaned = s.replace("%", "").trim()
    val num = cleaned.toFloatOrNull() ?: return 1f
    return num.coerceAtLeast(0f)
}

@Composable
private fun EventsTab(events: List<FixtureEvent>) {
    if (events.isEmpty()) {
        Text(stringResource(R.string.no_events_yet), modifier = Modifier.padding(16.dp), color = MediumGray)
        return
    }
    val sorted = events.sortedWith(
        compareBy<FixtureEvent>(
            { it.time?.elapsed ?: 0 },
            { it.time?.extra ?: 0 }
        )
    )
    Column(modifier = Modifier.padding(16.dp)) {
        sorted.forEach { ev ->
            EventRow(ev)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EventRow(ev: FixtureEvent) {
    val minute = buildString {
        append(ev.time?.elapsed ?: "?")
        ev.time?.extra?.takeIf { it > 0 }?.let { append("+$it") }
        append("'")
    }
    val (accent, iconText) = when (ev.type?.lowercase()) {
        "goal" -> GreenLive to "⚽"
        "card" -> when {
            ev.detail?.contains("yellow", true) == true -> YellowCard to "🟨"
            else -> RedCard to "🟥"
        }
        "subst" -> TextPrimary to "⇄"
        else -> MediumGray to "•"
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            minute,
            style = MaterialTheme.typography.labelLarge,
            color = accent,
            modifier = Modifier.width(40.dp)
        )
        Text(iconText, modifier = Modifier.padding(end = 8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                listOfNotNull(ev.player?.name, ev.detail).joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium
            )
            ev.team?.name?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = MediumGray)
            }
            ev.assist?.name?.takeIf { it.isNotBlank() }?.let {
                Text(stringResource(R.string.assist_label, it), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun LineupsTab(
    lineups: List<LineupResponse>,
    onTeamClick: (Int) -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    if (lineups.isEmpty()) {
        Text(stringResource(R.string.lineups_unavailable), modifier = Modifier.padding(16.dp), color = MediumGray)
        return
    }
    Column {
        lineups.forEach { lu ->
            val tid = lu.team?.id
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (tid != null) {
                                Modifier.clickable { onTeamClick(tid) }
                            } else {
                                Modifier
                            }
                        )
                ) {
                    TeamLogo(imageLink = lu.team?.logo, size = 36.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            lu.team?.name ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            stringResource(R.string.formation_label, lu.formation ?: "—"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MediumGray
                        )
                        lu.coach?.name?.let {
                            Text(stringResource(R.string.coach_label, it), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                SectionHeader(stringResource(R.string.starting_xi))
                lu.startXI.orEmpty().forEach { holder ->
                    val p = holder.player
                    if (p?.id != null) {
                        LineupPlayerRow(p.name, p.number, p.pos, p.grid, onClick = { onPlayerClick(p.id!!) })
                    }
                }
                SectionHeader(stringResource(R.string.substitutes))
                lu.substitutes.orEmpty().forEach { holder ->
                    val p = holder.player
                    if (p?.id != null) {
                        LineupPlayerRow(p.name, p.number, p.pos, null, onClick = { onPlayerClick(p.id!!) })
                    }
                }
            }
        }
    }
}

@Composable
private fun LineupPlayerRow(
    name: String?,
    number: Int?,
    pos: String?,
    grid: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = number?.toString() ?: "—",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.width(28.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name ?: "",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = PrimaryRed,
                modifier = Modifier.padding(end = 8.dp)
            )
            val meta = listOfNotNull(pos, grid).joinToString(" · ")
            if (meta.isNotEmpty()) {
                Text(meta, style = MaterialTheme.typography.labelSmall, color = MediumGray)
            }
        }
    }
}

@Composable
private fun H2HTab(
    fixtures: List<FixtureResponse>,
    onFixtureClick: (Int) -> Unit
) {
    if (fixtures.isEmpty()) {
        Text(stringResource(R.string.no_h2h_matches), modifier = Modifier.padding(16.dp), color = MediumGray)
        return
    }
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        fixtures.forEach { fx ->
            val id = fx.fixture?.id
            if (id != null) {
                MatchCard(fixture = fx, onClick = { onFixtureClick(id) })
            }
        }
    }
}

@Composable
private fun PredictionsTab(
    prediction: xyz.appmaker.pbyvul.data.api.models.PredictionResponse?,
    odds: xyz.appmaker.pbyvul.data.api.models.OddsResponse?
) {
    Column(modifier = Modifier.padding(16.dp)) {
        val pred = prediction?.predictions
        if (pred == null && odds == null) {
            Text(stringResource(R.string.predictions_unavailable), color = MediumGray)
            return
        }
        pred?.advice?.let {
            SectionHeader(stringResource(R.string.advice))
            Text(it, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(12.dp))
        }
        pred?.percent?.let { pc ->
            SectionHeader(stringResource(R.string.probabilities))
            PredictionBar(stringResource(R.string.home), parsePercent(pc.home), PrimaryRed)
            Spacer(modifier = Modifier.height(6.dp))
            PredictionBar(stringResource(R.string.draw), parsePercent(pc.draw), MediumGray)
            Spacer(modifier = Modifier.height(6.dp))
            PredictionBar(stringResource(R.string.away), parsePercent(pc.away), TextPrimary)
        }
        pred?.winner?.name?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(stringResource(R.string.favorite_label, it), style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        }
        odds?.bookmakers?.takeIf { it.isNotEmpty() }?.let { bookmakers ->
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(stringResource(R.string.odds))
            bookmakers.take(5).forEach { bm ->
                Text(
                    bm.name ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                bm.bets?.take(3)?.forEach { bet ->
                    Text(
                        bet.name ?: "",
                        style = MaterialTheme.typography.labelLarge,
                        color = MediumGray
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bet.values.orEmpty().take(12).forEach { v ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = LightGray),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        v.value ?: "",
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        v.odd ?: "",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = PrimaryRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PredictionBar(label: String, fraction: Float, color: androidx.compose.ui.graphics.Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text("${(fraction * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(MediumGray.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .height(10.dp)
                    .background(color, RoundedCornerShape(5.dp))
            )
        }
    }
}

private fun parsePercent(s: String?): Float {
    if (s.isNullOrBlank()) return 0f
    return s.replace("%", "").trim().toFloatOrNull()?.div(100f) ?: 0f
}
