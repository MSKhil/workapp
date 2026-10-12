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
    background = Color(0xFF0B0C0E),
    surface = Color(0xFF17181C),
    surfaceBorder = Color(0xFF2B2D38),
    primaryAccent = Color(0xFF223FD2),
    adminAccent = Color(0xFF223FD2),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF8B92A0),
    inputLabel = Color(0xFF90A3D4),
    searchBg = Color(0xFF101114),
    shiftsBg = Color(0xFF161F38),
    shiftsText = Color(0xFF7A99FF),
    weekendsBg = Color(0xFF2A1519),
    weekendsText = Color(0xFFF87171),
    vacBg = Color(0xFF112033),
    vacText = Color(0xFF60A5FA),
    timeOffBg = Color(0xFF2A1A14),
    timeOffText = Color(0xFFFB923C),
    sickBg = Color(0xFF22182E),
    sickText = Color(0xFFC084FC),
    defaultShiftBg = Color(0xFF17181C)
)

val LightCustomColorsPalette = CustomColorsPalette(
    background = Color(0xFFF1F4F9),
    surface = Color(0xFFFFFFFF),
    surfaceBorder = Color(0xFFD6DCED),
    primaryAccent = Color(0xFF1428A0),
    adminAccent = Color(0xFF1428A0),
    textPrimary = Color(0xFF111827),
    textSecondary = Color(0xFF6B7280),
    inputLabel = Color(0xFF2B3A67),
    searchBg = Color(0xFFF8FAFC),
    shiftsBg = Color(0xFFEBF1FF),
    shiftsText = Color(0xFF1428A0),
    weekendsBg = Color(0xFFFEE2E2),
    weekendsText = Color(0xFFDC2626),
    vacBg = Color(0xFFE0F2FE),
    vacText = Color(0xFF0284C7),
    timeOffBg = Color(0xFFFFEDD5),
    timeOffText = Color(0xFFD97706),
    sickBg = Color(0xFFF3E8FF),
    sickText = Color(0xFF7E22CE),
    defaultShiftBg = Color(0xFFF8FAFC)
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
