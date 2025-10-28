package ru.practicum.android.diploma.vacancydetails.domain

import android.content.Context

interface SharingRepository {
    fun shareVacancy(context: Context, vacancyName: String?, vacancyUrl: String?)
}
