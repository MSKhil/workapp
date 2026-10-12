package org.project.workapp.feature.search

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.feature.main.SearchMode

@Composable
fun SearchModeToggle(
    currentMode: SearchMode,
    onModeSelected: (SearchMode) -> Unit
) {
    val isRetail = currentMode == SearchMode.RETAIL

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(50))
                .background(CustomColors.surface.copy(alpha = 0.85f))
                .border(1.dp, CustomColors.surfaceBorder, RoundedCornerShape(50))
                .padding(3.dp)
        ) {
            val itemWidth = maxWidth / 2

            val indicatorOffset by animateDpAsState(
                targetValue = if (isRetail) 0.dp else itemWidth,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "toggleIndicator"
            )

            // Активный скользящий индикатор One UI
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(itemWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(CustomColors.primaryAccent)
            )

            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onModeSelected(SearchMode.RETAIL) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Розница",
                        color = if (isRetail) Color.White else CustomColors.textSecondary,
                        fontWeight = if (isRetail) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onModeSelected(SearchMode.OFFICE) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Офис",
                        color = if (!isRetail) Color.White else CustomColors.textSecondary,
                        fontWeight = if (!isRetail) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}