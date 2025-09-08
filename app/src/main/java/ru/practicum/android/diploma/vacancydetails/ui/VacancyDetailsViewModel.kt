package ru.practicum.android.diploma.vacancydetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.VacancyRepository
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetailsState

class VacancyDetailsViewModel(
    private val repository: VacancyRepository,
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

            val resource = repository.getVacancyDetailsById(vacancyId)

            when (resource) {
                is Resource.Success -> {
                    _vacancyState.value = VacancyDetailsState.Content(resource.data!!)
                    // _isLiked.value = repository.isVacancyFavorite(vacancyId) // если нужно
                }
                is Resource.Error -> {
                    _vacancyState.value = VacancyDetailsState.Error(resource.message ?: "Unknown error")
                }
                else -> {
                    _vacancyState.value = VacancyDetailsState.Error("Unexpected resource state")
                }
            }
        }
    }

    // Переключение статуса избранного для вакансии
    fun toggleFavorite(vacancyId: String, vacancyDetails: Vacancy?) {
        viewModelScope.launch {
            val currentIsLiked = _isLiked.value
            val newIsLiked = !currentIsLiked

            if (newIsLiked && vacancyDetails != null) {
//                repository.addToFavorites(vacancyDetails)
            } else {
//                repository.removeFromFavorites(vacancyId)
            }
            _isLiked.value = newIsLiked
        }
    }
}
