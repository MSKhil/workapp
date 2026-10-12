package org.project.workapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import org.project.workapp.core.database.LocalCache
import org.project.workapp.feature.auth.AuthScreen
import org.project.workapp.feature.main.MainScreen
import org.project.workapp.core.designsystem.AppTheme

@Preview
@Composable
fun App() {
    AppTheme {
        val cache = LocalCache()
        val startScreen = if (!cache.getUserName().isNullOrEmpty()) {
            MainScreen()
        } else {
            AuthScreen()
        }

        Navigator(screen = startScreen)
    }
}
