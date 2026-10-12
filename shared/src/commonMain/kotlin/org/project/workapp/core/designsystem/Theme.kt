package org.project.workapp.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val customColors = DarkCustomColorsPalette

    val materialColors = darkColorScheme(
        primary = customColors.primaryAccent,
        background = customColors.background,
        surface = customColors.surface,
        onBackground = customColors.textPrimary,
        onSurface = customColors.textPrimary
    )

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = AppTypography,
            content = content
        )
    }
}