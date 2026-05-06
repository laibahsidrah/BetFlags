package xyz.appmaker.pbyvul.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import xyz.appmaker.pbyvul.R

sealed class RootDestination(
    val route: String,
    @StringRes val drawerLabelResId: Int,
    @StringRes val hubLabelResId: Int,
    @StringRes val screenTitleResId: Int,
    val icon: ImageVector
) {
    data object LiveCenter : RootDestination(
        route = "live_center",
        drawerLabelResId = R.string.nav_live_center,
        hubLabelResId = R.string.top_hub_live,
        screenTitleResId = R.string.live_screen_title,
        icon = Icons.Outlined.LiveTv
    )

    data object Schedule : RootDestination(
        route = "schedule",
        drawerLabelResId = R.string.nav_schedule,
        hubLabelResId = R.string.top_hub_matches,
        screenTitleResId = R.string.screen_schedule_title,
        icon = Icons.Outlined.CalendarMonth
    )

    data object Competitions : RootDestination(
        route = "competitions",
        drawerLabelResId = R.string.nav_competitions,
        hubLabelResId = R.string.top_hub_leagues,
        screenTitleResId = R.string.screen_competitions_title,
        icon = Icons.Outlined.EmojiEvents
    )

    data object MyProfile : RootDestination(
        route = "my_profile",
        drawerLabelResId = R.string.nav_my_profile,
        hubLabelResId = R.string.top_hub_profile,
        screenTitleResId = R.string.screen_my_profile_title,
        icon = Icons.Outlined.Person
    )

    companion object {
        val all: List<RootDestination> by lazy {
            listOf(LiveCenter, Schedule, Competitions, MyProfile)
        }

        val rootRoutes: Set<String> by lazy {
            all.map { it.route }.toSet()
        }
    }
}

object Routes {
    const val MATCH_DETAILS = "match_details/{fixtureId}"
    const val TEAM_DETAILS = "team_details/{teamId}"
    const val PLAYER_DETAILS = "player_details/{playerId}/{season}"
    const val STANDINGS = "standings/{leagueId}/{season}"
    const val TOP_SCORERS = "top_scorers/{leagueId}/{season}"

    fun matchDetails(fixtureId: Int) = "match_details/$fixtureId"
    fun teamDetails(teamId: Int) = "team_details/$teamId"
    fun playerDetails(playerId: Int, season: Int) = "player_details/$playerId/$season"
    fun standings(leagueId: Int, season: Int) = "standings/$leagueId/$season"
    fun topScorers(leagueId: Int, season: Int) = "top_scorers/$leagueId/$season"
}
