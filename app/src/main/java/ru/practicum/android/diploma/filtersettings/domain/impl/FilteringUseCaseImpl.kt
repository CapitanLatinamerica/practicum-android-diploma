package ru.practicum.android.diploma.filtersettings.domain.impl

import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringRepository
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase

class FilteringUseCaseImpl(
    private val repository: FilteringRepository
) : FilteringUseCase {
    override suspend fun saveParameters(params: FilterParameters) {
        repository.saveParameters(params)
    }

    override suspend fun loadParameters(): FilterParameters? {
        return repository.loadParameters()
    }

    override suspend fun clearParameters() {
        repository.clearParameters()
    }
}
