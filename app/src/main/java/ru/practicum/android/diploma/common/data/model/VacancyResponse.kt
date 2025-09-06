package ru.practicum.android.diploma.common.data.model

import ru.practicum.android.diploma.common.data.domain.api.Address
import ru.practicum.android.diploma.common.data.domain.api.Area
import ru.practicum.android.diploma.common.data.domain.api.Contacts
import ru.practicum.android.diploma.common.data.domain.api.Employer
import ru.practicum.android.diploma.common.data.domain.api.Employment
import ru.practicum.android.diploma.common.data.domain.api.Experience
import ru.practicum.android.diploma.common.data.domain.api.IndustryDto
import ru.practicum.android.diploma.common.data.domain.api.Salary
import ru.practicum.android.diploma.common.data.domain.api.Schedule

data class VacancyResponse(
    val address: Address,
    val area: Area,
    val contacts: Contacts,
    val description: String,
    val employer: Employer,
    val employment: Employment,
    val experience: Experience,
    val id: String,
    val industryDto: IndustryDto,
    val name: String,
    val salary: Salary,
    val schedule: Schedule,
    val skills: List<String>,
    val url: String
) : NetResponse()
