package ru.practicum.android.diploma.common.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.practicum.android.diploma.common.data.db.entity.VacancyEntity

@Dao
interface VacancyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vacancy: VacancyEntity)

    @Delete
    suspend fun delete(vacancy: VacancyEntity)

    @Query("DELETE FROM vacancies_table WHERE id = :vacancyId")
    suspend fun deleteById(vacancyId: String)

    @Query("SELECT * FROM vacancies_table")
    suspend fun getAll(): List<VacancyEntity>

    @Query("SELECT * FROM vacancies_table WHERE id = :vacancyId")
    suspend fun getById(vacancyId: String): VacancyEntity?

    @Query("SELECT EXISTS(SELECT * FROM vacancies_table WHERE id = :vacancyId)")
    suspend fun isFavorite(vacancyId: String): Boolean
}
