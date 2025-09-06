// VacancyDetailsViewModelFactory.kt
package ru.practicum.android.diploma.vacancydetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetailsRepository

// Фабрика для создания VacancyDetailsViewModel
class VacancyDetailsViewModelFactory(
    private val repository: VacancyDetailsRepository, // Зависимость репозитория
    private val vacancyId: String // ID вакансии для загрузки
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Проверяем, что запрашивается правильный класс ViewModel
        if (modelClass.isAssignableFrom(VacancyDetailsViewModel::class.java)) {
            return VacancyDetailsViewModel(repository, vacancyId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
