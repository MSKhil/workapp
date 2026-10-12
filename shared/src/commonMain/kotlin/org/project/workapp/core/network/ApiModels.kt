package org.project.workapp.core.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiRequest(
    val action: String,
    val monthSheet: String = "",
    val accessLevel: String = "",
    val storeName: String = "",
    val login: String = "",
    val password: String = "",
    val name: String = "",
    val employeeName: String = "",
    val query: String = ""
)

@Serializable
data class UsefulLinkDto(
    val title: String,
    val description: String,
    val url: String,
    val type: String
)

@Serializable
data class UsefulLinksResponse(
    val success: Boolean,
    val links: List<UsefulLinkDto> = emptyList(),
    val error: String? = null
)

@Serializable
data class MetaResponse(
    val success: Boolean,
    val stores: Map<String, List<String>> = emptyMap(),
    val admins: List<String> = emptyList(),
    val regionals: List<String> = emptyList(),
    val availableMonths: List<String> = emptyList(),
    val error: String? = null
)

@Serializable
data class MetaData(
    val admins: List<String> = emptyList(),
    val stores: Map<String, List<String>> = emptyMap()
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val role: String? = null,
    val name: String? = null,
    val store: String? = null,
    val position: String? = null,
    val stores: List<String> = emptyList(),
    val error: String? = null
)

@Serializable
data class ScheduleResponse(
    val success: Boolean,
    val actual_version: String? = null,
    val results: List<StoreScheduleResult> = emptyList(),
    val error: String? = null
)

@Serializable
data class StoreScheduleResult(
    val storeName: String? = null,
    val contacts: StoreContacts? = null,
    val schedule: List<EmployeeSchedule> = emptyList(),
    val salary: SalaryData? = null
)

@Serializable
data class EmployeeSchedule(
    val name: String,
    val days: List<WorkDay>
)

@Serializable
data class WorkDay(
    val date: String,
    val status: String,
    val time: String
)

@Serializable
data class SalaryData(
    val cash: String,
    val card: String
)

@Serializable
data class MetaRequest(
    val action: String,
    val monthSheet: String
)

@Serializable
data class StoreContacts(
    val internal: String = "",
    val phone: String = "",
    val email: String = ""
)

@Serializable
data class OfficeSearchResponse(
    val success: Boolean,
    val results: List<OfficeEmployee> = emptyList(),
    val error: String? = null
)

@Serializable
data class OfficeEmployee(
    val name: String,
    val position: String,
    val phone: String,
    val internal: String,
    val email: String,
    val questions: String
)