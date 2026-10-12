package org.project.workapp.core.database

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.project.workapp.core.network.MetaResponse
import org.project.workapp.core.network.ScheduleResponse
import org.project.workapp.core.network.UsefulLinkDto

class LocalCache {
    val settings = Settings()

    fun saveSession(
        role: String,
        name: String,
        storeName: String?,
        position: String = "",
        stores: List<String> = emptyList()
    ) {
        settings["is_logged_in"] = true
        settings["role"] = role
        settings["name"] = name
        settings["position"] = position
        if (storeName != null) {
            settings["store_name"] = storeName
        }
        if (stores.isNotEmpty()) {
            settings["assigned_stores"] = stores.joinToString(";")
        }
    }

    fun getUserPosition(): String = settings.getString("position", "")

    fun getAssignedStores(): List<String> {
        val raw = settings.getString("assigned_stores", "")
        return if (raw.isEmpty()) emptyList() else raw.split(";")
    }

    fun isLoggedIn(): Boolean = settings.getBoolean("is_logged_in", false)
    fun getUserRole(): String = settings.getString("role", "")
    fun getUserName(): String = settings.getString("name", "")
    fun getStoreName(): String = settings.getString("store_name", "")

    fun clearSession() {
        settings.clear()
    }

    fun saveSchedule(scheduleData: ScheduleResponse) {
        val jsonString = Json.encodeToString(scheduleData)
        settings["schedule_cache"] = jsonString
    }

    fun getSchedule(): ScheduleResponse? {
        val jsonString = settings.getString("schedule_cache", "")
        if (jsonString.isEmpty()) return null

        return try {
            Json.decodeFromString<ScheduleResponse>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    fun saveMeta(metaData: MetaResponse) {
        val jsonString = Json.encodeToString(metaData)
        settings["meta_cache"] = jsonString
    }

    fun getMeta(): MetaResponse? {
        val jsonString = settings.getString("meta_cache", "")
        if (jsonString.isEmpty()) return null
        return try {
            Json.decodeFromString<MetaResponse>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    fun saveUsefulLinks(links: List<UsefulLinkDto>) {
        val jsonString = Json.encodeToString(links)
        settings["useful_links_cache"] = jsonString
    }

    fun getUsefulLinks(): List<UsefulLinkDto> {
        val jsonString = settings.getString("useful_links_cache", "")
        if (jsonString.isEmpty()) return emptyList()
        return try {
            Json.decodeFromString<List<UsefulLinkDto>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveLastSelectedRegionalStore(storeName: String) {
        settings["last_regional_store"] = storeName
    }

    fun getLastSelectedRegionalStore(): String = settings.getString("last_regional_store", "")

    fun saveLastSyncTimestamp(timeMs: Long) {
        settings.putLong("last_sync_time", timeMs)
    }

    fun getLastSyncTimestamp(): Long {
        return settings.getLong("last_sync_time", 0L)
    }
}