package org.project.workapp.feature.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.core.designsystem.liquidGlassCard
import org.project.workapp.feature.main.MainScreenModel
import org.project.workapp.feature.main.SearchMode
import org.project.workapp.feature.schedule.ScheduleCard

@Composable
fun EmployeeSearchScreen(
    screenModel: MainScreenModel,
    navigator: Navigator
) {
    val state by screenModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val searchFocusRequester = remember { FocusRequester() }
    var isSearchFocused by remember { mutableStateOf(false) }

    val bottomOffset by animateDpAsState(
        targetValue = if (isSearchFocused) 16.dp else 120.dp,
        label = "fabBottomOffset"
    )

    val buttonAlpha by animateFloatAsState(
        targetValue = if (isSearchFocused) 0.55f else 1.0f,
        label = "fabAlpha"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    .widthIn(max = 600.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Переключатель режимов
                    SearchModeToggle(
                        currentMode = state.searchMode,
                        onModeSelected = { screenModel.setSearchMode(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Поиск сотрудника, магазина...",
                                color = CustomColors.inputLabel.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .focusRequester(searchFocusRequester)
                            .onFocusChanged { isSearchFocused = it.isFocused },
                        shape = RoundedCornerShape(50),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Поиск",
                                tint = CustomColors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Очистить",
                                        tint = CustomColors.textSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CustomColors.surface.copy(alpha = 0.85f),
                            unfocusedContainerColor = CustomColors.surface.copy(alpha = 0.85f),
                            unfocusedBorderColor = CustomColors.surfaceBorder.copy(alpha = 0.5f),
                            focusedBorderColor = CustomColors.primaryAccent,
                            unfocusedTextColor = CustomColors.textPrimary,
                            focusedTextColor = CustomColors.textPrimary
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchQuery.isNotBlank()) {
                                    screenModel.searchSchedule(searchQuery)
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Блок недавних запросов
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
                                    text = "Очистить",
                                    color = CustomColors.primaryAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Горизонтальная лента компактных чипсов
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.recentSearches.isEmpty()) {
                            Text(
                                text = "История поиска пуста",
                                color = CustomColors.textSecondary,
                                fontSize = 13.sp
                            )
                        } else {
                            state.recentSearches.forEach { searchEntry ->
                                val parts = searchEntry.split("|")
                                val query = parts.getOrElse(0) { searchEntry }
                                val count = parts.getOrElse(1) { "-" }
                                val modeStr = parts.getOrElse(2) { "RETAIL" }

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(CustomColors.surface.copy(alpha = 0.85f))
                                        .border(
                                            1.dp,
                                            CustomColors.surfaceBorder.copy(alpha = 0.4f),
                                            RoundedCornerShape(50)
                                        )
                                        .clickable {
                                            searchQuery = ""
                                            screenModel.setSearchMode(if (modeStr == "OFFICE") SearchMode.OFFICE else SearchMode.RETAIL)
                                            screenModel.searchSchedule(query)
                                            focusManager.clearFocus()
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (modeStr == "RETAIL") {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(CustomColors.primaryAccent, CircleShape)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.People,
                                            contentDescription = null,
                                            tint = CustomColors.textSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    val label = if (modeStr == "RETAIL" && count != "-") "$query ($count сотр.)" else query
                                    Text(
                                        text = label,
                                        color = CustomColors.textPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Результаты поиска или справочник
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .liquidGlassCard(alpha = 0.08f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Введите запрос для поиска", color = CustomColors.textSecondary)
                                }
                            }
                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .liquidGlassCard(alpha = 0.08f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Ничего не найдено",
                                        color = CustomColors.textPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Попробуйте изменить запрос",
                                        color = CustomColors.textSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }

        // Плавающая кнопка поиска (FAB) с динамическим отступом и прозрачностью
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .imePadding()
                .padding(end = 20.dp, bottom = bottomOffset)
                .size(56.dp)
                .clip(CircleShape)
                .background(CustomColors.primaryAccent.copy(alpha = buttonAlpha))
                .border(
                    width = 1.dp,
                    color = if (isSearchFocused) Color.White.copy(alpha = 0.3f) else Color.Transparent,
                    shape = CircleShape
                )
                .clickable {
                    if (isSearchFocused) {
                        if (searchQuery.isNotBlank()) {
                            screenModel.searchSchedule(searchQuery)
                        }
                        focusManager.clearFocus()
                    } else {
                        searchFocusRequester.requestFocus()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Поиск",
                tint = Color.White.copy(alpha = if (isSearchFocused) 0.85f else 1.0f),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
fun CompactDirectoryCard(title: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CustomColors.surface.copy(alpha = 0.6f))
                .border(
                    1.dp,
                    CustomColors.surfaceBorder.copy(alpha = 0.4f),
                    RoundedCornerShape(14.dp)
                )
                .clickable { onClick() }
                .liquidGlassCard(alpha = 0.05f, shape = RoundedCornerShape(14.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CustomColors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Перейти",
                    tint = CustomColors.textSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}