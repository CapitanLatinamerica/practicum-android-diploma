package ru.practicum.android.diploma.filtersettings.domain

import ru.practicum.android.diploma.filtersettings.data.FilterParameters

interface FilteringRepository {
    suspend fun saveParameters(params: FilterParameters)

    suspend fun loadParameters(): FilterParameters?

    suspend fun clearParameters()

    suspend fun isNotBlank(): Boolean
}
