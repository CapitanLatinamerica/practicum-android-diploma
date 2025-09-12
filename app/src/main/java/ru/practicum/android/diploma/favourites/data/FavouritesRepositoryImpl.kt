package ru.practicum.android.diploma.favourites.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.common.data.db.AppDataBase
import ru.practicum.android.diploma.common.data.db.entity.VacancyEntity
import ru.practicum.android.diploma.common.data.mapper.VacancyMapper
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.favourites.domain.db.FavouritesRepository

class FavouritesRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val vacancyMapper: VacancyMapper,
) : FavouritesRepository {
    override suspend fun insertVacancy(vacancy: Vacancy) {
        appDataBase.vacancyDao().insertVacancy(convertToEntity(vacancy))
    }

    override suspend fun deleteVacancyById(vacancyId: String) {
        appDataBase.vacancyDao().deleteVacancyById(vacancyId)
    }

    override fun getAllVacancies(): Flow<List<Vacancy>> {
        return appDataBase.vacancyDao().getAllVacancies()
            .map { entities -> convertFromEntity(entities) }
    }

    override suspend fun getVacancyById(vacancyId: String): Vacancy? {
        val entity = appDataBase.vacancyDao().getVacancyById(vacancyId)
        return entity?.let { vacancyMapper.mapFromEntityToVacancy(it) }
    }

    private fun convertFromEntity(vacancies: List<VacancyEntity>): List<Vacancy> {
        return vacancies.map { vacancyEntity -> vacancyMapper.mapFromEntityToVacancy(vacancyEntity) }
    }

    private fun convertToEntity(vacancy: Vacancy): VacancyEntity {
        return vacancyMapper.mapFromVacancyToEntity(vacancy)
    }
}
