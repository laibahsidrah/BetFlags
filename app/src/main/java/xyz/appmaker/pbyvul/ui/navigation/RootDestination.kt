package xyz.appmaker.pbyvul.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.ui.graphics.vector.ImageVector
import xyz.appmaker.pbyvul.R

/**
 * Корневые разделы приложения: боковое меню (не нижняя панель), порядок и имена маршрутов
 * отличаются от прежней схемы «вкладок», функционал экранов тот же.
 */
sealed class RootDestination(
    val route: String,
    @StringRes val drawerLabelResId: Int,
    /** Короткая подпись верхнего «хаба» (CAPS как у букмекера). */
    @StringRes val hubLabelResId: Int,
    @StringRes val screenTitleResId: Int,
    val icon: ImageVector
) {
    /** LIVE — первый пункт (как в линейке live-ставок). */
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

    data object MyList : RootDestination(
        route = "my_list",
        drawerLabelResId = R.string.nav_my_list,
        hubLabelResId = R.string.top_hub_saved,
        screenTitleResId = R.string.screen_my_list_title,
        icon = Icons.Outlined.StarOutline
    )

    companion object {
        /**
         * Lazy — иначе при <clinit> companion может обратиться к вложенным `data object`
         * до завершения их инициализации (ExceptionInInitializerError / NPE на .route).
         */
        val all: List<RootDestination> by lazy {
            listOf(LiveCenter, Schedule, Competitions, MyList)
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
