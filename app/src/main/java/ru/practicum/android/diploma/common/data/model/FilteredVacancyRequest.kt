package ru.practicum.android.diploma.common.data.model

import retrofit2.http.Query

class FilteredVacancyRequest(
    val areaId: Int?,
    val industryId: Int?,
    val text: String?,
    val salary: Int?,
    val page: Int?,
    val onlyWithSalary: Boolean?
)
