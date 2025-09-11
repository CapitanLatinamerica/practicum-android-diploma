package ru.practicum.android.diploma.vacancydetails.ui.model

// UI-модель для отображения детальной информации о вакансии
data class VacancyDetailsUi(
    val id: String,
    val name: String,
    val salaryText: String,
    val logoUrl: String?,
    val area: String?,
    val employer: String?,
    val experience: String?,
    val employment: String?,
    val schedule: String?,
    val description: String?
)
