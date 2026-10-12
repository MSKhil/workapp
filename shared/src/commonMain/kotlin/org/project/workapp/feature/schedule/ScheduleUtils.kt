package org.project.workapp.feature.schedule

import org.project.workapp.core.utils.getCurrentMonthIndex
import org.project.workapp.core.utils.getCurrentMonthNumber
import org.project.workapp.core.utils.getCurrentYear

data class DayHeader(
    val dayOfWeek: String,
    val date: String,
    val fullDate: String,
    val isToday: Boolean = false
)

fun parseMonthInfo(monthString: String?): Triple<Int, String, Int> {
    if (monthString.isNullOrBlank()) {
        return Triple(getCurrentMonthIndex(), getCurrentMonthNumber(), getCurrentYear())
    }
    val upper = monthString.uppercase()
    val monthIdx = when {
        upper.contains("ЯНВ") -> 1
        upper.contains("ФЕВ") -> 2
        upper.contains("МАР") -> 3
        upper.contains("АПР") -> 4
        upper.contains("МАЙ") || upper.contains("МАЯ") -> 5
        upper.contains("ИЮН") -> 6
        upper.contains("ИЮЛ") -> 7
        upper.contains("АВГ") -> 8
        upper.contains("СЕН") -> 9
        upper.contains("ОКТ") -> 10
        upper.contains("НОЯ") -> 11
        upper.contains("ДЕК") -> 12
        else -> getCurrentMonthIndex()
    }
    val monthNum = if (monthIdx < 10) "0$monthIdx" else monthIdx.toString()
    val year = Regex("\\d{4}").find(monthString)?.value?.toIntOrNull() ?: getCurrentYear()
    return Triple(monthIdx, monthNum, year)
}