package xyz.appmaker.pbyvul.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = White,
    primaryContainer = ScoreBlockNavy,
    onPrimaryContainer = White,
    secondary = GoldAccent,
    onSecondary = NavyPrimary,
    secondaryContainer = GoldAccent.copy(alpha = 0.35f),
    onSecondaryContainer = NavyPrimary,
    tertiary = TealLiveTime,
    onTertiary = White,
    background = White,
    onBackground = TextPrimary,
    surface = White,
    onSurface = TextPrimary,
    surfaceVariant = SectionLeagueGrey,
    onSurfaceVariant = DarkGray,
    outline = NavyPrimary.copy(alpha = 0.35f),
    error = PrimaryRed
)

@Composable
fun BetFlagsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
