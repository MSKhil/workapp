package org.project.workapp.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.project.workapp.core.designsystem.CustomColors
import org.project.workapp.core.designsystem.liquidGlassCard
import org.project.workapp.feature.main.MainScreen

class AuthScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { AuthScreenModel() }
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val focusManager = LocalFocusManager.current

        LaunchedEffect(state.isLoginSuccess) {
            if (state.isLoginSuccess) {
                navigator.replace(MainScreen())
            }
        }

        Scaffold(
            containerColor = CustomColors.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when {
                        state.isLoading && state.metaData == null -> {
                            CircularProgressIndicator(color = CustomColors.primaryAccent)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Готовим Ваш личный кабинет",
                                color = CustomColors.textPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        state.error != null && state.metaData == null -> {
                            Text(text = "Ошибка: ${state.error}", color = Color(0xFFEF9A9A))
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { screenModel.loadMetaData() },
                                colors = ButtonDefaults.buttonColors(containerColor = CustomColors.primaryAccent)
                            ) {
                                Text("Повторить", color = Color.White)
                            }
                        }

                        state.metaData != null -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(bottom = 28.dp)
                            ) {
                                Text(
                                    text = "WorkApp",
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CustomColors.primaryAccent,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "CORPORATE PORTAL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = CustomColors.textSecondary
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .liquidGlassCard(alpha = 0.08f, shape = RoundedCornerShape(24.dp))
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Авторизация",
                                    color = CustomColors.textPrimary,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Войдите в корпоративный портал",
                                    color = CustomColors.inputLabel,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )

                                val roleMap = mapOf(
                                    "user" to "Сотрудник розницы",
                                    "admin" to "Сотрудник офиса",
                                    "regional" to "Региональный директор"
                                )
                                val displayRoles = roleMap.values.toList()
                                val currentDisplayRole = roleMap[state.accessLevel] ?: ""

                                CustomDropdown(
                                    label = "Уровень доступа",
                                    items = displayRoles,
                                    selectedValue = currentDisplayRole,
                                    onItemSelected = { selectedName ->
                                        val actualRole =
                                            roleMap.entries.firstOrNull { it.value == selectedName }?.key
                                                ?: ""
                                        screenModel.updateAccessLevel(actualRole)
                                    }
                                )

                                if (state.accessLevel == "user") {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    val storeNames = state.metaData!!.stores.keys.toList()
                                    CustomDropdown(
                                        label = "Магазин",
                                        items = storeNames,
                                        selectedValue = state.selectedStore,
                                        onItemSelected = { screenModel.updateStore(it) }
                                    )

                                    if (state.selectedStore.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        val employeesInStore =
                                            state.metaData!!.stores[state.selectedStore]
                                                ?: emptyList()
                                        CustomDropdown(
                                            label = "Сотрудник",
                                            items = employeesInStore,
                                            selectedValue = state.selectedEmployee,
                                            onItemSelected = { screenModel.updateEmployee(it) }
                                        )
                                    }
                                } else if (state.accessLevel == "admin") {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    val admins = state.metaData!!.admins
                                    CustomDropdown(
                                        label = "ФИО сотрудника",
                                        items = admins,
                                        selectedValue = state.selectedEmployee,
                                        onItemSelected = { screenModel.updateEmployee(it) }
                                    )
                                } else if (state.accessLevel == "regional") {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    val regionals = state.metaData!!.regionals
                                    CustomDropdown(
                                        label = "Региональный директор",
                                        items = regionals,
                                        selectedValue = state.selectedEmployee,
                                        onItemSelected = { screenModel.updateEmployee(it) }
                                    )
                                }

                                if (state.selectedEmployee.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    var passwordVisible by remember { mutableStateOf(false) }

                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Пароль",
                                            color = CustomColors.inputLabel,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))

                                        OutlinedTextField(
                                            value = state.passwordInput,
                                            onValueChange = { screenModel.updatePassword(it) },
                                            placeholder = {
                                                Text(
                                                    "Введите пароль",
                                                    color = CustomColors.textSecondary.copy(alpha = 0.5f),
                                                    fontSize = 14.sp
                                                )
                                            },
                                            singleLine = true,
                                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Password,
                                                imeAction = ImeAction.Done
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onDone = {
                                                    focusManager.clearFocus()
                                                    screenModel.performLogin()
                                                }
                                            ),
                                            trailingIcon = {
                                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                    Icon(
                                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                        contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                                                    )
                                                }
                                            },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                unfocusedContainerColor = CustomColors.searchBg,
                                                focusedContainerColor = CustomColors.searchBg,
                                                unfocusedBorderColor = CustomColors.surfaceBorder.copy(alpha = 0.6f),
                                                focusedBorderColor = CustomColors.primaryAccent,
                                                unfocusedTextColor = CustomColors.textPrimary,
                                                focusedTextColor = CustomColors.textPrimary,
                                                unfocusedLabelColor = CustomColors.textSecondary,
                                                focusedLabelColor = CustomColors.primaryAccent,
                                                unfocusedTrailingIconColor = CustomColors.textSecondary,
                                                focusedTrailingIconColor = CustomColors.primaryAccent
                                            ),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                    }

                                    if (state.error != null) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = state.error!!,
                                            color = Color(0xFFEF9A9A),
                                            fontSize = 14.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(28.dp))

                                    Button(
                                        onClick = { screenModel.performLogin() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp),
                                        enabled = !state.isLoading,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = CustomColors.primaryAccent,
                                            disabledContainerColor = CustomColors.primaryAccent.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        if (state.isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                text = "Войти в систему",
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CustomDropdown(
        label: String,
        items: List<String>,
        selectedValue: String,
        onItemSelected: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = label,
                    color = CustomColors.inputLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = selectedValue.ifEmpty { "Не выбрано" },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CustomColors.searchBg,
                        focusedContainerColor = CustomColors.searchBg,
                        unfocusedBorderColor = CustomColors.surfaceBorder.copy(alpha = 0.6f),
                        focusedBorderColor = CustomColors.primaryAccent,
                        unfocusedTextColor = CustomColors.textPrimary,
                        focusedTextColor = CustomColors.textPrimary,
                        unfocusedLabelColor = CustomColors.textSecondary,
                        focusedLabelColor = CustomColors.primaryAccent,
                        unfocusedTrailingIconColor = CustomColors.textSecondary,
                        focusedTrailingIconColor = CustomColors.primaryAccent
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(CustomColors.surface)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item, color = CustomColors.textPrimary) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}