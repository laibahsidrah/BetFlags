package xyz.appmaker.pbyvul.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import xyz.appmaker.pbyvul.ui.screens.leagues.LeaguesScreen
import xyz.appmaker.pbyvul.ui.screens.live.LiveScreen
import xyz.appmaker.pbyvul.ui.screens.matchdetails.MatchDetailsScreen
import xyz.appmaker.pbyvul.ui.screens.matches.MatchesScreen
import xyz.appmaker.pbyvul.ui.screens.playerdetails.PlayerDetailsScreen
import xyz.appmaker.pbyvul.ui.screens.profile.ProfileScreen
import xyz.appmaker.pbyvul.ui.screens.standings.StandingsScreen
import xyz.appmaker.pbyvul.ui.screens.teamdetails.TeamDetailsScreen
import xyz.appmaker.pbyvul.ui.screens.topscorers.TopScorersScreen

@Composable
fun BetFlagsNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isAtRoot = currentRoute in RootDestination.rootRoutes

    BackHandler(enabled = isAtRoot) { }

    val navigateRoot: (RootDestination) -> Unit = { dest ->
        navController.navigate(dest.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = RootDestination.LiveCenter.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(RootDestination.LiveCenter.route) {
            SportBookShell(
                selected = RootDestination.LiveCenter,
                onSelectRoot = navigateRoot
            ) {
                LiveScreen(
                    onFixtureClick = { navController.navigate(Routes.matchDetails(it)) }
                )
            }
        }
        composable(RootDestination.Schedule.route) {
            SportBookShell(
                selected = RootDestination.Schedule,
                onSelectRoot = navigateRoot
            ) {
                MatchesScreen(
                    onFixtureClick = { navController.navigate(Routes.matchDetails(it)) }
                )
            }
        }
        composable(RootDestination.Competitions.route) {
            SportBookShell(
                selected = RootDestination.Competitions,
                onSelectRoot = navigateRoot
            ) {
                LeaguesScreen(
                    onLeagueClick = { leagueId, season ->
                        navController.navigate(Routes.standings(leagueId, season))
                    }
                )
            }
        }
        composable(RootDestination.MyProfile.route) {
            SportBookShell(
                selected = RootDestination.MyProfile,
                onSelectRoot = navigateRoot
            ) {
                ProfileScreen(
                    onTeamClick = { navController.navigate(Routes.teamDetails(it)) },
                    onLeagueClick = { leagueId, season ->
                        navController.navigate(Routes.standings(leagueId, season))
                    },
                    onTopScorersClick = { leagueId, season ->
                        navController.navigate(Routes.topScorers(leagueId, season))
                    }
                )
            }
        }

        composable(
            Routes.MATCH_DETAILS,
            arguments = listOf(navArgument("fixtureId") { type = NavType.IntType })
        ) {
            MatchDetailsScreen(
                onBack = { navController.popBackStack() },
                onTeamClick = { navController.navigate(Routes.teamDetails(it)) },
                onPlayerClick = { playerId, season ->
                    navController.navigate(Routes.playerDetails(playerId, season))
                },
                onFixtureClick = { navController.navigate(Routes.matchDetails(it)) }
            )
        }

        composable(
            Routes.TEAM_DETAILS,
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) {
            TeamDetailsScreen(
                onBack = { navController.popBackStack() },
                onPlayerClick = { playerId, season ->
                    navController.navigate(Routes.playerDetails(playerId, season))
                },
                onFixtureClick = { navController.navigate(Routes.matchDetails(it)) }
            )
        }

        composable(
            Routes.PLAYER_DETAILS,
            arguments = listOf(
                navArgument("playerId") { type = NavType.IntType },
                navArgument("season") { type = NavType.IntType }
            )
        ) {
            PlayerDetailsScreen(onBack = { navController.popBackStack() })
        }

        composable(
            Routes.STANDINGS,
            arguments = listOf(
                navArgument("leagueId") { type = NavType.IntType },
                navArgument("season") { type = NavType.IntType }
            )
        ) {
            StandingsScreen(
                onBack = { navController.popBackStack() },
                onTeamClick = { navController.navigate(Routes.teamDetails(it)) }
            )
        }

        composable(
            Routes.TOP_SCORERS,
            arguments = listOf(
                navArgument("leagueId") { type = NavType.IntType },
                navArgument("season") { type = NavType.IntType }
            )
        ) {
            TopScorersScreen(
                onBack = { navController.popBackStack() },
                onPlayerClick = { playerId, season ->
                    navController.navigate(Routes.playerDetails(playerId, season))
                }
            )
        }
    }
}
