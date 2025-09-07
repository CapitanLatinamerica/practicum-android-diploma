package ru.practicum.android.diploma.search.domain

import ru.practicum.android.diploma.common.data.model.NetworkClient
import ru.practicum.android.diploma.common.data.model.VacanciesRequest
import ru.practicum.android.diploma.common.data.model.VacancyRequest

class SearchVacancyDetailsByIdUseCase(private val networkClient: NetworkClient) {
    suspend fun execute(id: String) {
        networkClient.doRequest(VacancyRequest(id))
    }
}
