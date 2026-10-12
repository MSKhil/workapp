package org.project.workapp.core.designsystem

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class NavItem(val title: String, val icon: ImageVector)

@Composable
fun AnimatedBottomNavBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    items: List<NavItem>,
    accentColor: Color = CustomColors.primaryAccent,
    modifier: Modifier = Modifier
) {
    val itemWidth = 72.dp
    val totalWidth = itemWidth * items.size
    val indicatorSize = 48.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .width(totalWidth)
                .height(60.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = AppShapes.pill,
                    spotColor = Color(0x38000000),
                    ambientColor = Color(0x1A000000)
                )
                .clip(AppShapes.pill)
                .background(CustomColors.surface)
                .border(
                    width = 1.dp,
                    color = CustomColors.surfaceBorder,
                    shape = AppShapes.pill
                )
        ) {
            val targetOffset = itemWidth * selectedItem + (itemWidth - indicatorSize) / 2
            val indicatorOffset by animateDpAsState(
                targetValue = targetOffset,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "indicatorOffset"
            )

            // Скользящий круг
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .size(indicatorSize)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(accentColor)
            )

            // Иконки
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedItem == index
                    Box(
                        modifier = Modifier
                            .width(itemWidth)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onItemSelected(index) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) Color.White else CustomColors.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}