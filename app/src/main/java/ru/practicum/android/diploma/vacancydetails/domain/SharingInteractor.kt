package ru.practicum.android.diploma.vacancydetails.domain

import android.content.Context

interface SharingInteractor {
    fun shareVacancy(context: Context, vacancyName: String?, vacancyUrl: String?)
}
