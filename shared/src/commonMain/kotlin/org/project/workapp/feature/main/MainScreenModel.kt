package org.project.workapp.feature.main

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.project.workapp.core.network.ApiClient
import org.project.workapp.core.network.MetaResponse
import org.project.workapp.core.network.OfficeSearchResponse
import org.project.workapp.core.database.LocalCache
import org.project.workapp.core.network.ScheduleResponse
import org.project.workapp.core.utils.getCurrentDay
import org.project.workapp.core.utils.getCurrentRussianMonth
import kotlin.time.Clock

enum class SearchMode { RETAIL, OFFICE }
data class MainState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val scheduleData: ScheduleResponse? = null,
    val searchResult: ScheduleResponse? = null,
    val officeSearchResult: OfficeSearchResponse? = null,
    val searchMode: SearchMode = SearchMode.RETAIL,
    val metaData: MetaResponse? = null,
    val error: String? = null,
    val recentSearches: List<String> = emptyList(),
    val hasSearched: Boolean = false,
    val archiveSchedule: ScheduleResponse? = null,
    val isArchiveLoading: Boolean = false,
    val archiveError: String? = null,
    val showArchiveSheet: Boolean = false,
    val selectedArchiveMonth: String = "",
    val syncSuccessEvent: Boolean = false
)

class MainScreenModel : StateScreenModel<MainState>(MainState()) {
    private val cache = LocalCache()
    private val api = ApiClient()
    val userName = cache.getUserName() ?: "Пользователь"
    val userRole = cache.getUserRole() ?: "user"
    val userStore = cache.getStoreName() ?: ""
    val currentDay = getCurrentDay()
    val userPosition = cache.getUserPosition() ?: "Сотрудник"
    var currentSearchQuery: String = ""
    val assignedStores = cache.getAssignedStores()

    fun clearError() {
        mutableState.value = mutableState.value.copy(error = null)
    }

    fun clearSyncSuccessEvent() {
        mutableState.value = mutableState.value.copy(syncSuccessEvent = false)
    }

    fun refreshSchedule() {
        if (userRole == "regional") {
            loadRegionalData(isForce = true)
        } else if (userRole == "user") {
            loadEmployeeData(isForce = true)
        }
        if (currentSearchQuery.isNotEmpty()) {
            searchSchedule(currentSearchQuery)
        }
    }

    init {
        val savedSearches = cache.settings.getString("recent_searches", "")
        if (savedSearches.isNotEmpty()) {
            mutableState.value = mutableState.value.copy(
                recentSearches = savedSearches.split(",")
            )
        }

        val cachedMeta = cache.getMeta()
        mutableState.value = mutableState.value.copy(metaData = cachedMeta)

        loadMetaData()

        if (userRole == "user" && userStore.isNotEmpty()) {
            val cachedData = cache.getSchedule()
            if (cachedData != null) {
                mutableState.value = mutableState.value.copy(
                    scheduleData = cachedData,
                    isLoading = false
                )
                val lastSync = cache.getLastSyncTimestamp()
                val now = Clock.System.now().toEpochMilliseconds()
                if (now - lastSync > 86_400_000L) {
                    loadEmployeeData(isForce = false)
                }
            } else {
                loadEmployeeData(isForce = true)
            }
        } else if (userRole == "regional") {
            val cachedData = cache.getSchedule()
            if (cachedData != null) {
                mutableState.value = mutableState.value.copy(
                    scheduleData = cachedData,
                    isLoading = false
                )
                val lastSync = cache.getLastSyncTimestamp()
                val now = Clock.System.now().toEpochMilliseconds()
                if (now - lastSync > 86_400_000L) {
                    loadRegionalData(isForce = false)
                }
            } else {
                loadRegionalData(isForce = true)
            }
        }
    }

    fun loadMetaData() {
        screenModelScope.launch {
            val currentMonth = getCurrentRussianMonth()
            try {
                val response = api.getMeta(monthSheet = currentMonth)
                if (response.success) {
                    cache.saveMeta(response)
                    mutableState.value = mutableState.value.copy(metaData = response)
                }
            } catch (_: Exception) {}
        }
    }

    fun loadRegionalData(isForce: Boolean = false) {
        if (assignedStores.isEmpty()) return

        screenModelScope.launch {
            if (mutableState.value.scheduleData == null) {
                mutableState.value = mutableState.value.copy(isLoading = true, error = null)
            } else {
                mutableState.value = mutableState.value.copy(isSyncing = true, error = null)
            }

            try {
                val currentMonth = getCurrentRussianMonth()
                val response = api.getSchedule(
                    storeName = assignedStores.joinToString(","),
                    employeeName = "",
                    monthSheet = currentMonth
                )

                if (response.success) {
                    cache.saveSchedule(response)
                    cache.saveLastSyncTimestamp(Clock.System.now().toEpochMilliseconds())
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        isSyncing = false,
                        scheduleData = response,
                        error = null,
                        syncSuccessEvent = isForce
                    )
                } else {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        isSyncing = false,
                        error = response.error ?: "Ошибка загрузки данных"
                    )
                }
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    isSyncing = false,
                    error = "Нет сети. Подключитесь к интернету и обновите"
                )
            }
        }
    }

    fun loadEmployeeData(isForce: Boolean = false) {
        screenModelScope.launch {
            if (mutableState.value.scheduleData == null) {
                mutableState.value = mutableState.value.copy(isLoading = true, error = null)
            } else {
                mutableState.value = mutableState.value.copy(isSyncing = true, error = null)
            }

            try {
                val currentMonth = getCurrentRussianMonth()
                val response = api.getSchedule(
                    storeName = userStore,
                    employeeName = userName,
                    monthSheet = currentMonth
                )

                if (response.success) {
                    cache.saveSchedule(response)
                    cache.saveLastSyncTimestamp(Clock.System.now().toEpochMilliseconds())
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        isSyncing = false,
                        scheduleData = response,
                        error = null,
                        syncSuccessEvent = isForce
                    )
                } else {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        isSyncing = false,
                        error = response.error ?: "Ошибка загрузки данных"
                    )
                }
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    isSyncing = false,
                    error = "Нет сети. Подключитесь к интернету и обновите"
                )
            }
        }
    }

    fun searchSchedule(searchQuery: String) {
        if (searchQuery.isBlank()) return

        screenModelScope.launch {
            mutableState.value =
                mutableState.value.copy(isLoading = true, error = null, hasSearched = true)
            currentSearchQuery = searchQuery

            if (mutableState.value.searchMode == SearchMode.RETAIL) {
                val currentMonth = getCurrentRussianMonth()
                val response = api.getSchedule(
                    storeName = searchQuery,
                    employeeName = "",
                    monthSheet = currentMonth
                )

                if (response.success) {
                    var workingToday = 0
                    var totalEmployees = 0

                    response.results.forEach { storeResult ->
                        totalEmployees += storeResult.schedule.size
                        val todayDate = getCurrentDay()

                        storeResult.schedule.forEach { emp ->
                            val todayWork = emp.days.find { it.date == todayDate }
                            if (todayWork?.status?.uppercase()?.contains("СМЕНА") == true) {
                                workingToday++
                            }
                        }
                    }

                    val empStat =
                        if (totalEmployees > 0) "$workingToday/$totalEmployees" else "-"
                    val cacheEntry = "$searchQuery|$empStat|RETAIL"

                    val filteredOld = mutableState.value.recentSearches.filter {
                        !it.startsWith("$searchQuery|") && !it.equals(
                            searchQuery,
                            ignoreCase = true
                        )
                    }
                    val updatedSearches = (listOf(cacheEntry) + filteredOld).take(10)
                    cache.settings.putString(
                        "recent_searches",
                        updatedSearches.joinToString(",")
                    )

                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        searchResult = response,
                        recentSearches = updatedSearches,
                        error = null
                    )
                } else {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        searchResult = null,
                        error = response.error
                    )
                }
            } else {
                val response = api.searchOffice(searchQuery)
                if (response.success) {
                    val empStat = "${response.results.size} чел."
                    val cacheEntry = "$searchQuery|$empStat|OFFICE"

                    val filteredOld = mutableState.value.recentSearches.filter {
                        !it.startsWith("$searchQuery|") && !it.equals(
                            searchQuery,
                            ignoreCase = true
                        )
                    }
                    val updatedSearches = (listOf(cacheEntry) + filteredOld).take(10)
                    cache.settings.putString(
                        "recent_searches",
                        updatedSearches.joinToString(",")
                    )

                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        officeSearchResult = response,
                        recentSearches = updatedSearches,
                        error = null
                    )
                } else {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        officeSearchResult = null,
                        error = response.error
                    )
                }
            }
        }
    }

    fun setSearchMode(mode: SearchMode) {
        if (mutableState.value.searchMode != mode) {
            mutableState.value = mutableState.value.copy(
                searchMode = mode,
                searchResult = null,
                officeSearchResult = null,
                hasSearched = false,
                error = null
            )
            currentSearchQuery = ""
        }
    }

    fun clearRecentSearches() {
        cache.settings.putString("recent_searches", "")
        mutableState.value = mutableState.value.copy(recentSearches = emptyList())
    }

    fun clearSearchResults() {
        mutableState.value = mutableState.value.copy(
            searchResult = null,
            officeSearchResult = null,
            hasSearched = false
        )
        currentSearchQuery = ""
    }

    fun loadArchiveSchedule(storeName: String, month: String) {
        screenModelScope.launch {
            mutableState.value = mutableState.value.copy(
                isArchiveLoading = true,
                showArchiveSheet = true,
                selectedArchiveMonth = month,
                archiveError = null,
                archiveSchedule = null
            )

            val response = api.getSchedule(
                storeName = storeName,
                employeeName = if (userRole == "user") userName else "",
                monthSheet = month
            )

            if (response.success) {
                mutableState.value = mutableState.value.copy(
                    isArchiveLoading = false,
                    archiveSchedule = response,
                    archiveError = null
                )
            } else {
                mutableState.value = mutableState.value.copy(
                    isArchiveLoading = false,
                    archiveError = response.error ?: "Не удалось загрузить архив"
                )
            }
        }
    }

    fun closeArchiveSheet() {
        mutableState.value = mutableState.value.copy(
            showArchiveSheet = false,
            archiveSchedule = null,
            archiveError = null
        )
    }
}