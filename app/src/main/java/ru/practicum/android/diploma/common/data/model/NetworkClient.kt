package ru.practicum.android.diploma.common.data.model

interface NetworkClient {
    suspend fun doRequest(dto: Any): NetResponse
}
