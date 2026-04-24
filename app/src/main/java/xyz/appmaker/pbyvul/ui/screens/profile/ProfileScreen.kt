package xyz.appmaker.pbyvul.ui.screens.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.appmaker.pbyvul.R
import xyz.appmaker.pbyvul.data.local.entity.FavoriteLeagueEntity
import xyz.appmaker.pbyvul.data.local.entity.FavoriteTeamEntity
import xyz.appmaker.pbyvul.ui.components.SectionHeader
import xyz.appmaker.pbyvul.ui.components.TeamLogo
import xyz.appmaker.pbyvul.ui.screens.favorites.FavoritesViewModel
import xyz.appmaker.pbyvul.ui.screens.more.MoreViewModel
import xyz.appmaker.pbyvul.ui.theme.LightGray
import xyz.appmaker.pbyvul.ui.theme.NavyPrimary
import xyz.appmaker.pbyvul.ui.theme.PrimaryRed
import xyz.appmaker.pbyvul.ui.theme.TextPrimary
import xyz.appmaker.pbyvul.ui.theme.White

private data class TopLeagueOption(
    val leagueId: Int,
    val season: Int,
    val title: String
)

private val TOP_LEAGUE_OPTIONS = listOf(
    TopLeagueOption(39, 2024, "Premier League"),
    TopLeagueOption(140, 2024, "La Liga"),
    TopLeagueOption(135, 2024, "Serie A"),
    TopLeagueOption(78, 2024, "Bundesliga"),
    TopLeagueOption(61, 2024, "Ligue 1")
)

@Composable
fun ProfileScreen(
    onTeamClick: (Int) -> Unit,
    onLeagueClick: (leagueId: Int, season: Int) -> Unit,
    onTopScorersClick: (leagueId: Int, season: Int) -> Unit,
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    moreViewModel: MoreViewModel = hiltViewModel()
) {
    val teams by favoritesViewModel.favoriteTeams.collectAsStateWithLifecycle()
    val leagues by favoritesViewModel.favoriteLeagues.collectAsStateWithLifecycle()
    var showTopScorersDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
            item {
                Text(
                    text = stringResource(R.string.profile_saved_section),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            if (teams.isEmpty() && leagues.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.add_favorites_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                if (teams.isNotEmpty()) {
                    item { SectionHeader(stringResource(R.string.teams)) }
                    items(teams, key = { "team_${it.teamId}" }) { team ->
                        SwipeFavoriteTeamRow(
                            team = team,
                            onOpen = { onTeamClick(team.teamId) },
                            onRemove = { favoritesViewModel.removeTeam(team.teamId) }
                        )
                    }
                }
                if (leagues.isNotEmpty()) {
                    item { SectionHeader(stringResource(R.string.leagues_label)) }
                    items(leagues, key = { "league_${it.leagueId}" }) { league ->
                        SwipeFavoriteLeagueRow(
                            league = league,
                            onOpen = { onLeagueClick(league.leagueId, league.currentSeason) },
                            onRemove = { favoritesViewModel.removeLeague(league.leagueId) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.profile_tools_section),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            item {
                ProfileToolCard(
                    title = stringResource(R.string.top_scorers),
                    icon = Icons.Default.Star,
                    onClick = { showTopScorersDialog = true }
                )
            }
            item {
                ProfileToolCard(
                    title = stringResource(R.string.delete_favorites),
                    icon = Icons.Default.DeleteForever,
                    onClick = { showDeleteConfirmation = true }
                )
            }
    }

    if (showTopScorersDialog) {
        AlertDialog(
            onDismissRequest = { showTopScorersDialog = false },
            title = { Text(stringResource(R.string.select_league)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TOP_LEAGUE_OPTIONS.forEach { option ->
                        TextButton(
                            onClick = {
                                showTopScorersDialog = false
                                onTopScorersClick(option.leagueId, option.season)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(option.title, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTopScorersDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.delete_all_favorites)) },
            text = { Text(stringResource(R.string.delete_favorites_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    moreViewModel.clearAllFavorites()
                    showDeleteConfirmation = false
                }) {
                    Text(stringResource(R.string.delete), color = PrimaryRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun ProfileToolCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = LightGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = NavyPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeFavoriteTeamRow(
    team: FavoriteTeamEntity,
    onOpen: () -> Unit,
    onRemove: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove()
                true
            } else false
        }
    )
    LaunchedEffect(team.teamId) {
        dismissState.snapTo(SwipeToDismissBoxValue.Settled)
    }
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val scale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 1f,
                label = "iconScale"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryRed, RoundedCornerShape(8.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = White,
                    modifier = Modifier.scale(scale)
                )
            }
        },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpen),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = LightGray)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamLogo(imageLink = team.logo, size = 36.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = team.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = PrimaryRed)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeFavoriteLeagueRow(
    league: FavoriteLeagueEntity,
    onOpen: () -> Unit,
    onRemove: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove()
                true
            } else false
        }
    )
    LaunchedEffect(league.leagueId) {
        dismissState.snapTo(SwipeToDismissBoxValue.Settled)
    }
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val scale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 1f,
                label = "iconScaleLeague"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryRed, RoundedCornerShape(8.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = White,
                    modifier = Modifier.scale(scale)
                )
            }
        },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpen),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = LightGray)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamLogo(imageLink = league.logo, size = 36.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = league.name, style = MaterialTheme.typography.bodyLarge)
                    Text(text = league.country, style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = PrimaryRed)
                }
            }
        }
    }
}
