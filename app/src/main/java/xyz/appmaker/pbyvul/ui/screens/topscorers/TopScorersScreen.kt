package xyz.appmaker.pbyvul.ui.screens.topscorers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import xyz.appmaker.pbyvul.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.appmaker.pbyvul.ui.components.RemoteImage
import xyz.appmaker.pbyvul.data.api.models.PlayerResponse
import xyz.appmaker.pbyvul.ui.components.ErrorScreen
import xyz.appmaker.pbyvul.ui.components.LoadingScreen
import xyz.appmaker.pbyvul.ui.components.TeamLogo
import xyz.appmaker.pbyvul.ui.theme.TextPrimary
import xyz.appmaker.pbyvul.ui.theme.NavyPrimary
import xyz.appmaker.pbyvul.ui.theme.LightGray
import xyz.appmaker.pbyvul.ui.theme.PrimaryRed
import xyz.appmaker.pbyvul.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopScorersScreen(
    onBack: () -> Unit,
    onPlayerClick: (playerId: Int, season: Int) -> Unit,
    viewModel: TopScorersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(R.string.scorers), stringResource(R.string.assists_tab))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.top_players), color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = NavyPrimary)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = White,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = PrimaryRed
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> ErrorScreen(
                    message = state.error ?: "",
                    onRetry = { viewModel.loadAll() }
                )
                else -> {
                    val players = if (selectedTab == 0) state.topScorers else state.topAssists
                    val isGoals = selectedTab == 0

                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refresh() }
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(players) { index, playerResponse ->
                                PlayerRow(
                                    rank = index + 1,
                                    player = playerResponse,
                                    isGoals = isGoals,
                                    onClick = {
                                        val id = playerResponse.player?.id ?: return@PlayerRow
                                        val season = playerResponse.statistics?.firstOrNull()
                                            ?.league?.season ?: 2024
                                        onPlayerClick(id, season)
                                    }
                                )
                                if (index < players.lastIndex) {
                                    HorizontalDivider(thickness = 0.5.dp, color = LightGray)
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
private fun PlayerRow(
    rank: Int,
    player: PlayerResponse,
    isGoals: Boolean,
    onClick: () -> Unit
) {
    val stat = player.statistics?.firstOrNull()
    val value = if (isGoals) stat?.goals?.total ?: 0 else stat?.goals?.assists ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rank",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.width(32.dp)
        )

        RemoteImage(
            imageLink = player.player?.photo,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = player.player?.name ?: "",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                TeamLogo(imageLink = stat?.team?.logo, size = 16.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stat?.team?.name ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Text(
            text = "$value",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryRed
        )
    }
}
