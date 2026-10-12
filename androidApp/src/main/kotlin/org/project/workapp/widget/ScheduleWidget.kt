package org.project.workapp.widget

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import org.project.workapp.MainActivity
import org.project.workapp.R
import org.project.workapp.core.database.LocalCache
import org.project.workapp.core.utils.getCurrentDay
import org.project.workapp.feature.employee.handleAlarmAction
import java.util.Calendar

data class WidgetDay(
    val dateNum: String,
    val dayOfWeekShort: String,
    val dayOfWeekFull: String,
    val monthName: String,
    val status: String,
    val time: String,
    val isToday: Boolean,
    val isCurrentMonth: Boolean
)

@Composable
private fun ColorProvider(day: Color, night: Color): ColorProvider {
    val context = LocalContext.current
    val isNight = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    return ColorProvider(if (isNight) night else day)
}

class ScheduleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ScheduleWidget()
}

class AlarmActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val isArming = parameters[ActionParameters.Key<Boolean>("isArming")] ?: false
        val store = parameters[ActionParameters.Key<String>("store")] ?: ""

        val msg = handleAlarmAction(storeName = store, isArming = isArming)
        android.widget.Toast.makeText(context, "Команда отправлена: $msg", android.widget.Toast.LENGTH_SHORT).show()
    }
}

class ScheduleWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val cache = LocalCache()
        val userName = cache.getUserName() ?: ""
        val userStore = cache.getStoreName() ?: ""
        val userRole = cache.getUserRole() ?: ""
        val apiData = cache.getSchedule()
        val currentDay = getCurrentDay()

        val shortName = userName.split(" ").firstOrNull()?.uppercase() ?: ""

        // Примечание: требуется добавить метод getUserPosition() в LocalCache
        val userPosition = cache.getUserPosition() ?: ""
        val hasAlarmAccess = userPosition.contains("Директор", ignoreCase = true)

        val mySchedule = if (userRole == "user") {
            apiData?.results?.flatMap { it.schedule }?.find {
                it.name.uppercase().contains(shortName)
            }
        } else null

        val monthNamesGenitive = arrayOf(
            "янв", "фев", "мар", "апр", "мая", "июн",
            "июл", "авг", "сен", "окт", "ноя", "дек"
        )
        val monthNamesNominative = arrayOf(
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        )
        val dayNamesShort = arrayOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        val dayNamesFull = arrayOf(
            "Понедельник", "Вторник", "Среда", "Четверг",
            "Пятница", "Суббота", "Воскресенье"
        )

        val cal = Calendar.getInstance()
        val currentMonthIndex = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonthNameNom = "${monthNamesNominative[currentMonthIndex]} $currentYear"

        cal.set(Calendar.DAY_OF_MONTH, 1)
        var firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (firstDayOfWeek == 0) firstDayOfWeek = 7
        val offset = firstDayOfWeek - 1

        cal.add(Calendar.DAY_OF_MONTH, -offset)

        val grid = mutableListOf<WidgetDay>()
        for (i in 0 until 35) {
            val dNum = cal.get(Calendar.DAY_OF_MONTH).toString()
            val mIndex = cal.get(Calendar.MONTH)
            val dOfWeekIndex = i % 7

            val apiDay = if (mIndex == currentMonthIndex) {
                mySchedule?.days?.find { it.date == dNum }
            } else null

            grid.add(
                WidgetDay(
                    dateNum = dNum,
                    dayOfWeekShort = dayNamesShort[dOfWeekIndex],
                    dayOfWeekFull = dayNamesFull[dOfWeekIndex],
                    monthName = monthNamesGenitive[mIndex],
                    status = apiDay?.status ?: "",
                    time = apiDay?.time ?: "",
                    isToday = (mIndex == currentMonthIndex && dNum == currentDay),
                    isCurrentMonth = (mIndex == currentMonthIndex)
                )
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        provideContent {
            val size = LocalSize.current
            val height = size.height
            val width = size.width

            val widgetBg = ColorProvider(
                day = Color(0xF8FFFFFF),
                night = Color(0xF414151B)
            )

            val widgetBorder = ColorProvider(
                day = Color(0xFFDCE2ED),
                night = Color(0x33FFFFFF)
            )

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(widgetBorder)
                        .cornerRadius(24.dp)
                        .padding(1.dp)
                ) {
                    Box(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(widgetBg)
                            .cornerRadius(23.dp)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .clickable(actionStartActivity(intent))
                    ) {
                        when {
                            height >= 240.dp -> {
                                LargeWidget(
                                    shortName,
                                    grid,
                                    currentMonthNameNom,
                                    hasAlarmAccess,
                                    userStore
                                )
                            }
                            width >= 260.dp -> {
                                MediumWidget(
                                    shortName,
                                    grid,
                                    currentMonthNameNom,
                                    hasAlarmAccess,
                                    userStore
                                )
                            }
                            else -> {
                                SmallWidget(
                                    shortName,
                                    grid,
                                    currentMonthNameNom,
                                    hasAlarmAccess,
                                    userStore
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SharedHeader(name: String, monthYear: String, hasAlarm: Boolean, store: String) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "WorkApp",
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(
                                day = Color(0xFF1428A0),
                                night = Color(0xFF4C68EF)
                            ),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        " | $name",
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(
                                day = Color(0xFF111827),
                                night = Color.White
                            ),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    monthYear,
                    style = TextStyle(
                        color = ColorProvider(
                            day = Color(0xFF6B7280),
                            night = Color(0xFF8B92A0)
                        ),
                        fontSize = 13.sp
                    )
                )
            }
            Spacer(GlanceModifier.defaultWeight())
            if (hasAlarm) AlarmButtons(store)
        }
    }

    @Composable
    private fun SmallWidget(
        name: String,
        grid: List<WidgetDay>,
        monthYear: String,
        hasAlarm: Boolean,
        store: String
    ) {
        val todayIndex = grid.indexOfFirst { it.isToday }
        val targetDay = if (todayIndex != -1) grid.getOrNull(todayIndex + 1) else null

        Column(modifier = GlanceModifier.fillMaxSize()) {
            SharedHeader(name, monthYear, hasAlarm = false, store)

            Spacer(modifier = GlanceModifier.defaultWeight())

            val isShift = targetDay?.status?.uppercase()?.contains("СМЕН") == true
            val isWeekend = targetDay?.status?.uppercase()?.contains("ВЫХ") == true
            val isVac = targetDay?.status?.uppercase()?.contains("ОТП") == true

            val (statusText, textColor) = when {
                targetDay == null || (targetDay.status.isBlank() && targetDay.time.isBlank()) ->
                    "Смена не найдена" to ColorProvider(day = Color(0xFF6B7280), night = Color(0xFF9CA3AF))
                isWeekend ->
                    "Выходной" to ColorProvider(day = Color(0xFFDC2626), night = Color(0xFFF87171))
                isVac ->
                    "Отпуск" to ColorProvider(day = Color(0xFF0284C7), night = Color(0xFF60A5FA))
                isShift ->
                    targetDay.time.replace(" - ", "  -  ") to ColorProvider(day = Color(0xFF1428A0), night = Color(0xFF829DFF))
                else ->
                    "Смена не найдена" to ColorProvider(day = Color(0xFF6B7280), night = Color(0xFF9CA3AF))
            }

            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (targetDay != null) "Завтра, ${targetDay.dateNum} ${targetDay.monthName}" else "Завтра",
                    style = TextStyle(
                        color = ColorProvider(day = Color(0xFF0F172A), night = Color.White),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(14.dp))
                Box(
                    modifier = GlanceModifier
                        .background(
                            ColorProvider(
                                day = Color(0xFFE8EEFC),
                                night = Color(0xFF222634)
                            )
                        )
                        .cornerRadius(18.dp)
                        .padding(horizontal = 22.dp, vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusText,
                        style = TextStyle(
                            color = textColor,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.defaultWeight())
        }
    }

    @Composable
    private fun MediumWidget(
        name: String,
        grid: List<WidgetDay>,
        monthYear: String,
        hasAlarm: Boolean,
        store: String
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            SharedHeader(name, monthYear, hasAlarm, store)

            Spacer(modifier = GlanceModifier.defaultWeight())

            val todayIndex = grid.indexOfFirst { it.isToday }.takeIf { it != -1 } ?: 0
            val weekStartIndex = (todayIndex / 7) * 7
            val weekDays = grid.subList(weekStartIndex, weekStartIndex + 7)

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekDays.forEach { day ->
                    val isToday = day.isToday
                    val bgModifier = if (isToday) {
                        GlanceModifier
                            .background(
                                ColorProvider(
                                    day = Color(0xFFE8EEFC),
                                    night = Color(0xFF262D4A)
                                )
                            )
                            .cornerRadius(16.dp)
                    } else GlanceModifier

                    val dayOfWeekColor = if (isToday) {
                        ColorProvider(day = Color(0xFF1428A0), night = Color(0xFF829DFF))
                    } else {
                        ColorProvider(day = Color(0xFF475569), night = Color(0xFF8B92A0))
                    }

                    val dateNumColor = if (isToday) {
                        ColorProvider(day = Color(0xFF1428A0), night = Color(0xFF829DFF))
                    } else {
                        ColorProvider(day = Color(0xFF0F172A), night = Color.White)
                    }

                    Box(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .then(bgModifier)
                            .padding(vertical = 6.dp, horizontal = 2.dp)
                    ) {
                        Column(
                            modifier = GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = day.dayOfWeekShort,
                                style = TextStyle(
                                    color = dayOfWeekColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            Text(
                                text = day.dateNum,
                                style = TextStyle(
                                    color = dateNumColor,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(3.dp))

                            val isShift = day.status.uppercase().contains("СМЕН")
                            val isWeekend = day.status.uppercase().contains("ВЫХ")
                            val isVac = day.status.uppercase().contains("ОТП")

                            if (isShift) {
                                val parts = day.time.replace(" ", "").split("-")
                                val start = parts.getOrNull(0) ?: ""
                                val end = parts.getOrNull(1) ?: ""
                                Text(
                                    start,
                                    style = TextStyle(
                                        color = ColorProvider(day = Color(0xFF0F172A), night = Color.White),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    "-",
                                    style = TextStyle(
                                        color = ColorProvider(day = Color(0xFF94A3B8), night = Color(0xFF6B7280)),
                                        fontSize = 9.sp
                                    )
                                )
                                Text(
                                    end,
                                    style = TextStyle(
                                        color = ColorProvider(day = Color(0xFF0F172A), night = Color.White),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            } else if (isWeekend) {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                                Text(
                                    "Вых",
                                    style = TextStyle(
                                        color = ColorProvider(day = Color(0xFFDC2626), night = Color(0xFFF87171)),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            } else if (isVac) {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                                Text(
                                    "Отп",
                                    style = TextStyle(
                                        color = ColorProvider(day = Color(0xFF0284C7), night = Color(0xFF60A5FA)),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            } else {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                                Text(
                                    "-",
                                    style = TextStyle(
                                        color = ColorProvider(day = Color(0xFF6B7280), night = Color(0xFF8B92A0)),
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = GlanceModifier.defaultWeight())
        }
    }

    @Composable
    private fun LargeWidget(
        name: String,
        grid: List<WidgetDay>,
        monthYear: String,
        hasAlarm: Boolean,
        store: String
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            SharedHeader(name, monthYear, hasAlarm, store)
            Spacer(modifier = GlanceModifier.height(14.dp))
            val weekNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                weekNames.forEach { dayName ->
                    Text(
                        text = dayName,
                        modifier = GlanceModifier.defaultWeight(),
                        style = TextStyle(
                            color = ColorProvider(day = Color(0xFF475569), night = Color(0xFF8B92A0)),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
            Spacer(modifier = GlanceModifier.height(6.dp))

            val weeks = grid.chunked(7)
            weeks.forEach { week ->
                Row(modifier = GlanceModifier.fillMaxWidth().defaultWeight()) {
                    week.forEach { day ->
                        Box(
                            modifier = GlanceModifier.defaultWeight().fillMaxHeight().padding(2.dp)
                        ) {
                            LargeDayCell(day)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun LargeDayCell(day: WidgetDay) {
        val isShift = day.status.uppercase().contains("СМЕН")
        val isWeekend = day.status.uppercase().contains("ВЫХ")
        val isVac = day.status.uppercase().contains("ОТП")

        val cellBg = when {
            day.isToday -> ColorProvider(day = Color(0xFF1428A0), night = Color(0xFF364BC7))
            !day.isCurrentMonth -> ColorProvider(day = Color(0xFFEEF2F7), night = Color(0xFF171820))
            else -> ColorProvider(day = Color(0xFFE2E7F0), night = Color(0xFF20222B))
        }

        val dateColor = when {
            day.isToday -> ColorProvider(day = Color.White, night = Color.White)
            day.isCurrentMonth -> ColorProvider(day = Color(0xFF0F172A), night = Color.White)
            else -> ColorProvider(day = Color(0xFF94A3B8), night = Color(0xFF4B5563))
        }

        val timeColor = when {
            day.isToday -> ColorProvider(day = Color.White, night = Color.White)
            isShift -> ColorProvider(day = Color(0xFF1428A0), night = Color(0xFF829DFF))
            isWeekend -> ColorProvider(day = Color(0xFFDC2626), night = Color(0xFFF87171))
            isVac -> ColorProvider(day = Color(0xFF0284C7), night = Color(0xFF60A5FA))
            else -> ColorProvider(day = Color(0xFF94A3B8), night = Color(0xFF4B5563))
        }

        val contentText = when {
            isWeekend -> "Вых"
            isVac -> "Отп"
            isShift -> {
                val parts = day.time.replace(" ", "").split("-")
                val start = parts.getOrNull(0)?.replace(":00", "") ?: ""
                val end = parts.getOrNull(1)?.replace(":00", "") ?: ""
                "$start-$end"
            }
            else -> ""
        }

        Box(
            modifier = GlanceModifier.fillMaxSize().background(cellBg).cornerRadius(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.dateNum,
                    style = TextStyle(
                        color = dateColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                if (contentText.isNotEmpty() && day.isCurrentMonth) {
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = contentText,
                        style = TextStyle(
                            color = timeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun AlarmButtons(store: String) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = GlanceModifier
                    .size(36.dp)
                    .background(ColorProvider(day = Color(0x26EF4444), night = Color(0x33EF4444)))
                    .cornerRadius(50.dp)
                    .clickable(
                        actionRunCallback<AlarmActionCallback>(
                            actionParametersOf(
                                ActionParameters.Key<Boolean>("isArming") to true,
                                ActionParameters.Key<String>("store") to store
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_lock),
                    contentDescription = "Закрыть",
                    modifier = GlanceModifier.size(18.dp)
                )
            }
            Spacer(modifier = GlanceModifier.width(8.dp))
            Box(
                modifier = GlanceModifier
                    .size(36.dp)
                    .background(ColorProvider(day = Color(0x2622C55E), night = Color(0x3322C55E)))
                    .cornerRadius(50.dp)
                    .clickable(
                        actionRunCallback<AlarmActionCallback>(
                            actionParametersOf(
                                ActionParameters.Key<Boolean>("isArming") to false,
                                ActionParameters.Key<String>("store") to store
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_unlock),
                    contentDescription = "Открыть",
                    modifier = GlanceModifier.size(18.dp)
                )
            }
        }
    }
}