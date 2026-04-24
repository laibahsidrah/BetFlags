package xyz.appmaker.pbyvul.ui.screens.live

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import kotlin.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.appmaker.pbyvul.R
import xyz.appmaker.pbyvul.ui.components.EmptyScreen
import xyz.appmaker.pbyvul.ui.components.ErrorScreen
import xyz.appmaker.pbyvul.ui.components.LiveBettingRow
import xyz.appmaker.pbyvul.ui.components.LoadingScreen
import xyz.appmaker.pbyvul.ui.components.SectionHeader
import xyz.appmaker.pbyvul.ui.theme.SectionLeagueGrey
import xyz.appmaker.pbyvul.ui.theme.SportsbookRowAlt
import xyz.appmaker.pbyvul.ui.theme.TextPrimary
import xyz.appmaker.pbyvul.ui.theme.White
import xyz.appmaker.pbyvul.util.CompactOddsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
    onFixtureClick: (Int) -> Unit,
    viewModel: LiveViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val fixturesByLeague = state.fixtures.groupBy { it.league?.name ?: "—" }
    val errorMessage = state.error

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {
                state.isLoading && state.fixtures.isEmpty() -> LoadingScreen()
                errorMessage != null && state.fixtures.isEmpty() ->
                    ErrorScreen(errorMessage, onRetry = viewModel::loadLiveFixtures)
                state.fixtures.isEmpty() ->
                    EmptyScreen(stringResource(R.string.no_live_matches))
                else -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = viewModel::refresh,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            item {
                                OddsTableHeader()
                            }
                            fixturesByLeague.forEach { (leagueName, fixtures) ->
                                item(key = "header_$leagueName") {
                                    SectionHeader(
                                        title = buildString {
                                            append(stringResource(R.string.sport_football_prefix))
                                            append(" ")
                                            append(leagueName)
                                        }
                                    )
                                }
                                itemsIndexed(
                                    items = fixtures,
                                    key = { _, f -> f.fixture?.id ?: f.hashCode() }
                                ) { rowIndex, fixture ->
                                    val id = fixture.fixture?.id ?: return@itemsIndexed
                                    val odds = state.oddsByFixture[id] ?: CompactOddsRow.empty()
                                    val stripe = if (rowIndex % 2 == 0) White else SportsbookRowAlt
                                    LiveBettingRow(
                                        fixture = fixture,
                                        odds = odds,
                                        rowBackground = stripe,
                                        onClick = { onFixtureClick(id) }
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
private fun OddsTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SectionLeagueGrey)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.col_event),
            modifier = Modifier.width(118.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = stringResource(R.string.col_score),
            modifier = Modifier.width(40.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            listOf(
                R.string.col_1,
                R.string.col_x,
                R.string.col_2,
                R.string.col_f1,
                R.string.col_k1,
                R.string.col_f2,
                R.string.col_k2,
                R.string.col_tot
            ).forEach { res ->
                Text(
                    text = stringResource(res),
                    modifier = Modifier
                        .width(34.dp)
                        .padding(vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}
