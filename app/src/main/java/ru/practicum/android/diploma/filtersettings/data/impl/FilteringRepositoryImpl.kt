package ru.practicum.android.diploma.filtersettings.data.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.data.FilterStorage
import ru.practicum.android.diploma.filtersettings.domain.FilteringRepository

class FilteringRepositoryImpl(
    private val storage: FilterStorage
) : FilteringRepository {
    override suspend fun saveParameters(params: FilterParameters) {
        withContext(Dispatchers.IO) {
            storage.save(params)
        }
    }

    override suspend fun loadParameters(): FilterParameters? {
        return storage.load()
    }

    override suspend fun clearParameters() {
        withContext(Dispatchers.IO) {
            storage.clear()
        }
    }

    override suspend fun isNotBlank(): Boolean = loadParameters()?.let {
            it.onlyWithSalary ||
            it.country.isNotBlank() ||
            it.region.isNotBlank() ||
            it.industry.isNotBlank() ||
            it.salary.isNotBlank()
    } ?: false
}
