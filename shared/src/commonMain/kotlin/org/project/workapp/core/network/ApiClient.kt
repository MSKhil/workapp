package org.project.workapp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ApiClient {
    private val scriptUrl = "https://script.google.com/macros/s/AKfycbyiQFKo8MZGs12oocgY2x_D58sF-h0rESjBPknXr0eNIZsYHf6s7F-hRxgLAcyuHoMU/exec"

    private val jsonFormatter = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(jsonFormatter)
        }
    }

    suspend fun getUsefulLinks(): Result<List<UsefulLinkDto>> {
        return try {
            val requestBody = jsonFormatter.encodeToString(
                ApiRequest(action = "get_links")
            )

            val initialResponse: HttpResponse = client.post(scriptUrl) {
                setBody(TextContent(requestBody, ContentType.Text.Plain))
            }

            val response: UsefulLinksResponse = handleResponse(initialResponse)
            if (response.success) {
                Result.success(response.links)
            } else {
                Result.failure(Exception(response.error ?: "Ошибка загрузки ссылок"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMeta(monthSheet: String): MetaResponse {
        return try {
            val requestBody = jsonFormatter.encodeToString(
                MetaRequest(action = "get_meta", monthSheet = monthSheet)
            )

            val initialResponse: HttpResponse = client.post(scriptUrl) {
                setBody(TextContent(requestBody, ContentType.Text.Plain))
            }

            handleResponse(initialResponse)
        } catch (e: Exception) {
            MetaResponse(success = false, error = "Ошибка сети: ${e.message}")
        }
    }

    suspend fun login(accessLevel: String, storeName: String, name: String, password: String): AuthResponse {
        return try {
            val requestBody = jsonFormatter.encodeToString(
                ApiRequest(
                    action = "login",
                    accessLevel = accessLevel,
                    storeName = storeName,
                    name = name,
                    password = password
                )
            )

            val initialResponse: HttpResponse = client.post(scriptUrl) {
                setBody(TextContent(requestBody, ContentType.Text.Plain))
            }

            handleResponse(initialResponse)
        } catch (e: Exception) {
            AuthResponse(success = false, error = "Ошибка сети: ${e.message}")
        }
    }

    suspend fun getSchedule(storeName: String, employeeName: String, monthSheet: String): ScheduleResponse {
        return try {
            val requestBody = jsonFormatter.encodeToString(
                ApiRequest(
                    action = "get_schedule",
                    storeName = storeName,
                    employeeName = employeeName,
                    monthSheet = monthSheet
                )
            )

            val initialResponse: HttpResponse = client.post(scriptUrl) {
                setBody(TextContent(requestBody, ContentType.Text.Plain))
            }

            handleResponse(initialResponse)
        } catch (e: Exception) {
            ScheduleResponse(success = false, error = "Ошибка сети: ${e.message}")
        }
    }

    private suspend inline fun <reified T> handleResponse(initialResponse: HttpResponse): T {
        if (initialResponse.status == HttpStatusCode.Found || initialResponse.status == HttpStatusCode.MovedPermanently) {
            val redirectUrl = initialResponse.headers[HttpHeaders.Location]
            if (redirectUrl != null) {
                val finalResponse: HttpResponse = client.get(redirectUrl)
                return finalResponse.body()
            }
        }

        return initialResponse.body()
    }

    suspend fun searchOffice(query: String): OfficeSearchResponse {
        return try {
            val requestBody = jsonFormatter.encodeToString(
                ApiRequest(action = "search_office", query = query)
            )

            val initialResponse: HttpResponse = client.post(scriptUrl) {
                setBody(TextContent(requestBody, ContentType.Text.Plain))
            }

            handleResponse(initialResponse)
        } catch (e: Exception) {
            OfficeSearchResponse(success = false, error = "Ошибка сети: ${e.message}")
        }
    }
}