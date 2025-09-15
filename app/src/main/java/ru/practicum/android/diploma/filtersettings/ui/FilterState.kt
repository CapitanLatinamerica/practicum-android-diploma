package ru.practicum.android.diploma.filtersettings.ui

data class FilterState(
    val country: String = "",
    val region: String = "",
    val industry: String = "",
    val salary: String = "",
    val onlyWithSalary: Boolean = false
)
