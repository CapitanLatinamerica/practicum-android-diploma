package ru.practicum.android.diploma.vacancydetails.domain

data class VacancyDetails(
    val id: String,
    val title: String,
    val salary: String?,
    val companyName: String,
    val companyLogo: String?,
    val companyCity: String,
    val experience: String,
    val employmentType: String,
    val description: String,
    val responsibilities: String,
    val requirements: String,
    val conditions: String,
    val skills: String,
    val contactPerson: String?,
    val contactEmail: String?,
    val contactPhones: List<String>
)
