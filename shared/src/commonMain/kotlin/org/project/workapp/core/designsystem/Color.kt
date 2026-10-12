package org.project.workapp.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColorsPalette(
    val background: Color,
    val surface: Color,
    val surfaceBorder: Color,
    val primaryAccent: Color,
    val adminAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val inputLabel: Color,
    val searchBg: Color,
    val shiftsBg: Color,
    val shiftsText: Color,
    val weekendsBg: Color,
    val weekendsText: Color,
    val vacBg: Color,
    val vacText: Color,
    val timeOffBg: Color,
    val timeOffText: Color,
    val sickBg: Color,
    val sickText: Color,
    val defaultShiftBg: Color
)

val DarkCustomColorsPalette = CustomColorsPalette(
    background = Color(0xFF0B0914),
    surface = Color(0xFF1A162B),
    surfaceBorder = Color(0xFF332D4A),
    primaryAccent = Color(0xFF6B57FF),
    adminAccent = Color(0xFF6B57FF),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF8A879E),
    inputLabel = Color(0xFF6B57FF),
    searchBg = Color(0xFF13101F),
    shiftsBg = Color(0x336B57FF),
    shiftsText = Color(0xFFB0A6FF),
    weekendsBg = Color(0x33FF577F),
    weekendsText = Color(0xFFFF8FA3),
    vacBg = Color(0x3357C4FF),
    vacText = Color(0xFF8FD6FF),
    timeOffBg = Color(0x33FF9E57),
    timeOffText = Color(0xFFFFB88A),
    sickBg = Color(0x33B357FF),
    sickText = Color(0xFFD6A6FF),
    defaultShiftBg = Color(0x331A162B)
)

val LocalCustomColors = staticCompositionLocalOf { DarkCustomColorsPalette }

object CustomColors {
    val background: Color @Composable get() = LocalCustomColors.current.background
    val surface: Color @Composable get() = LocalCustomColors.current.surface
    val surfaceBorder: Color @Composable get() = LocalCustomColors.current.surfaceBorder
    val primaryAccent: Color @Composable get() = LocalCustomColors.current.primaryAccent
    val adminAccent: Color @Composable get() = LocalCustomColors.current.adminAccent
    val textPrimary: Color @Composable get() = LocalCustomColors.current.textPrimary
    val textSecondary: Color @Composable get() = LocalCustomColors.current.textSecondary
    val inputLabel: Color @Composable get() = LocalCustomColors.current.inputLabel
    val searchBg: Color @Composable get() = LocalCustomColors.current.searchBg
    val shiftsBg: Color @Composable get() = LocalCustomColors.current.shiftsBg
    val shiftsText: Color @Composable get() = LocalCustomColors.current.shiftsText
    val weekendsBg: Color @Composable get() = LocalCustomColors.current.weekendsBg
    val weekendsText: Color @Composable get() = LocalCustomColors.current.weekendsText
    val vacBg: Color @Composable get() = LocalCustomColors.current.vacBg
    val vacText: Color @Composable get() = LocalCustomColors.current.vacText
    val timeOffBg: Color @Composable get() = LocalCustomColors.current.timeOffBg
    val timeOffText: Color @Composable get() = LocalCustomColors.current.timeOffText
    val sickBg: Color @Composable get() = LocalCustomColors.current.sickBg
    val sickText: Color @Composable get() = LocalCustomColors.current.sickText
    val defaultShiftBg: Color @Composable get() = LocalCustomColors.current.defaultShiftBg
}