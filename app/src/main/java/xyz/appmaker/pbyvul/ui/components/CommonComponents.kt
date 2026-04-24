package xyz.appmaker.pbyvul.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import xyz.appmaker.pbyvul.R
import xyz.appmaker.pbyvul.util.normalizeForImageLoad
import xyz.appmaker.pbyvul.ui.theme.SportsbookAccent
import xyz.appmaker.pbyvul.ui.theme.GreenLive
import xyz.appmaker.pbyvul.ui.theme.NavyPrimary
import xyz.appmaker.pbyvul.ui.theme.SectionLeagueGrey
import xyz.appmaker.pbyvul.ui.theme.TextPrimary
import xyz.appmaker.pbyvul.ui.theme.White

/** Шапка корневых экранов: опционально кнопка меню для боковой панели. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldBetsTabTopBar(
    title: String,
    modifier: Modifier = Modifier,
    titleBold: Boolean = false,
    onMenuClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        modifier = modifier.fillMaxWidth(),
        windowInsets = TopAppBarDefaults.windowInsets,
        navigationIcon = {
            if (onMenuClick != null) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(R.string.open_menu),
                        tint = White
                    )
                }
            }
        },
        title = {
            Text(
                text = title,
                color = White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (titleBold) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = NavyPrimary,
            titleContentColor = White,
            navigationIconContentColor = White,
            actionIconContentColor = White
        )
    )
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = SportsbookAccent)
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.loading_error),
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun EmptyScreen(message: String = "", modifier: Modifier = Modifier) {
    val displayMessage = message.ifEmpty { stringResource(R.string.no_data) }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Loads remote images with the app [ImageLoader] (browser-like headers, protocol fix for //… links).
 */
@Composable
fun RemoteImage(
    imageLink: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null
) {
    val context = LocalContext.current
    val data = imageLink.normalizeForImageLoad()
    if (data == null) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
        )
        return
    }
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(data)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

@Composable
fun TeamLogo(imageLink: String?, size: Dp = 32.dp) {
    RemoteImage(
        imageLink = imageLink,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(4.dp)),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun LiveBadge() {
    val transition = rememberInfiniteTransition(label = "live")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = GreenLive.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(GreenLive.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.live_badge),
            style = MaterialTheme.typography.labelSmall,
            color = GreenLive
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SectionLeagueGrey)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = NavyPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )
    }
}

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.LightGray.copy(alpha = alpha))
    )
}
