package org.project.workapp.feature.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.core.designsystem.liquidGlassCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SalaryCard(
    balance: Int,
    modifier: Modifier = Modifier,
    onDoubleTap: (() -> Unit)? = null
) {
    var isBalanceVisible by remember { mutableStateOf(true) }

    fun formatNumber(num: Int): String {
        return num.toString().reversed().chunked(3).joinToString(" ").reversed()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .liquidGlassCard(alpha = 0.08f)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
                onDoubleClick = { onDoubleTap?.invoke() }
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ваш баланс:",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = CustomColors.textPrimary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { isBalanceVisible = !isBalanceVisible },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Скрыть баланс",
                        tint = CustomColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = if (isBalanceVisible) "${formatNumber(balance)} ₽" else "•••••• ₽",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CustomColors.primaryAccent
                )
            }
        }
    }
}