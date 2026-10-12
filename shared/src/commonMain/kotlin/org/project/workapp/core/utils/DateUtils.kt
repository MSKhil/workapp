package org.project.workapp.core.utils

expect fun getCurrentRussianMonth(): String
expect fun getCurrentDay(): String
expect fun getCurrentMonthNumber(): String
expect fun getCurrentMonthIndex(): Int
expect fun getCurrentYear(): Int

expect fun getCurrentTimeMs(): Long

fun getRussianDayOfWeek(day: Int, month: Int, year: Int): String {
    val t = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    var y = year
    if (month < 3) y -= 1
    val dow = (y + y / 4 - y / 100 + y / 400 + t[month - 1] + day) % 7
    return when (dow) {
        1 -> "Пн"
        2 -> "Вт"
        3 -> "Ср"
        4 -> "Чт"
        5 -> "Пт"
        6 -> "Сб"
        0 -> "Вс"
        else -> ""
    }
}