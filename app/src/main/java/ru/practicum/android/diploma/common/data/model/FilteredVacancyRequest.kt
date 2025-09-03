package ru.practicum.android.diploma.common.data.model

class FilteredVacancyRequest(
    val areaId: Int?,
    val industryId: Int?,
    val text: String?,
    val salary: Int?,
    val page: Int?,
    val onlyWithSalary: Boolean?
)
