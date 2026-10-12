package org.project.workapp.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.core.designsystem.liquidGlassCard
import org.project.workapp.core.network.OfficeEmployee
import org.project.workapp.feature.schedule.ContactRow

@Composable
fun OfficeEmployeeCard(
    employee: OfficeEmployee,
    onClose: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .liquidGlassCard(alpha = 0.08f)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = employee.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CustomColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = employee.position,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = CustomColors.primaryAccent
                        )
                    }

                    if (onClose != null) {
                        IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Закрыть",
                                tint = CustomColors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (employee.internal.isNotBlank()) ContactRow(
                        Icons.Default.PhoneInTalk,
                        employee.internal,
                        "Внутренний"
                    )
                    if (employee.phone.isNotBlank()) ContactRow(
                        Icons.Default.Phone,
                        employee.phone,
                        "Телефон"
                    )
                    if (employee.email.isNotBlank()) ContactRow(
                        Icons.Default.Email,
                        employee.email,
                        "Email"
                    )
                }

                if (employee.questions.isNotBlank()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Вопросы:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CustomColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = employee.questions,
                        fontSize = 13.sp,
                        color = CustomColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}