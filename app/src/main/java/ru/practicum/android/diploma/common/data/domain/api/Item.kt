package ru.practicum.android.diploma.common.data.domain.api

data class Item(
    val address: Address,
    val area: Area,
    val contacts: Contacts,
    val description: String,
    val employer: Employer,
    val employment: Employment,
    val experience: Experience,
    val id: String,
    val industry: Industry,
    val name: String,
    val salary: Salary,
    val schedule: Schedule,
    val skills: List<String>,
    val url: String
)
