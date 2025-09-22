package ru.practicum.android.diploma.filtersettings.ui.mapper

import ru.practicum.android.diploma.common.domain.entity.FilteredVacancyParameters
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.ui.FilterState

class FilterParametersMapper {
    fun mapParamsToUi(params: FilterParameters): FilterState {
        return FilterState(
            country = params.country,
            region = params.region,
            industry = params.industry,
            salary = params.salary,
            onlyWithSalary = params.onlyWithSalary
        )
    }

    fun mapToSearchParams(storage: FilterParameters?): FilteredVacancyParameters {
        if (storage == null) {
            return FilteredVacancyParameters(null, null, null, null, null, null)
        }

        val areaId = storage.regionId.takeIf { it != 0 } ?: storage.countryId.takeIf { it != 0 }
        val industryId = storage.industryId.takeIf { it != 0 }

        val salaryInt = storage.salary
            .trim()
            .takeIf { it.isNotEmpty() }
            ?.toIntOrNull()

        val onlyWithSalary = storage.onlyWithSalary.takeIf { it }

        return FilteredVacancyParameters(
            areaId = areaId,
            industryId = industryId,
            text = null,
            salary = salaryInt,
            page = null,
            onlyWithSalary = onlyWithSalary
        )
    }
}
