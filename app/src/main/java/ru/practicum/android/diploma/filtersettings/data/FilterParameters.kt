package ru.practicum.android.diploma.filtersettings.data

data class FilterParameters(
    val country: String = "",
    val countryId: Int = 0,
    val industry: String = "",
    val salary: String = "",
    val onlyWithSalary: Boolean = false,
    val region: String = "",
    val regionId: Int = 0
)
