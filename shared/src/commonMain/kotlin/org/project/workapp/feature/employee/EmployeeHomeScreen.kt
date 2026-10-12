package org.project.workapp.feature.employee

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import org.project.workapp.core.database.LocalCache
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.core.designsystem.liquidGlassCard
import org.project.workapp.core.utils.getCurrentRussianMonth
import org.project.workapp.feature.auth.AuthScreen
import org.project.workapp.feature.main.MainScreenModel
import org.project.workapp.feature.schedule.ArchiveScheduleBottomSheet
import org.project.workapp.feature.schedule.InfoCard
import org.project.workapp.feature.schedule.SalaryCard
import org.project.workapp.feature.schedule.ScheduleCard
import org.project.workapp.feature.schedule.ShiftStatus

@Composable
fun EmployeeHomeScreen(
    screenModel: MainScreenModel,
    navigator: Navigator
) {
    val state by screenModel.state.collectAsState()
    val userName = screenModel.userName
    val userStore = screenModel.userStore
    val uriHandler = LocalUriHandler.current
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColors.background)
    ) {
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
                            tint = CustomColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = userStore.uppercase(),
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
                                .border(1.dp, CustomColors.surfaceBorder.copy(alpha = 0.5f), RoundedCornerShape(50)),
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
                                Box(modifier = Modifier.width(1.dp).fillMaxHeight(0.4f).background(CustomColors.surfaceBorder))
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
                                Box(modifier = Modifier.width(1.dp).fillMaxHeight(0.4f).background(CustomColors.surfaceBorder))
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
                            val initials = userName.split(" ").take(2).joinToString("") { it.take(1) }.uppercase()
                            Text(
                                text = initials,
                                color = CustomColors.primaryAccent,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = userName,
                                color = CustomColors.textPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Сотрудник розницы",
                                color = CustomColors.inputLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        val hasAlarmAccess = screenModel.userPosition.contains("Директор", ignoreCase = true)

                        if (hasAlarmAccess) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = {
                                        val msg = handleAlarmAction(userStore, isArming = true)
                                        toastMessage = "Команда отправлена: $msg"
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CustomColors.surface.copy(alpha = 0.5f))
                                        .border(
                                            1.dp,
                                            Color(0xFFEF9A9A).copy(alpha = 0.4f),
                                            RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = "Включить",
                                        tint = Color(0xFFEF9A9A)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        val msg = handleAlarmAction(userStore, isArming = false)
                                        toastMessage = "Команда отправлена: $msg"
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CustomColors.surface.copy(alpha = 0.5f))
                                        .border(
                                            1.dp,
                                            Color(0xFFA5D6A7).copy(alpha = 0.4f),
                                            RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.LockOpen,
                                        contentDescription = "Выключить",
                                        tint = Color(0xFFA5D6A7)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

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
                            val apiData = state.scheduleData!!

                            var totalShifts = 0
                            var totalWeekends = 0
                            var totalVacs = 0
                            var totalSick = 0
                            var totalTimeOff = 0

                            apiData.results.forEach { storeResult ->
                                val mySchedule = storeResult.schedule.find {
                                    it.name.uppercase().contains(userName.split(" ")[0].uppercase())
                                }
                                mySchedule?.days?.forEach { day ->
                                    when (ShiftStatus.from(day.status)) {
                                        ShiftStatus.SHIFT, ShiftStatus.TRAINEE -> totalShifts++
                                        ShiftStatus.WEEKEND -> totalWeekends++
                                        ShiftStatus.TIME_OFF -> totalTimeOff++
                                        ShiftStatus.VACATION -> totalVacs++
                                        ShiftStatus.SICK_LEAVE -> totalSick++
                                        ShiftStatus.UNKNOWN -> {}
                                    }
                                }
                            }

                            val firstStore = apiData.results.firstOrNull()
                            val salaryData = firstStore?.salary
                            if (salaryData != null) {
                                val cashVal = salaryData.cash.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                                val cardVal = salaryData.card.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                                val totalBalance = cashVal + cardVal

                                val sheetUrl = when {
                                    firstStore.storeName?.uppercase()?.contains("СЕДАНКА") == true ->
                                        "https://docs.google.com/spreadsheets/d/1_P1MF7hW_MoGY9U-WXXmZv-WyeyhJPIzB0j3FR3e6nU/edit"
                                    firstStore.storeName?.uppercase()?.contains("КАЛИНА") == true ->
                                        "https://docs.google.com/spreadsheets/d/17vUUGGTMsoEgu5lU2zQQZG1qpPRutXS9W3KXV8iqe2o/edit"
                                    else -> null
                                }

                                SalaryCard(
                                    balance = totalBalance,
                                    onDoubleTap = {
                                        if (sheetUrl != null) {
                                            try { uriHandler.openUri(sheetUrl) } catch (_: Exception) {}
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            InfoCard(
                                month = getCurrentRussianMonth() + " 2026",
                                shiftsCount = totalShifts,
                                weekendsCount = totalWeekends,
                                vacationCount = totalVacs,
                                timeOffCount = totalTimeOff,
                                sickLeaveCount = totalSick
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            apiData.results.forEach { storeResult ->
                                ScheduleCard(
                                    apiSchedule = storeResult.schedule,
                                    storeTitle = storeResult.storeName,
                                    contacts = storeResult.contacts,
                                    currentDay = screenModel.currentDay,
                                    currentUserName = userName,
                                    availableMonths = state.metaData?.availableMonths ?: emptyList(),
                                    onMonthSelected = { selectedMonth ->
                                        screenModel.loadArchiveSchedule(userStore, selectedMonth)
                                    }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
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
                                        text = "Ведутся технические работы",
                                        color = CustomColors.textPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "График временно недоступен",
                                        color = CustomColors.textSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(90.dp))
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
                        Text("WA", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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