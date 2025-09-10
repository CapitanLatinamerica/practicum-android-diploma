package ru.practicum.android.diploma.common.domain.entity

data class Vacancy(
    val id: String,
    val name: String,
    val salaryCurrency: String?,
    val salaryFrom: Int?,
    val salaryTo: Int?,
    val logo: String?,
    val area: String?,
    val employer: String?,
    val experience: String?,
    val employment: String?,
    val schedule: String?,
    val description: String?,
    val skills: String?
)
