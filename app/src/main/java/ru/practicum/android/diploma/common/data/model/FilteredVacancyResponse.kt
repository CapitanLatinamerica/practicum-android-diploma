package ru.practicum.android.diploma.common.data.model

import ru.practicum.android.diploma.common.data.domain.api.Address
import ru.practicum.android.diploma.common.data.domain.api.Area
import ru.practicum.android.diploma.common.data.domain.api.Contacts
import ru.practicum.android.diploma.common.data.domain.api.Employer
import ru.practicum.android.diploma.common.data.domain.api.Employment
import ru.practicum.android.diploma.common.data.domain.api.Experience
import ru.practicum.android.diploma.common.data.domain.api.Industry
import ru.practicum.android.diploma.common.data.domain.api.Salary
import ru.practicum.android.diploma.common.data.domain.api.Schedule
import ru.practicum.android.diploma.common.data.domain.api.VacanciesDto
import ru.practicum.android.diploma.common.data.domain.api.VacancyDto

data class FilteredVacancyResponse(
    val found: Int,
    val items: List<VacanciesDto>,
    val page: Int,
    val pages: Int
) : NetResponse()
