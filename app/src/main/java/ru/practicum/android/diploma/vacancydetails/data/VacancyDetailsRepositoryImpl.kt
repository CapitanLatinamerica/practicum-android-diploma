package ru.practicum.android.diploma.vacancydetails.data

import kotlinx.coroutines.flow.first
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.search.domain.usecase.SearchUseCase
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.search.domain.SearchVacancyDetailsByIdUseCase
import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetailsRepository

class VacancyDetailsRepositoryImpl(
    private val searchVacancyDetailsByIdUseCase: SearchVacancyDetailsByIdUseCase
) : VacancyDetailsRepository {

    override suspend fun getVacancyDetails(vacancyId: String): Resource<Vacancy> {
        val resourceFlow = searchVacancyDetailsByIdUseCase.searchVacancies(query = vacancyId, page = 0)
        val resource = resourceFlow.first()  // Получаем первый результат из Flow

        return when (resource) {
            is Resource.Success -> {
                val vacancy = resource.data?.items?.find { it.id == vacancyId }
                if (vacancy != null) {
                    Resource.Success(vacancy)
                } else {
                    Resource.Error("Vacancy with id $vacancyId not found")
                }
            }
            is Resource.Error -> {
                Resource.Error(resource.message ?: "Unknown error")
            }
            else -> Resource.Error("Loading state unsupported here")
        }
    }
}

