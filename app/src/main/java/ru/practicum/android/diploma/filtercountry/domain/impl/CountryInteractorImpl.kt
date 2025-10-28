package ru.practicum.android.diploma.filtercountry.domain.impl

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.filtercountry.domain.CountryInteractor
import ru.practicum.android.diploma.filtercountry.domain.CountryRepository

class CountryInteractorImpl(private val countryRepository: CountryRepository) : CountryInteractor {
    override suspend fun getCountries(): Resource<List<Area>> {
        return countryRepository.getCountries()
    }
}
