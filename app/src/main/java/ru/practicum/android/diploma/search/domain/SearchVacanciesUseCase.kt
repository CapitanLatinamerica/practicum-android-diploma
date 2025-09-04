package ru.practicum.android.diploma.search.domain

import ru.practicum.android.diploma.common.data.model.NetworkClient
import ru.practicum.android.diploma.common.data.model.VacanciesRequest


class SearchVacanciesUseCase(private val networkClient: NetworkClient) {
    suspend fun execute() {
        networkClient.doRequest(VacanciesRequest())
    }
}
