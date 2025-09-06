package ru.practicum.android.diploma.common.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.practicum.android.diploma.common.domain.db.FavouritesInteractor
import ru.practicum.android.diploma.common.domain.db.FavouritesRepository
import ru.practicum.android.diploma.common.domain.entity.Vacancy

class FavouritesInteractorImpl(private val favouritesRepository: FavouritesRepository) : FavouritesInteractor {
    override suspend fun insertVacancy(vacancy: Vacancy) {
        favouritesRepository.insertVacancy(vacancy)
    }

    override suspend fun deleteVacancyById(vacancyId: String) {
        favouritesRepository.deleteVacancyById(vacancyId)
    }

    override fun getAllVacancies(): Flow<List<Vacancy>> {
        return favouritesRepository.getAllVacancies()
    }

    override suspend fun getVacancyById(vacancyId: String): Vacancy? {
        return favouritesRepository.getVacancyById(vacancyId)
    }

    override suspend fun isFavourite(vacancyId: String): Boolean {
        return favouritesRepository.getAllVacancies()
            .first()
            .any { it.id == vacancyId }
    }
}
