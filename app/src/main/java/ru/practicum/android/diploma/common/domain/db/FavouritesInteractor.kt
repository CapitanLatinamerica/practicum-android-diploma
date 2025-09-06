package ru.practicum.android.diploma.common.domain.db

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.domain.entity.Vacancy

interface FavouritesInteractor {

    suspend fun insertVacancy(vacancy: Vacancy)

    suspend fun deleteVacancyById(vacancyId: String)

    fun getAllVacancies(): Flow<List<Vacancy>>

    suspend fun getVacancyById(vacancyId: String): Vacancy?

    suspend fun isFavourite(vacancyId: String): Boolean
}
