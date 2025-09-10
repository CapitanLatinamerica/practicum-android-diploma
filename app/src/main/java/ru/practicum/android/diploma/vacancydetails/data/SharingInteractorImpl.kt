package ru.practicum.android.diploma.vacancydetails.data

import android.content.Context
import ru.practicum.android.diploma.vacancydetails.domain.SharingInteractor
import ru.practicum.android.diploma.vacancydetails.domain.SharingRepository

class SharingInteractorImpl(private val sharingRepository: SharingRepository) : SharingInteractor {
    override fun shareVacancy(context: Context, vacancyUrl: String?) {
        sharingRepository.shareVacancy(context, vacancyUrl)
    }

}
