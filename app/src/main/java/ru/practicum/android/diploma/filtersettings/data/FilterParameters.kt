package ru.practicum.android.diploma.filtersettings.data

data class FilterParameters(
    val workplace: String = "",
    val workplaceId: Int = 0,
    val industry: String = "",
    val industryId: Int = 0,
    val salary: String = "",
    val onlyWithSalary: Boolean = false
)
