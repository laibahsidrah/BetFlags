package xyz.appmaker.pbyvul.ui.screens.standings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import xyz.appmaker.pbyvul.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.appmaker.pbyvul.data.api.models.StandingEntry
import xyz.appmaker.pbyvul.ui.components.ErrorScreen
import xyz.appmaker.pbyvul.ui.components.LoadingScreen
import xyz.appmaker.pbyvul.ui.components.TeamLogo
import xyz.appmaker.pbyvul.ui.theme.TextPrimary
import xyz.appmaker.pbyvul.ui.theme.LightGray
import xyz.appmaker.pbyvul.ui.theme.NavyPrimary
import xyz.appmaker.pbyvul.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandingsScreen(
    onBack: () -> Unit,
    onTeamClick: (Int) -> Unit,
    viewModel: StandingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(state.leagueName ?: stringResource(R.string.standings), color = White, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = NavyPrimary)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> ErrorScreen(
                    message = state.error ?: "",
                    onRetry = { viewModel.loadStandings() }
                )
                else -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refresh() }
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item { TableHeader() }
                            items(state.standings) { entry ->
                                TableRow(entry = entry, onClick = {
                                    entry.team?.id?.let { onTeamClick(it) }
                                })
                                HorizontalDivider(thickness = 0.5.dp, color = LightGray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.col_rank), modifier = Modifier.width(28.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(stringResource(R.string.col_team), modifier = Modifier.width(160.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.col_played), modifier = Modifier.width(32.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(stringResource(R.string.col_wins), modifier = Modifier.width(32.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(stringResource(R.string.col_draws), modifier = Modifier.width(32.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(stringResource(R.string.col_losses), modifier = Modifier.width(32.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(stringResource(R.string.col_gd), modifier = Modifier.width(36.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(stringResource(R.string.col_pts), modifier = Modifier.width(32.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun TableRow(entry: StandingEntry, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${entry.rank ?: "-"}",
            modifier = Modifier.width(28.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = TextPrimary
        )

        Row(modifier = Modifier.width(160.dp), verticalAlignment = Alignment.CenterVertically) {
            TeamLogo(imageLink = entry.team?.logo, size = 22.dp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = entry.team?.name ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
        }

        val stats = entry.all
        Text("${stats?.played ?: 0}", modifier = Modifier.width(32.dp), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        Text("${stats?.win ?: 0}", modifier = Modifier.width(32.dp), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        Text("${stats?.draw ?: 0}", modifier = Modifier.width(32.dp), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        Text("${stats?.lose ?: 0}", modifier = Modifier.width(32.dp), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        Text(
            text = "${entry.goalsDiff ?: 0}",
            modifier = Modifier.width(36.dp),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${entry.points ?: 0}",
            modifier = Modifier.width(32.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = TextPrimary
        )
    }
}
