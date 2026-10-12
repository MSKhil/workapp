package org.project.workapp.feature.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.feature.main.MainState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScheduleBottomSheet(
    state: MainState,
    onDismiss: () -> Unit
) {
    if (state.showArchiveSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = CustomColors.background,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(CustomColors.surfaceBorder, CircleShape)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Архив графика: ${state.selectedArchiveMonth}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CustomColors.textPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                when {
                    state.isArchiveLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = CustomColors.primaryAccent)
                        }
                    }
                    state.archiveError != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = state.archiveError, color = Color(0xFFEF9A9A))
                        }
                    }
                    state.archiveSchedule != null -> {
                        state.archiveSchedule.results.forEach { storeResult ->
                            ScheduleCard(
                                apiSchedule = storeResult.schedule,
                                storeTitle = storeResult.storeName,
                                contacts = storeResult.contacts,
                                currentDay = "",
                                displayedMonth = state.selectedArchiveMonth
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}