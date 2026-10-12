package org.project.workapp.feature.regional

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.project.workapp.core.database.LocalCache
import org.project.workapp.core.designsystem.AnimatedBottomNavBar
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.core.designsystem.NavItem
import org.project.workapp.core.designsystem.liquidGlassCard
import org.project.workapp.feature.auth.AuthScreen
import org.project.workapp.feature.links.EmployeeLinksScreen
import org.project.workapp.feature.main.MainScreenModel
import org.project.workapp.feature.schedule.ArchiveScheduleBottomSheet
import org.project.workapp.feature.schedule.ScheduleCard
import org.project.workapp.feature.search.EmployeeSearchScreen

@Composable
fun RegionalDashboard(
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
                    1 -> RegionalHomeScreen(screenModel, navigator)
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

@Composable
fun RegionalHomeScreen(
    screenModel: MainScreenModel,
    navigator: Navigator
) {
    val state by screenModel.state.collectAsState()
    val userName = screenModel.userName
    val userPosition = screenModel.userPosition
    val localCache = remember { LocalCache() }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.error) {
        if (state.error != null) {
            toastMessage = state.error
            screenModel.clearError()
        }
    }

    LaunchedEffect(state.syncSuccessEvent) {
        if (state.syncSuccessEvent) {
            toastMessage = "Данные успешно обновлены"
            screenModel.clearSyncSuccessEvent()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 1200.dp)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Локация",
                            tint = CustomColors.primaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "1GALAXY | РЕГИОН",
                            color = CustomColors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier
                                .height(36.dp)
                                .clip(RoundedCornerShape(50))
                                .background(CustomColors.surface.copy(alpha = 0.9f))
                                .border(
                                    1.dp,
                                    CustomColors.surfaceBorder.copy(alpha = 0.5f),
                                    RoundedCornerShape(50)
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.isSyncing) {
                                Box(
                                    modifier = Modifier.size(36.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = CustomColors.primaryAccent,
                                        strokeWidth = 2.dp
                                    )
                                }
                                Box(
                                    modifier = Modifier.width(1.dp).fillMaxHeight(0.4f)
                                        .background(CustomColors.surfaceBorder)
                                )
                            } else if (state.scheduleData != null) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable { screenModel.refreshSchedule() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Обновить",
                                        tint = CustomColors.primaryAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier.width(1.dp).fillMaxHeight(0.4f)
                                        .background(CustomColors.surfaceBorder)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .clickable {
                                        val cache = LocalCache()
                                        cache.clearSession()
                                        navigator.replaceAll(AuthScreen())
                                    }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Выйти",
                                    color = CustomColors.primaryAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(CustomColors.surface.copy(alpha = 0.6f))
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.3f),
                                            CustomColors.primaryAccent.copy(alpha = 0.5f)
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials =
                                userName.split(" ").take(2).joinToString("") { it.take(1) }
                                    .uppercase()
                            Text(
                                text = initials,
                                color = CustomColors.primaryAccent,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName,
                                color = CustomColors.textPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = userPosition,
                                color = CustomColors.textSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "МАГАЗИНЫ КОРЗИНЫ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CustomColors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        state.isLoading && state.scheduleData == null -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = CustomColors.primaryAccent)
                            }
                        }

                        state.error != null && state.scheduleData == null -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = state.error!!, color = Color(0xFFEF9A9A))
                            }
                        }

                        state.scheduleData != null -> {
                            val results = state.scheduleData!!.results
                            if (results.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(100.dp)
                                        .liquidGlassCard(alpha = 0.08f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "За Вами пока не закреплены магазины",
                                        color = CustomColors.textSecondary
                                    )
                                }
                            } else {
                                val availableStoreNames = remember(results) {
                                    results.mapNotNull { it.storeName }.filter { it.isNotBlank() }
                                }

                                var selectedStore by remember {
                                    val cached = localCache.getLastSelectedRegionalStore()
                                    mutableStateOf(
                                        if (cached.isNotBlank() && cached in availableStoreNames) cached
                                        else availableStoreNames.firstOrNull() ?: ""
                                    )
                                }

                                LaunchedEffect(availableStoreNames) {
                                    if (selectedStore !in availableStoreNames) {
                                        val cached = localCache.getLastSelectedRegionalStore()
                                        selectedStore = if (cached.isNotBlank() && cached in availableStoreNames) cached
                                        else availableStoreNames.firstOrNull() ?: ""
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    availableStoreNames.forEach { storeName ->
                                        val isSelected = storeName == selectedStore
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(
                                                    if (isSelected) CustomColors.primaryAccent
                                                    else CustomColors.surface.copy(alpha = 0.85f)
                                                )
                                                .then(
                                                    if (!isSelected) {
                                                        Modifier.border(
                                                            1.dp,
                                                            CustomColors.surfaceBorder.copy(alpha = 0.5f),
                                                            RoundedCornerShape(50)
                                                        )
                                                    } else Modifier
                                                )
                                                .clickable {
                                                    selectedStore = storeName
                                                    localCache.saveLastSelectedRegionalStore(storeName)
                                                }
                                                .padding(horizontal = 14.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = storeName,
                                                color = if (isSelected) Color.White else CustomColors.textSecondary,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                val selectedResult = results.find { it.storeName == selectedStore } ?: results.firstOrNull()
                                if (selectedResult != null) {
                                    ScheduleCard(
                                        apiSchedule = selectedResult.schedule,
                                        storeTitle = selectedResult.storeName,
                                        contacts = selectedResult.contacts,
                                        currentDay = screenModel.currentDay,
                                        isCollapsible = false,
                                        initiallyExpanded = true,
                                        availableMonths = state.metaData?.availableMonths ?: emptyList(),
                                        onMonthSelected = { month ->
                                            screenModel.loadArchiveSchedule(
                                                selectedResult.storeName ?: "",
                                                month
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }

        LaunchedEffect(toastMessage) {
            if (toastMessage != null) {
                delay(3000.milliseconds)
                toastMessage = null
            }
        }

        AnimatedVisibility(
            visible = toastMessage != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF1E1A29).copy(alpha = 0.95f))
                    .border(1.dp, CustomColors.primaryAccent.copy(alpha = 0.5f), RoundedCornerShape(50))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.Black, CircleShape)
                            .border(1.dp, CustomColors.primaryAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1G", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = toastMessage ?: "",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        ArchiveScheduleBottomSheet(
            state = state,
            onDismiss = { screenModel.closeArchiveSheet() }
        )
    }
}