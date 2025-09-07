package ru.practicum.android.diploma.common.data.domain.api

data class VacanciesDto(
    val found: Int,
    val items: List<VacancyDto>,
    val page: Int,
    val pages: Int
)
