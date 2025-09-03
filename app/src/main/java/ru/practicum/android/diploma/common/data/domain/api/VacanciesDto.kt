package ru.practicum.android.diploma.common.data.domain.api

data class VacanciesDto(
    val found: Int,
    val items: List<Item>,
    val page: Int,
    val pages: Int
)
