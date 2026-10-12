package org.project.workapp.feature.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.core.designsystem.liquidGlassCard
import org.project.workapp.core.network.EmployeeSchedule
import org.project.workapp.core.network.StoreContacts
import org.project.workapp.core.utils.getCurrentRussianMonth
import org.project.workapp.core.utils.getRussianDayOfWeek

@Composable
fun ScheduleCard(
    modifier: Modifier = Modifier,
    apiSchedule: List<EmployeeSchedule> = emptyList(),
    storeTitle: String? = null,
    contacts: StoreContacts? = null,
    currentDay: String = "",
    currentUserName: String? = null,
    isCollapsible: Boolean = false,
    initiallyExpanded: Boolean = true,
    displayedMonth: String? = null,
    availableMonths: List<String> = emptyList(),
    onMonthSelected: ((String) -> Unit)? = null,
    onClose: (() -> Unit)? = null
) {
    val secText = CustomColors.textSecondary
    val density = LocalDensity.current

    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    val hasContacts =
        contacts != null && (contacts.internal.isNotBlank() || contacts.phone.isNotBlank() || contacts.email.isNotBlank())
    var isContactsExpanded by remember { mutableStateOf(false) }
    var isMonthMenuExpanded by remember { mutableStateOf(false) }

    val highlightColor = CustomColors.primaryAccent

    val smoothFrostGradient = Brush.horizontalGradient(
        0.0f to CustomColors.surface,
        0.75f to CustomColors.surface.copy(alpha = 0.98f),
        0.95f to CustomColors.surface.copy(alpha = 0.3f),
        1.0f to Color.Transparent
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .liquidGlassCard(alpha = 0.08f)
    ) {
        Column(modifier = Modifier.padding(bottom = if (isExpanded) 20.dp else 0.dp)) {
            // Верхний заголовок карточки
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (isCollapsible) {
                                isExpanded = !isExpanded
                            } else if (hasContacts) {
                                isContactsExpanded = !isContactsExpanded
                            }
                        }
                    )
                    .padding(start = 20.dp, end = 16.dp, top = 18.dp, bottom = 8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = storeTitle?.takeIf { it.isNotBlank() } ?: "График магазина",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CustomColors.textPrimary
                    )
                    if (isCollapsible && !isExpanded) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${apiSchedule.size} сотр.",
                            fontSize = 12.sp,
                            color = CustomColors.textSecondary
                        )
                    }
                }

                if (isExpanded) {
                    if (hasContacts) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isContactsExpanded = !isContactsExpanded }
                        ) {
                            Text(
                                text = "Контакты",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = CustomColors.textSecondary,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Icon(
                                imageVector = if (isContactsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Скрыть/Показать",
                                tint = CustomColors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (onClose != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Закрыть",
                                tint = CustomColors.textSecondary
                            )
                        }
                    }
                }

                if (isCollapsible) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                        tint = CustomColors.primaryAccent
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    if (hasContacts) {
                        AnimatedVisibility(visible = isContactsExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (contacts!!.internal.isNotBlank()) ContactRow(
                                    Icons.Default.PhoneInTalk,
                                    contacts.internal,
                                    "Внутренний"
                                )
                                if (contacts.phone.isNotBlank()) ContactRow(
                                    Icons.Default.Phone,
                                    contacts.phone,
                                    "Телефон"
                                )
                                if (contacts.email.isNotBlank()) ContactRow(
                                    Icons.Default.Email,
                                    contacts.email,
                                    "Email"
                                )
                            }
                        }
                        if (!isContactsExpanded) Spacer(modifier = Modifier.height(4.dp))
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (apiSchedule.isNotEmpty()) {
                        val firstEmpDays = apiSchedule.first().days
                        val (monthIdx, monthNum, year) = parseMonthInfo(displayedMonth)

                        val headers = firstEmpDays.map { workDay ->
                            val dayInt = workDay.date.toIntOrNull() ?: 1
                            val dOfWeek = getRussianDayOfWeek(dayInt, monthIdx, year)
                            DayHeader(
                                dayOfWeek = dOfWeek,
                                date = workDay.date,
                                fullDate = "${workDay.date}.$monthNum",
                                isToday = (workDay.date == currentDay && (displayedMonth == null || displayedMonth.contains(
                                    getCurrentRussianMonth(), ignoreCase = true)))
                            )
                        }

                        val todayIndex = headers.indexOfFirst { it.isToday }

                        val scrollState = rememberScrollState()
                        val scope = rememberCoroutineScope()

                        val stopParentPagerScroll = remember {
                            object : NestedScrollConnection {
                                override fun onPostScroll(
                                    consumed: Offset,
                                    available: Offset,
                                    source: NestedScrollSource
                                ): Offset {
                                    return Offset(x = available.x, y = 0f)
                                }

                                override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                                    return Velocity(x = available.x, y = 0f)
                                }
                            }
                        }

                        LaunchedEffect(headers) {
                            if (todayIndex != -1) {
                                val itemWidth = 62.dp
                                val targetPx = with(density) { (itemWidth * todayIndex).toPx().roundToInt() }

                                var retries = 0
                                while (scrollState.maxValue == 0 && retries < 20) {
                                    delay(50)
                                    retries++
                                }
                                delay(50)
                                scrollState.animateScrollTo(targetPx)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .nestedScroll(stopParentPagerScroll)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(scrollState)
                                    .drawWithContent {
                                        drawContent()

                                        if (todayIndex != -1) {
                                            val paddingPx = 3.dp.roundToPx()
                                            val cellWidthPx = 56.dp.roundToPx()
                                            val spacingPx = 6.dp.roundToPx()
                                            val initialSpacerPx = 88.dp.roundToPx()

                                            val xOffset = (initialSpacerPx + spacingPx + todayIndex * (cellWidthPx + spacingPx) - paddingPx).toFloat()
                                            val yOffset = 0f

                                            val highlightSize = Size(
                                                width = (cellWidthPx + paddingPx * 2).toFloat(),
                                                height = size.height
                                            )
                                            val radius = CornerRadius(14.dp.toPx())

                                            drawRoundRect(
                                                color = highlightColor.copy(alpha = 0.12f),
                                                topLeft = Offset(x = xOffset, y = yOffset),
                                                size = highlightSize,
                                                cornerRadius = radius
                                            )

                                            drawRoundRect(
                                                color = highlightColor,
                                                topLeft = Offset(x = xOffset, y = yOffset),
                                                size = highlightSize,
                                                cornerRadius = radius,
                                                style = Stroke(width = 2.dp.toPx())
                                            )
                                        }
                                    }
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    Spacer(modifier = Modifier.width(88.dp))

                                    headers.forEach { header ->
                                        val isWeekend = header.dayOfWeek == "Сб" || header.dayOfWeek == "Вс"
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.width(56.dp)
                                        ) {
                                            Text(
                                                text = header.dayOfWeek,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (isWeekend) CustomColors.weekendsText else secText
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))

                                            val dateBg = Color.Transparent
                                            val dateTextColor = if (header.isToday) CustomColors.primaryAccent else if (isWeekend) CustomColors.weekendsText else CustomColors.textPrimary

                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(dateBg, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = header.date,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = dateTextColor
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                apiSchedule.forEachIndexed { empIndex, emp ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.height(58.dp)
                                    ) {
                                        Spacer(modifier = Modifier.width(88.dp))

                                        headers.forEachIndexed { index, header ->
                                            val workDay = emp.days.getOrNull(index)
                                            val shiftStatus = ShiftStatus.from(workDay?.status)

                                            val contentText = when (shiftStatus) {
                                                ShiftStatus.SHIFT, ShiftStatus.TRAINEE -> workDay?.time?.replace("-", "\n-\n") ?: ""
                                                ShiftStatus.WEEKEND -> "Вых"
                                                ShiftStatus.VACATION -> "Отп"
                                                ShiftStatus.TIME_OFF -> "Отпр"
                                                ShiftStatus.SICK_LEAVE -> "Бол"
                                                ShiftStatus.UNKNOWN -> "-"
                                            }

                                            val cellShape = RoundedCornerShape(12.dp)
                                            val cellBorderWidth = 1.dp
                                            val cellBorderColor = shiftStatus.borderColor()

                                            Box(
                                                modifier = Modifier
                                                    .width(56.dp)
                                                    .height(58.dp)
                                                    .background(shiftStatus.badgeBackground(), cellShape)
                                                    .border(cellBorderWidth, cellBorderColor, cellShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = contentText,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = shiftStatus.textColor(secText),
                                                    textAlign = TextAlign.Center,
                                                    lineHeight = 13.sp
                                                )
                                            }
                                        }
                                    }

                                    if (empIndex < apiSchedule.lastIndex) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .width(104.dp)
                                    .drawBehind {
                                        val bleed = 4.dp.toPx()
                                        drawRect(
                                            brush = smoothFrostGradient,
                                            topLeft = Offset(0f, -bleed),
                                            size = size.copy(height = size.height + bleed * 2)
                                        )
                                    }
                                    .draggable(
                                        orientation = Orientation.Horizontal,
                                        state = rememberDraggableState { delta ->
                                            scope.launch {
                                                scrollState.scrollBy(-delta)
                                            }
                                        }
                                    )
                            ) {
                                val currentMonthTitle = displayedMonth ?: "${getCurrentRussianMonth()} 2026"
                                val monthName = currentMonthTitle.split(" ").firstOrNull() ?: currentMonthTitle
                                val isPickerEnabled = onMonthSelected != null && availableMonths.isNotEmpty()

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .width(88.dp)
                                            .height(28.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(CustomColors.searchBg)
                                            .border(
                                                width = 1.dp,
                                                color = if (isPickerEnabled) CustomColors.primaryAccent.copy(alpha = 0.5f) else CustomColors.surfaceBorder,
                                                shape = RoundedCornerShape(50)
                                            )
                                            .clickable(enabled = isPickerEnabled) { isMonthMenuExpanded = true }
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = monthName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPickerEnabled) CustomColors.textPrimary else CustomColors.textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (isPickerEnabled) {
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Выбрать месяц",
                                                tint = CustomColors.primaryAccent,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = isMonthMenuExpanded,
                                        onDismissRequest = { isMonthMenuExpanded = false },
                                        modifier = Modifier
                                            .background(CustomColors.surface)
                                            .border(1.dp, CustomColors.surfaceBorder, RoundedCornerShape(12.dp))
                                    ) {
                                        availableMonths.forEach { mName ->
                                            DropdownMenuItem(
                                                text = { Text(mName, color = CustomColors.textPrimary, fontSize = 13.sp) },
                                                onClick = {
                                                    isMonthMenuExpanded = false
                                                    onMonthSelected?.invoke(mName)
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                apiSchedule.forEachIndexed { empIndex, emp ->
                                    val nameParts = emp.name.trim().split(" ")
                                    val lastName = nameParts.firstOrNull() ?: emp.name
                                    val firstName = nameParts.drop(1).joinToString(" ")

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(58.dp)
                                            .padding(top = 4.dp, bottom = 4.dp, end = 16.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxHeight(),
                                            verticalArrangement = Arrangement.spacedBy((-2).dp, Alignment.CenterVertically)
                                        ) {
                                            Text(
                                                text = lastName,
                                                color = CustomColors.textPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                lineHeight = 13.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (firstName.isNotBlank()) {
                                                Text(
                                                    text = firstName,
                                                    color = CustomColors.inputLabel,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    lineHeight = 11.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }

                                    if (empIndex < apiSchedule.lastIndex) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }
                            }
                        }

                        if (scrollState.maxValue > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .height(4.dp)
                                    .background(CustomColors.surfaceBorder.copy(alpha = 0.2f), CircleShape)
                            ) {
                                val maxWidthPx = maxWidth.value
                                val fraction = if (scrollState.maxValue > 0)
                                    scrollState.value.toFloat() / scrollState.maxValue
                                else 0f

                                val thumbWidthPercent = 0.25f
                                val thumbWidthPx = maxWidthPx * thumbWidthPercent
                                val thumbOffsetPx = (maxWidthPx - thumbWidthPx) * fraction

                                Box(
                                    modifier = Modifier
                                        .offset(x = thumbOffsetPx.dp)
                                        .width(thumbWidthPx.dp)
                                        .fillMaxHeight()
                                        .background(CustomColors.primaryAccent.copy(alpha = 0.6f), CircleShape)
                                        .pointerInput(Unit) {
                                            detectDragGestures { change, dragAmount ->
                                                change.consume()
                                                val deltaFraction = dragAmount.x / (maxWidthPx - thumbWidthPx)
                                                val newValue = (scrollState.value + deltaFraction * scrollState.maxValue.toFloat())
                                                    .toInt()
                                                    .coerceIn(0, scrollState.maxValue)

                                                scope.launch {
                                                    scrollState.scrollTo(newValue)
                                                }
                                            }
                                        }
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(80.dp)
                                .background(
                                    CustomColors.surface.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    CustomColors.surfaceBorder.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "График этого магазина пока не заполнен.\nСвяжитесь с руководителем по контактам выше.",
                                color = secText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}