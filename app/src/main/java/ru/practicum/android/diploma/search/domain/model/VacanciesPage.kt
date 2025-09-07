package ru.practicum.android.diploma.search.domain.model

import ru.practicum.android.diploma.common.domain.entity.Vacancy

data class VacanciesPage(
    val found: Int,
    val items: List<Vacancy>,
    val page: Int,
    val pages: Int
)
