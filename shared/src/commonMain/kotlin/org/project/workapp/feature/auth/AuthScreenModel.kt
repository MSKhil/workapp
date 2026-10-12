package org.project.workapp.feature.auth

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.project.workapp.core.network.ApiClient
import org.project.workapp.core.database.LocalCache
import org.project.workapp.core.network.MetaResponse
import org.project.workapp.core.utils.getCurrentRussianMonth

data class AuthState(
    val isLoading: Boolean = false,
    val metaData: MetaResponse? = null,
    val error: String? = null,
    val accessLevel: String = "user",
    val selectedStore: String = "",
    val selectedEmployee: String = "",
    val passwordInput: String = "",
    val isLoginSuccess: Boolean = false
)

class AuthScreenModel : StateScreenModel<AuthState>(AuthState()) {
    private val api = ApiClient()
    private val cache = LocalCache()

    init {
        loadMetaData()
    }

    fun loadMetaData() {
        screenModelScope.launch {
            mutableState.value = mutableState.value.copy(isLoading = true, error = null)

            val currentMonth = getCurrentRussianMonth()
            try {
                val response = api.getMeta(monthSheet = currentMonth)
                if (response.success) {
                    cache.saveMeta(response)
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        metaData = response,
                        error = null
                    )
                } else {
                    val cachedMeta = cache.getMeta()
                    if (cachedMeta != null) {
                        mutableState.value = mutableState.value.copy(
                            isLoading = false,
                            metaData = cachedMeta,
                            error = "Используются закэшированные данные (обновите позже)"
                        )
                    } else {
                        mutableState.value = mutableState.value.copy(
                            isLoading = false,
                            error = response.error ?: "Не удалось загрузить данные"
                        )
                    }
                }
            } catch (e: Exception) {
                val cachedMeta = cache.getMeta()
                if (cachedMeta != null) {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        metaData = cachedMeta,
                        error = "Ошибка соединения, используются закэшированные данные"
                    )
                } else {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        error = "Нет соединения и нет сохранённых данных"
                    )
                }
            }
        }
    }

    fun updateAccessLevel(level: String) {
        mutableState.value = mutableState.value.copy(
            accessLevel = level,
            selectedStore = "",
            selectedEmployee = "",
            passwordInput = "",
            error = null
        )
    }

    fun updateStore(store: String) {
        mutableState.value = mutableState.value.copy(
            selectedStore = store,
            selectedEmployee = ""
        )
    }

    fun updateEmployee(employee: String) {
        mutableState.value = mutableState.value.copy(selectedEmployee = employee)
    }

    fun updatePassword(password: String) {
        mutableState.value = mutableState.value.copy(passwordInput = password)
    }

    fun performLogin() {
        val currentState = mutableState.value

        if ((currentState.accessLevel == "user" && currentState.selectedStore.isEmpty()) ||
            currentState.selectedEmployee.isEmpty() ||
            currentState.passwordInput.isEmpty()
        ) {
            mutableState.value = currentState.copy(error = "Заполните все поля")
            return
        }

        screenModelScope.launch {
            mutableState.value = mutableState.value.copy(isLoading = true, error = null)

            val response = api.login(
                accessLevel = currentState.accessLevel,
                storeName = currentState.selectedStore,
                name = currentState.selectedEmployee,
                password = currentState.passwordInput
            )

            if (response.success) {
                cache.saveSession(
                    role = currentState.accessLevel,
                    storeName = currentState.selectedStore,
                    name = currentState.selectedEmployee,
                    position = response.position ?: "",
                    stores = response.stores
                )
                mutableState.value =
                    mutableState.value.copy(isLoading = false, isLoginSuccess = true)
            } else {
                mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    error = response.error ?: "Неверный пароль"
                )
            }
        }
    }
}