package ru.practicum.android.diploma.common.data.domain.api

data class VacancyDto(
    val address: Address?,
    val areaDto: AreaDto?,
    val contacts: Contacts?,
    val description: String?,
    val employer: Employer?,
    val employment: Employment?,
    val experience: Experience?,
    val id: String?,
    val industryDto: IndustryDto?,
    val name: String?,
    val salary: Salary?,
    val schedule: Schedule?,
    val skills: List<String>?,
    val url: String?
)
