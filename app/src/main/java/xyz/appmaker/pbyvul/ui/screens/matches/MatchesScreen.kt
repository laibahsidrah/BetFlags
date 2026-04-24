package xyz.appmaker.pbyvul.ui.screens.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import kotlin.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.appmaker.pbyvul.R
import xyz.appmaker.pbyvul.ui.components.EmptyScreen
import xyz.appmaker.pbyvul.ui.components.ErrorScreen
import xyz.appmaker.pbyvul.ui.components.LoadingScreen
import xyz.appmaker.pbyvul.ui.components.MatchCard
import xyz.appmaker.pbyvul.ui.components.SectionHeader
import xyz.appmaker.pbyvul.ui.theme.LightGray
import xyz.appmaker.pbyvul.ui.theme.SectionLeagueGrey
import xyz.appmaker.pbyvul.ui.theme.SportsbookTabActive
import xyz.appmaker.pbyvul.ui.theme.TextPrimary
import xyz.appmaker.pbyvul.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    onFixtureClick: (Int) -> Unit,
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val apiDateFormat = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
    }
    val dayFormatter = remember { SimpleDateFormat("EEE", Locale.ENGLISH) }
    val shortDateFormatter = remember { SimpleDateFormat("dd.MM", Locale.ENGLISH) }
    val headerDateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val weekDates = remember {
        val today = Calendar.getInstance()
        (-3..3).map { offset ->
            val c = Calendar.getInstance()
            c.timeInMillis = today.timeInMillis
            c.add(Calendar.DAY_OF_MONTH, offset)
            apiDateFormat.format(c.time) to c
        }
    }

    val todayApiString = remember {
        apiDateFormat.format(Calendar.getInstance().time)
    }

    val fixturesByLeague = state.fixtures.groupBy { it.league?.name ?: "—" }
    val errorMessage = state.error

    val selectedDateLabel = remember(state.selectedDate) {
        runCatching {
            val d = apiDateFormat.parse(state.selectedDate) ?: return@runCatching state.selectedDate
            headerDateFormatter.format(d)
        }.getOrDefault(state.selectedDate)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .background(SectionLeagueGrey)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            weekDates.forEach { (dateStr, cal) ->
                val isSelected = dateStr == state.selectedDate
                val isToday = dateStr == todayApiString
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) SportsbookTabActive else LightGray)
                        .clickable { viewModel.selectDate(dateStr) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isToday) stringResource(R.string.today) else dayFormatter.format(cal.time),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) White else TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = shortDateFormatter.format(cal.time),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) White else TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SectionLeagueGrey.copy(alpha = 0.85f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDateLabel,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        when {
            state.isLoading && state.fixtures.isEmpty() -> LoadingScreen()
            errorMessage != null && state.fixtures.isEmpty() ->
                ErrorScreen(errorMessage, onRetry = viewModel::loadFixtures)
            state.fixtures.isEmpty() ->
                EmptyScreen(stringResource(R.string.no_matches_date))
            else -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        fixturesByLeague.forEach { (leagueName, fixtures) ->
                            item(key = "header_$leagueName") {
                                SectionHeader(leagueName)
                            }
                            items(
                                items = fixtures,
                                key = { it.fixture?.id ?: it.hashCode() }
                            ) { fixture ->
                                MatchCard(
                                    fixture = fixture,
                                    onClick = {
                                        val id = fixture.fixture?.id ?: return@MatchCard
                                        onFixtureClick(id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
