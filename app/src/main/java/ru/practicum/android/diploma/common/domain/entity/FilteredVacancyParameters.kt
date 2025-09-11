package ru.practicum.android.diploma.common.domain.entity

class FilteredVacancyParameters(
    val areaId: Int?,
    val industryId: Int?,
    val text: String?,
    val salary: Int?,
    val page: Int?,
    val onlyWithSalary: Boolean?
)
