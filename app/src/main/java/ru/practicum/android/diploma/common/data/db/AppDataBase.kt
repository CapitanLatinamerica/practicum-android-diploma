package ru.practicum.android.diploma.common.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.practicum.android.diploma.common.data.db.dao.VacancyDao
import ru.practicum.android.diploma.common.data.db.entity.VacancyEntity

@Database(version = 3, entities = [VacancyEntity::class])
abstract class AppDataBase : RoomDatabase() {
    abstract fun vacancyDao(): VacancyDao
}
