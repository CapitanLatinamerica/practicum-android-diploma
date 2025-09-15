package ru.practicum.android.diploma.filtersettings.data

data class FilterParameters(
    val workplace: String = "",
    val industry: String = "",
    val salary: String = "",
    val onlyWithSalary: Boolean = false,
    val region: String = ""
)
