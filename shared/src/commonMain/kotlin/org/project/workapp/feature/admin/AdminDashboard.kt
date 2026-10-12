package org.project.workapp.feature.admin

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.launch
import org.project.workapp.core.database.LocalCache
import org.project.workapp.core.designsystem.AnimatedBottomNavBar
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.core.designsystem.NavItem
import org.project.workapp.feature.auth.AuthScreen
import org.project.workapp.feature.links.UsefulLinksScreen
import org.project.workapp.feature.main.MainScreenModel
import org.project.workapp.feature.main.SearchMode
import org.project.workapp.feature.schedule.ScheduleCard
import org.project.workapp.feature.search.CompactDirectoryCard
import org.project.workapp.feature.search.OfficeEmployeeCard
import org.project.workapp.feature.search.SearchModeToggle

@Composable
fun AdminDashboard(
    screenModel: MainScreenModel,
    navigator: Navigator
) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 2 })
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
                beyondViewportPageCount = 1
            ) { page ->
                when (page) {
                    0 -> UsefulLinksScreen(navigator)
                    1 -> AdminHomeScreen(screenModel, navigator)
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
                NavItem("Главная", Icons.Default.Home)
            ),
            accentColor = CustomColors.adminAccent,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    screenModel: MainScreenModel,
    navigator: Navigator
) {
    val state by screenModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var isSearchExpanded by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchExpanded) {
        if (isSearchExpanded) {
            searchFocusRequester.requestFocus()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 1200.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Локация",
                            tint = CustomColors.adminAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "1Galaxy | Офис",
                            color = CustomColors.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = {
                                val cache = LocalCache()
                                cache.clearSession()
                                navigator.replaceAll(AuthScreen())
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(CustomColors.surface.copy(alpha = 0.9f))
                                    .border(
                                        1.dp,
                                        CustomColors.surfaceBorder.copy(alpha = 0.5f),
                                        RoundedCornerShape(50)
                                    )
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
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(CustomColors.surface.copy(alpha = 0.6f))
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.3f),
                                            CustomColors.adminAccent.copy(alpha = 0.5f)
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = screenModel.userName.split(" ")
                                .take(2)
                                .joinToString("") { it.take(1) }
                                .uppercase()

                            Text(
                                text = initials,
                                color = CustomColors.adminAccent,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = screenModel.userName,
                                color = CustomColors.textPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = screenModel.userPosition,
                                color = CustomColors.textSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    SearchModeToggle(
                        currentMode = state.searchMode,
                        onModeSelected = { screenModel.setSearchMode(it) }
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "НЕДАВНИЕ ЗАПРОСЫ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CustomColors.textSecondary
                        )
                        if (state.recentSearches.isNotEmpty()) {
                            TextButton(
                                onClick = { screenModel.clearRecentSearches() },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text(
                                    "Очистить",
                                    color = CustomColors.adminAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (state.recentSearches.isEmpty()) {
                            Text(
                                "История поиска пуста",
                                color = CustomColors.textSecondary,
                                fontSize = 14.sp
                            )
                        } else {
                            state.recentSearches.forEach { searchEntry ->
                                val parts = searchEntry.split("|")
                                val query = parts.getOrElse(0) { searchEntry }
                                val count = parts.getOrElse(1) { "-" }
                                val modeStr = parts.getOrElse(2) { "RETAIL" }

                                RecentStoreCard(
                                    id = if (modeStr == "OFFICE") "Офис" else "Розница",
                                    name = query,
                                    address = "Нажмите для повтора",
                                    empCount = count,
                                    showCount = modeStr == "RETAIL",
                                    onClick = {
                                        searchQuery = ""
                                        screenModel.setSearchMode(if (modeStr == "OFFICE") SearchMode.OFFICE else SearchMode.RETAIL)
                                        screenModel.searchSchedule(query)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.searchResult != null) {
                            IconButton(
                                onClick = { screenModel.refreshSchedule() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Обновить",
                                    tint = CustomColors.adminAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        state.isLoading && state.hasSearched -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = CustomColors.primaryAccent)
                            }
                        }

                        state.error != null -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = state.error!!, color = Color(0xFFEF9A9A))
                            }
                        }

                        state.searchMode == SearchMode.RETAIL && state.searchResult != null -> {
                            state.searchResult!!.results.forEach { result ->
                                ScheduleCard(
                                    apiSchedule = result.schedule,
                                    storeTitle = result.storeName,
                                    contacts = result.contacts,
                                    currentDay = screenModel.currentDay,
                                    onClose = { screenModel.clearSearchResults() }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        state.searchMode == SearchMode.OFFICE && state.officeSearchResult != null -> {
                            state.officeSearchResult!!.results.forEach { emp ->
                                OfficeEmployeeCard(
                                    employee = emp,
                                    onClose = { screenModel.clearSearchResults() }
                                )
                            }
                        }

                        !state.hasSearched -> {
                            Text(
                                text = if (state.searchMode == SearchMode.RETAIL) "ВСЕ МАГАЗИНЫ" else "ВЕСЬ ОФИС",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CustomColors.textSecondary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            val meta = state.metaData
                            if (meta != null) {
                                val list = if (state.searchMode == SearchMode.RETAIL) {
                                    meta.stores.keys.toList().sorted()
                                } else {
                                    meta.admins.sorted()
                                }

                                list.forEach { itemName ->
                                    CompactDirectoryCard(
                                        title = itemName,
                                        onClick = {
                                            searchQuery = ""
                                            screenModel.searchSchedule(itemName)
                                            focusManager.clearFocus()
                                        }
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Введите запрос для поиска",
                                        color = CustomColors.textSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(170.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 16.dp)
                .padding(bottom = 128.dp)
                .widthIn(max = 500.dp)
                .then(if (isSearchExpanded) Modifier.fillMaxWidth() else Modifier)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (isSearchExpanded) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Поиск сотрудника или магазина...",
                            color = CustomColors.textSecondary.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(searchFocusRequester),
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CustomColors.surface.copy(alpha = 0.85f),
                        unfocusedContainerColor = CustomColors.surface.copy(alpha = 0.85f),
                        unfocusedBorderColor = CustomColors.surfaceBorder.copy(alpha = 0.5f),
                        focusedBorderColor = CustomColors.primaryAccent,
                        unfocusedTextColor = CustomColors.textPrimary,
                        focusedTextColor = CustomColors.textPrimary
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            screenModel.searchSchedule(searchQuery)
                            searchQuery = ""
                            focusManager.clearFocus()
                            isSearchExpanded = false
                        }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Поиск",
                                tint = CustomColors.primaryAccent
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            screenModel.searchSchedule(searchQuery)
                            searchQuery = ""
                            focusManager.clearFocus()
                            isSearchExpanded = false
                        }
                    )
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(CustomColors.surface.copy(alpha = 0.85f))
                        .border(1.dp, CustomColors.surfaceBorder.copy(alpha = 0.5f), CircleShape)
                        .clickable { isSearchExpanded = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Открыть поиск",
                        tint = CustomColors.primaryAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecentStoreCard(
    id: String,
    name: String,
    address: String,
    empCount: String,
    showCount: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = id,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CustomColors.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CustomColors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = address,
                fontSize = 12.sp,
                color = CustomColors.textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (showCount) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Сотрудники",
                        tint = CustomColors.adminAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = empCount,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CustomColors.textPrimary
                    )
                }
            }
        }
    }
}