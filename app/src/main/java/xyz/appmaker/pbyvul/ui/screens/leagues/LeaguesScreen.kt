package xyz.appmaker.pbyvul.ui.screens.leagues

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import kotlin.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.appmaker.pbyvul.R
import xyz.appmaker.pbyvul.data.api.models.LeagueResponse
import xyz.appmaker.pbyvul.ui.components.EmptyScreen
import xyz.appmaker.pbyvul.ui.components.ErrorScreen
import xyz.appmaker.pbyvul.ui.components.LoadingScreen
import xyz.appmaker.pbyvul.ui.components.SectionHeader
import xyz.appmaker.pbyvul.ui.components.TeamLogo
import xyz.appmaker.pbyvul.ui.theme.LightGray
import xyz.appmaker.pbyvul.ui.theme.NavyPrimary
import xyz.appmaker.pbyvul.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaguesScreen(
    onLeagueClick: (leagueId: Int, season: Int) -> Unit,
    viewModel: LeaguesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            placeholder = { Text(stringResource(R.string.search_league_hint)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NavyPrimary,
                cursorColor = NavyPrimary
            )
        )

        when {
            state.isLoading -> LoadingScreen()
            state.error != null -> ErrorScreen(state.error!!, onRetry = viewModel::loadLeagues)
            state.leaguesByCountry.isEmpty() -> EmptyScreen(stringResource(R.string.no_leagues_found))
            else -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        state.leaguesByCountry.forEach { (country, leagues) ->
                            item(key = "header_$country") { SectionHeader(country) }
                            items(leagues, key = { "${country}_${it.league?.id}" }) { league ->
                                LeagueItem(
                                    league = league,
                                    onClick = {
                                        val leagueId = league.league?.id ?: return@LeagueItem
                                        val season = league.seasons
                                            ?.firstOrNull { it.current == true }
                                            ?.year
                                            ?: league.seasons?.maxOfOrNull { it.year ?: 0 }
                                            ?: 2024
                                        onLeagueClick(leagueId, season)
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

@Composable
private fun LeagueItem(league: LeagueResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = LightGray)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamLogo(imageLink = league.league?.logo, size = 36.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = league.league?.name ?: "",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = league.league?.type ?: "",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            TeamLogo(imageLink = league.country?.flag, size = 28.dp)
        }
    }
}
