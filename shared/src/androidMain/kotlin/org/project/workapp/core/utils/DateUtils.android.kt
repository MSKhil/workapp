package org.project.workapp.core.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

actual fun getCurrentRussianMonth(): String {
    val cal = Calendar.getInstance()
    val monthName = SimpleDateFormat("LLLL", Locale("ru")).format(cal.time)
    return monthName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("ru")) else it.toString() }
}

actual fun getCurrentDay(): String {
    return Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
}

actual fun getCurrentMonthNumber(): String {
    val month = Calendar.getInstance().get(Calendar.MONTH) + 1
    return if (month < 10) "0$month" else month.toString()
}

actual fun getCurrentMonthIndex(): Int {
    return Calendar.getInstance().get(Calendar.MONTH) + 1
}

actual fun getCurrentYear(): Int {
    return Calendar.getInstance().get(Calendar.YEAR)
}

actual fun getCurrentTimeMs(): Long = System.currentTimeMillis()