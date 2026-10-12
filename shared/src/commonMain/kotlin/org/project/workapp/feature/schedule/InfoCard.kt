package org.project.workapp.feature.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.core.designsystem.liquidGlassCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    month: String,
    shiftsCount: Int,
    weekendsCount: Int,
    vacationCount: Int,
    timeOffCount: Int,
    sickLeaveCount: Int
) {
    var isExpanded by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .liquidGlassCard(alpha = 0.08f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isExpanded = !isExpanded }
            ) {
                Text(
                    text = month,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CustomColors.textPrimary
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Инфо-карта",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = CustomColors.textSecondary,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Скрыть/Показать",
                    tint = CustomColors.textSecondary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            modifier = Modifier.weight(1f),
                            title = "Смен",
                            value = shiftsCount.toString(),
                            borderColor = CustomColors.shiftsText.copy(alpha = 0.4f),
                            textColor = CustomColors.shiftsText
                        )
                        StatBox(
                            modifier = Modifier.weight(1f),
                            title = "Выходных",
                            value = weekendsCount.toString(),
                            borderColor = CustomColors.weekendsText.copy(alpha = 0.4f),
                            textColor = CustomColors.weekendsText
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BadgePill("Отпуска: $vacationCount", CustomColors.vacBg.copy(alpha = 0.4f), CustomColors.vacText)
                        BadgePill("Отпрашивания: $timeOffCount", CustomColors.timeOffBg.copy(alpha = 0.4f), CustomColors.timeOffText)
                        BadgePill("Больничные: $sickLeaveCount", CustomColors.sickBg.copy(alpha = 0.4f), CustomColors.sickText)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(modifier: Modifier = Modifier, title: String, value: String, borderColor: Color, textColor: Color) {
    Box(
        modifier = modifier
            .background(CustomColors.surface.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                color = CustomColors.textSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun BadgePill(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(16.dp))
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}