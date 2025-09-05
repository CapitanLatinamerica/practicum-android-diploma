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
    val responsibilities: List<String>,
    val requirements: List<String>,
    val conditions: List<String>,
    val skills: List<String>,
    val contactPerson: String?,
    val contactEmail: String?,
    val contactPhones: List<String>
)
