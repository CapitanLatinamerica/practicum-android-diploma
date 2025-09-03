package ru.practicum.android.diploma.common.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Vacancies_table")
data class VacancyEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val salaryCurrency: String,
    val salaryFrom: Int,
    val salaryTo: Int,
    val logoPath: String,
    val area: String,
    val employer: String,
    val experience: String,
    val employment: String,
    val schedule: String,
    val description: String
)
