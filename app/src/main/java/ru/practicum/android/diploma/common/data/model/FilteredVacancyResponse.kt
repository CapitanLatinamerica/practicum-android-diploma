package ru.practicum.android.diploma.common.data.model

import ru.practicum.android.diploma.common.data.domain.api.VacancyDto

data class FilteredVacancyResponse(
    val found: Int,
    val items: List<VacancyDto>,
    val page: Int,
    val pages: Int
) : NetResponse()
