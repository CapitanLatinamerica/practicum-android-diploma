package ru.practicum.android.diploma.filtersettings.ui.mapper

import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.ui.FilterState

class FilterParametersMapper {
    fun mapParamsToUi(params: FilterParameters): FilterState {
        return FilterState(
            country = params.country,
            industry = params.industry,
            salary = params.salary,
            onlyWithSalary = params.onlyWithSalary
        )
    }

    fun mapParamsToDomain(params: FilterState): FilterParameters {
        return FilterParameters(
            country = params.country,
            industry = params.industry,
            salary = params.salary,
            onlyWithSalary = params.onlyWithSalary
        )
    }
}
