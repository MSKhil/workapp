package org.project.workapp.feature.links

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import org.project.workapp.core.database.LocalCache
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.core.designsystem.liquidGlassCard
import org.project.workapp.core.network.ApiClient
import org.project.workapp.core.network.UsefulLinkDto

private fun getLinkIcon(type: String): ImageVector = when (type.uppercase()) {
    "PROMO" -> Icons.Default.LocalOffer
    "MOTIVATION" -> Icons.Default.EmojiEvents
    "SCHEDULE" -> Icons.Default.DateRange
    else -> Icons.Default.Link
}

@Composable
fun EmployeeLinksScreen(
    navigator: Navigator? = null,
    apiClient: ApiClient = remember { ApiClient() },
    localCache: LocalCache = remember { LocalCache() }
) {
    UsefulLinksScreen(
        navigator = navigator,
        apiClient = apiClient,
        localCache = localCache
    )
}

@Composable
fun UsefulLinksScreen(
    navigator: Navigator? = null,
    apiClient: ApiClient = remember { ApiClient() },
    localCache: LocalCache = remember { LocalCache() }
) {
    val uriHandler = LocalUriHandler.current

    // Offline-First: мгновенно отображаем данные из локального кэша
    var links by remember { mutableStateOf(localCache.getUsefulLinks()) }

    // Фоновый запрос в сеть за актуальными ссылками
    LaunchedEffect(Unit) {
        val result = apiClient.getUsefulLinks()
        result.onSuccess { freshLinks ->
            links = freshLinks
            localCache.saveUsefulLinks(freshLinks)
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // One UI Viewing Area (верхняя треть под свободный просмотр)
                item {
                    Spacer(modifier = Modifier.height(screenHeight * 0.18f))
                    Text(
                        text = "Полезные ссылки",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CustomColors.textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(screenHeight * 0.08f))
                }

                // One UI Interaction Area (зона досягаемости пальца одной руки)
                items(links) { linkItem ->
                    UsefulLinkCard(
                        item = linkItem,
                        onClick = {
                            try {
                                uriHandler.openUri(linkItem.url)
                            } catch (_: Exception) {
                            }
                        }
                    )
                }

                // Отступ для нижней плавающей панели навигации
                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}

@Composable
private fun UsefulLinkCard(
    item: UsefulLinkDto,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .liquidGlassCard(alpha = 0.08f, shape = RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        CustomColors.primaryAccent.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getLinkIcon(item.type),
                    contentDescription = item.title,
                    tint = CustomColors.primaryAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = CustomColors.textPrimary
                )
                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        fontSize = 13.sp,
                        color = CustomColors.inputLabel,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = CustomColors.textSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}