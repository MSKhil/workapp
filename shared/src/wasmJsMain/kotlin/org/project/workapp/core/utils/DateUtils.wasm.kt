package org.project.workapp.core.utils

actual fun getCurrentRussianMonth(): String = js("new Date().toLocaleString('ru-RU', { month: 'long' }).replace(/^./, c => c.toUpperCase())")

actual fun getCurrentDay(): String = js("new Date().getDate().toString()")

actual fun getCurrentMonthNumber(): String = js("String(new Date().getMonth() + 1).padStart(2, '0')")

actual fun getCurrentMonthIndex(): Int = js("new Date().getMonth() + 1")

actual fun getCurrentYear(): Int = js("new Date().getFullYear()")

private fun getJsDateNowString(): String = js("Date.now().toString()")

actual fun getCurrentTimeMs(): Long {
    return getJsDateNowString().toLongOrNull() ?: 0L
}