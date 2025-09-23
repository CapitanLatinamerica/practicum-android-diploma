package ru.practicum.android.diploma.common.data.network

import ru.practicum.android.diploma.common.data.model.NetResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): NetResponse
}
