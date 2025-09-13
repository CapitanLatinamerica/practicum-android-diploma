package ru.practicum.android.diploma.filtercountry.domain

import ru.practicum.android.diploma.common.domain.entity.Area

interface CountryInteractor {
    suspend fun getCountries(): List<Area>
}
