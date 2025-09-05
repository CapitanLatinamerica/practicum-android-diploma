// VacancyDetailsViewModelFactory.kt
package ru.practicum.android.diploma.vacancydetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetailsRepository

class VacancyDetailsViewModelFactory(
    private val repository: VacancyDetailsRepository,
    private val vacancyId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VacancyDetailsViewModel::class.java)) {
            return VacancyDetailsViewModel(repository, vacancyId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
