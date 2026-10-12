package org.project.workapp.feature.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.launch
import org.project.workapp.core.designsystem.AnimatedBottomNavBar
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.core.designsystem.NavItem
import org.project.workapp.feature.links.EmployeeLinksScreen
import org.project.workapp.feature.main.MainScreenModel
import org.project.workapp.feature.search.EmployeeSearchScreen

@Composable
fun EmployeeDashboard(
    screenModel: MainScreenModel,
    navigator: Navigator
) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColors.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .clip(RectangleShape)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 2
            ) { page ->
                when (page) {
                    0 -> EmployeeLinksScreen(navigator)
                    1 -> EmployeeHomeScreen(screenModel, navigator)
                    2 -> EmployeeSearchScreen(screenModel, navigator)
                }
            }
        }

        AnimatedBottomNavBar(
            selectedItem = pagerState.currentPage,
            onItemSelected = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
            items = listOf(
                NavItem("Инфо", Icons.Default.LocalOffer),
                NavItem("Главная", Icons.Default.Home),
                NavItem("Поиск", Icons.Default.Search)
            ),
            accentColor = CustomColors.primaryAccent,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}