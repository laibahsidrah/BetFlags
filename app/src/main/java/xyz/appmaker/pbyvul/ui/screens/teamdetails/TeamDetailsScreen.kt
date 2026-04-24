package xyz.appmaker.pbyvul.ui.screens.teamdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import xyz.appmaker.pbyvul.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.appmaker.pbyvul.ui.components.RemoteImage
import xyz.appmaker.pbyvul.data.api.models.InjuryResponse
import xyz.appmaker.pbyvul.data.api.models.SquadPlayer
import xyz.appmaker.pbyvul.ui.components.ErrorScreen
import xyz.appmaker.pbyvul.ui.components.LoadingScreen
import xyz.appmaker.pbyvul.ui.components.MatchCard
import xyz.appmaker.pbyvul.ui.components.SectionHeader
import xyz.appmaker.pbyvul.ui.components.TeamLogo
import xyz.appmaker.pbyvul.ui.theme.TextPrimary
import xyz.appmaker.pbyvul.ui.theme.LightGray
import xyz.appmaker.pbyvul.ui.theme.MediumGray
import xyz.appmaker.pbyvul.ui.theme.NavyPrimary
import xyz.appmaker.pbyvul.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsScreen(
    onBack: () -> Unit,
    onPlayerClick: (Int, Int) -> Unit,
    onFixtureClick: (Int) -> Unit,
    viewModel: TeamDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val teamName = state.teamInfo?.team?.name ?: stringResource(R.string.team)
    val positionOtherLabel = stringResource(R.string.position_other)

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = teamName,
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
            actions = {
                IconButton(onClick = viewModel::toggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
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
            state.isLoading && state.teamInfo == null -> LoadingScreen()
            state.error != null && state.teamInfo == null ->
                ErrorScreen(state.error ?: "", onRetry = viewModel::loadAll)
            else -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            TeamHeader(state)
                        }
                        item {
                            SectionHeader(stringResource(R.string.squad))
                        }
                        val grouped = state.squad?.players.orEmpty()
                            .groupBy { it.position?.ifBlank { null } ?: positionOtherLabel }
                            .toSortedMap(compareBy { it })
                        grouped.forEach { (position, players) ->
                            item(key = "pos_$position") {
                                Text(
                                    text = position,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                            items(
                                items = players,
                                key = { p -> "${position}_${p.id ?: p.hashCode()}" }
                            ) { p ->
                                SquadPlayerRow(p) {
                                    p.id?.let { id ->
                                        onPlayerClick(id, TeamDetailsViewModel.SEASON)
                                    }
                                }
                            }
                        }
                        item {
                            SectionHeader(stringResource(R.string.coach))
                            CoachBlock(state)
                        }
                        item {
                            SectionHeader(stringResource(R.string.injuries))
                            InjuriesBlock(state.injuries)
                        }
                        item {
                            SectionHeader(stringResource(R.string.recent_matches))
                        }
                        items(
                            items = state.recentFixtures,
                            key = { it.fixture?.id ?: it.hashCode() }
                        ) { fx ->
                            val fid = fx.fixture?.id
                            if (fid != null) {
                                Column(Modifier.padding(horizontal = 16.dp)) {
                                    MatchCard(fixture = fx, onClick = { onFixtureClick(fid) })
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
private fun TeamHeader(state: TeamDetailsUiState) {
    val team = state.teamInfo?.team
    val venue = state.teamInfo?.venue
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TeamLogo(imageLink = team?.logo, size = 72.dp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    team?.name ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                team?.country?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MediumGray)
                }
                team?.founded?.let {
                    Text(stringResource(R.string.founded_label, it.toString()), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        venue?.name?.let {
            Text(stringResource(R.string.stadium_label, it), style = MaterialTheme.typography.bodyMedium)
        }
        venue?.city?.let {
            Text(stringResource(R.string.city_label, it), style = MaterialTheme.typography.bodySmall, color = MediumGray)
        }
        val cap = venue?.capacity
        if (cap != null) {
            Text(stringResource(R.string.capacity_label, cap.toString()), style = MaterialTheme.typography.bodySmall, color = MediumGray)
        }
        venue?.surface?.let {
            Text(stringResource(R.string.surface_label, it), style = MaterialTheme.typography.bodySmall, color = MediumGray)
        }
    }
}

@Composable
private fun SquadPlayerRow(player: SquadPlayer, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemoteImage(
                imageLink = player.photo,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    player.name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    listOfNotNull(
                        player.position,
                        player.number?.let { "#$it" }
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MediumGray
                )
            }
        }
    }
}

@Composable
private fun CoachBlock(state: TeamDetailsUiState) {
    val c = state.coach
    if (c == null) {
        Text(
            stringResource(R.string.no_data),
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MediumGray
        )
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RemoteImage(
            imageLink = c.photo,
            modifier = Modifier.size(56.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(c.name ?: "", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            c.nationality?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MediumGray)
            }
        }
    }
}

@Composable
private fun InjuriesBlock(injuries: List<InjuryResponse>) {
    if (injuries.isEmpty()) {
        Text(
            stringResource(R.string.no_injuries),
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MediumGray
        )
        return
    }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        injuries.forEach { inj ->
            val p = inj.player
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = LightGray)
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RemoteImage(
                        imageLink = p?.photo,
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(p?.name ?: "", fontWeight = FontWeight.Medium)
                        Text(
                            listOfNotNull(p?.type, p?.reason).joinToString(" · "),
                            style = MaterialTheme.typography.labelSmall,
                            color = MediumGray
                        )
                    }
                }
            }
        }
    }
}
