package org.project.workapp.feature.schedule

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.project.workapp.core.designsystem.CustomColors

enum class ShiftStatus {
    SHIFT,
    TRAINEE,
    WEEKEND,
    VACATION,
    TIME_OFF,
    SICK_LEAVE,
    UNKNOWN;

    companion object {
        fun from(raw: String?): ShiftStatus {
            val s = raw?.uppercase()?.trim() ?: return UNKNOWN
            return when {
                s.contains("ОТПР") -> TIME_OFF
                s.contains("ОТП") -> VACATION
                s.contains("СТАЖ") -> TRAINEE
                s.contains("СМЕН") -> SHIFT
                s.contains("ВЫХ") -> WEEKEND
                s.contains("БОЛ") -> SICK_LEAVE
                else -> UNKNOWN
            }
        }
    }
}

val ShiftStatus.traineeAccent: Color
    get() = Color(0xFF4FC3F7)

@Composable
fun ShiftStatus.textColor(secondaryColor: Color): Color = when (this) {
    ShiftStatus.SHIFT -> CustomColors.shiftsText
    ShiftStatus.TRAINEE -> traineeAccent
    ShiftStatus.WEEKEND -> CustomColors.weekendsText
    ShiftStatus.VACATION -> CustomColors.vacText
    ShiftStatus.TIME_OFF -> CustomColors.timeOffText
    ShiftStatus.SICK_LEAVE -> CustomColors.sickText
    ShiftStatus.UNKNOWN -> secondaryColor
}

@Composable
fun ShiftStatus.borderColor(): Color = when (this) {
    ShiftStatus.SHIFT -> CustomColors.shiftsText.copy(alpha = 0.4f)
    ShiftStatus.TRAINEE -> traineeAccent.copy(alpha = 0.5f)
    ShiftStatus.WEEKEND -> CustomColors.weekendsText.copy(alpha = 0.4f)
    ShiftStatus.VACATION -> CustomColors.vacText.copy(alpha = 0.4f)
    ShiftStatus.TIME_OFF -> CustomColors.timeOffText.copy(alpha = 0.4f)
    ShiftStatus.SICK_LEAVE -> CustomColors.sickText.copy(alpha = 0.4f)
    ShiftStatus.UNKNOWN -> Color.Transparent
}

@Composable
fun ShiftStatus.badgeBackground(): Color = when (this) {
    ShiftStatus.TRAINEE -> traineeAccent.copy(alpha = 0.12f)
    ShiftStatus.SHIFT, ShiftStatus.WEEKEND, ShiftStatus.VACATION, ShiftStatus.TIME_OFF, ShiftStatus.SICK_LEAVE ->
        CustomColors.surface.copy(alpha = 0.3f)
    ShiftStatus.UNKNOWN -> CustomColors.defaultShiftBg.copy(alpha = 0.2f)
}