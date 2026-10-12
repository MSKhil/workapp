package org.project.workapp.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.liquidGlassCard(
    shape: RoundedCornerShape = AppShapes.innerCard,
    alpha: Float = 1.0f,
    elevation: Dp = 6.dp
): Modifier = composed {
    this
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            spotColor = Color(0x24000000),
            ambientColor = Color(0x10000000)
        )
        .clip(shape)
        .background(CustomColors.surface)
        .border(
            width = 1.dp,
            color = CustomColors.surfaceBorder,
            shape = shape
        )
}