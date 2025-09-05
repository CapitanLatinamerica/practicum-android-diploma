package ru.practicum.android.diploma.vacancydetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetails
import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetailsRepository

class VacancyDetailsViewModel(
    private val repository: VacancyDetailsRepository,
    private val vacancyId: String
) : ViewModel() {

    // Состояние загрузки данных о вакансии
    private val _vacancyState = MutableStateFlow<VacancyDetailsState>(VacancyDetailsState.Loading)
    val vacancyState: StateFlow<VacancyDetailsState> = _vacancyState.asStateFlow()

    // Состояние избранного (лайк/нелайк)
    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    // Загрузка данных при создании ViewModel
    init {
        loadVacancyDetails()
    }

    // Загрузка детальной информации о вакансии из репозитория
    internal fun loadVacancyDetails() {
        viewModelScope.launch {
            _vacancyState.value = VacancyDetailsState.Loading
            try {
                val vacancyDetails = repository.getVacancyDetails(vacancyId)
                _vacancyState.value = VacancyDetailsState.Content(vacancyDetails)
                _isLiked.value = repository.isVacancyFavorite(vacancyId)
            } catch (e: Exception) {
                _vacancyState.value = VacancyDetailsState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    // Переключение статуса избранного для вакансии
    fun toggleFavorite(vacancyId: String, vacancyDetails: VacancyDetails?) {
        viewModelScope.launch {
            val currentIsLiked = _isLiked.value
            val newIsLiked = !currentIsLiked

            if (newIsLiked && vacancyDetails != null) {
                repository.addToFavorites(vacancyDetails)
            } else {
                repository.removeFromFavorites(vacancyId)
            }
            _isLiked.value = newIsLiked
        }
    }
}

// состояния экрана деталей вакансии
sealed interface VacancyDetailsState {
    object Loading : VacancyDetailsState
    data class Content(val vacancy: VacancyDetails) : VacancyDetailsState
    data class Error(val message: String) : VacancyDetailsState
}
