package ru.practicum.android.diploma.common.data.model

import ru.practicum.android.diploma.common.data.domain.api.AddressDto
import ru.practicum.android.diploma.common.data.domain.api.AreaDto
import ru.practicum.android.diploma.common.data.domain.api.ContactsDto
import ru.practicum.android.diploma.common.data.domain.api.EmployerDto
import ru.practicum.android.diploma.common.data.domain.api.EmploymentDto
import ru.practicum.android.diploma.common.data.domain.api.ExperienceDto
import ru.practicum.android.diploma.common.data.domain.api.IndustryDto
import ru.practicum.android.diploma.common.data.domain.api.SalaryDto
import ru.practicum.android.diploma.common.data.domain.api.ScheduleDto

data class VacancyResponse(
    val addressDto: AddressDto,
    val areaDto: AreaDto,
    val contactsDto: ContactsDto,
    val description: String,
    val employerDto: EmployerDto,
    val employmentDto: EmploymentDto,
    val experienceDto: ExperienceDto,
    val id: String,
    val industryDto: IndustryDto,
    val name: String,
    val salaryDto: SalaryDto,
    val scheduleDto: ScheduleDto,
    val skills: List<String>,
    val url: String
) : NetResponse()
