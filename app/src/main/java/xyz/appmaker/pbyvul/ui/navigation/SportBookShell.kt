package xyz.appmaker.pbyvul.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.appmaker.pbyvul.R
import xyz.appmaker.pbyvul.ui.theme.SportsbookAccent
import xyz.appmaker.pbyvul.ui.theme.SportsbookSidebarBg
import xyz.appmaker.pbyvul.ui.theme.SportsbookTabActive
import xyz.appmaker.pbyvul.ui.theme.White

/**
 * Верхний хаб + скрываемое боковое меню только по футболу.
 */
@Composable
fun SportBookShell(
    selected: RootDestination,
    onSelectRoot: (RootDestination) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var sidebarVisible by rememberSaveable { mutableStateOf(true) }

    Column(modifier = modifier.fillMaxSize()) {
        SportBookTopHubBar(
            selected = selected,
            onSelect = onSelectRoot,
            sidebarVisible = sidebarVisible,
            onToggleSidebar = { sidebarVisible = !sidebarVisible },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedVisibility(
                visible = sidebarVisible,
                enter = slideInHorizontally { -it } + expandHorizontally(clip = false) + fadeIn(),
                exit = slideOutHorizontally { -it } + shrinkHorizontally(clip = false) + fadeOut()
            ) {
                SportBookSidebar(
                    selected = selected,
                    onSelectRoot = onSelectRoot,
                    modifier = Modifier
                        .width(156.dp)
                        .fillMaxHeight()
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(White)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SportBookTopHubBar(
    selected: RootDestination,
    onSelect: (RootDestination) -> Unit,
    sidebarVisible: Boolean,
    onToggleSidebar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val barBg = MaterialTheme.colorScheme.primary
    val scroll = rememberScrollState()
    Column(
        modifier = modifier
            .background(barBg)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            IconButton(onClick = onToggleSidebar) {
                Icon(
                    imageVector = if (sidebarVisible) {
                        Icons.AutoMirrored.Outlined.KeyboardArrowLeft
                    } else {
                        Icons.Filled.Menu
                    },
                    contentDescription = stringResource(
                        if (sidebarVisible) R.string.sidebar_hide else R.string.sidebar_show
                    ),
                    tint = White
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scroll)
                    .padding(end = 4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                RootDestination.all.forEach { dest ->
                    val isSel = dest == selected
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .clickable { onSelect(dest) }
                            .background(if (isSel) SportsbookTabActive else barBg)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(dest.hubLabelResId),
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            maxLines = 1,
                            letterSpacing = 0.3.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(28.dp)
                                .background(
                                    if (isSel) SportsbookAccent else Color.Transparent,
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SportBookSidebar(
    selected: RootDestination,
    onSelectRoot: (RootDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SportsbookSidebarBg)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                Icons.Outlined.SportsSoccer,
                contentDescription = null,
                tint = White,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(R.string.sidebar_group_football),
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        SidebarNavRow(
            icon = Icons.Outlined.LiveTv,
            label = stringResource(R.string.sidebar_football_live),
            selected = selected == RootDestination.LiveCenter,
            onClick = { onSelectRoot(RootDestination.LiveCenter) }
        )
        SidebarNavRow(
            icon = Icons.Outlined.CalendarMonth,
            label = stringResource(R.string.sidebar_football_calendar),
            selected = selected == RootDestination.Schedule,
            onClick = { onSelectRoot(RootDestination.Schedule) }
        )
        SidebarNavRow(
            icon = Icons.Outlined.EmojiEvents,
            label = stringResource(R.string.sidebar_football_leagues),
            selected = selected == RootDestination.Competitions,
            onClick = { onSelectRoot(RootDestination.Competitions) }
        )
        SidebarNavRow(
            icon = Icons.AutoMirrored.Outlined.ListAlt,
            label = stringResource(R.string.sidebar_my_list),
            selected = selected == RootDestination.MyList,
            onClick = { onSelectRoot(RootDestination.MyList) }
        )
    }
}

@Composable
private fun SidebarNavRow(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(if (selected) SportsbookTabActive.copy(alpha = 0.35f) else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = White, modifier = Modifier.width(22.dp))
        Text(
            text = label,
            color = White,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp)
        )
    }
}
