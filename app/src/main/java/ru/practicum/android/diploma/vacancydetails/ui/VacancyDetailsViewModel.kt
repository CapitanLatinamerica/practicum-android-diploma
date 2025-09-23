package ru.practicum.android.diploma.vacancydetails.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.ErrorMessageProvider
import ru.practicum.android.diploma.ErrorType
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.favourites.domain.db.FavouritesInteractor
import ru.practicum.android.diploma.search.domain.SearchVacancyDetailsUseCase
import ru.practicum.android.diploma.vacancydetails.domain.SharingInteractor

class VacancyDetailsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val detailsUseCase: SearchVacancyDetailsUseCase,
    private val favouritesInteractor: FavouritesInteractor,
    private val vacancyId: String,
    private val errorMessageProvider: ErrorMessageProvider
) : ViewModel() {

    // Состояние загрузки данных о вакансии
    private val _vacancyState = MutableStateFlow<VacancyDetailsState>(VacancyDetailsState.Loading)
    val vacancyState: StateFlow<VacancyDetailsState> = _vacancyState.asStateFlow()
    private var currentVacancy: Vacancy? = null

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

            val resource = detailsUseCase.getVacancyDetailsById(vacancyId)

            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { vacancy ->
                        currentVacancy = vacancy
                        _vacancyState.value = VacancyDetailsState.Content(vacancy)
                        _isLiked.value = favouritesInteractor.isFavourite(vacancyId)
                    } ?: handleRequestError("Vacancy data is null")
                }

                is Resource.Error -> {
                    handleRequestError(resource.message.orEmpty())
                }
            }
        }
    }

    // Переключение статуса избранного для вакансии
    fun toggleFavorite(vacancyId: String, vacancy: Vacancy?) {
        viewModelScope.launch {
            val currentIsLiked = _isLiked.value
            val newIsLiked = !currentIsLiked

            if (newIsLiked && vacancy != null) {
                favouritesInteractor.insertVacancy(vacancy)
            } else {
                favouritesInteractor.deleteVacancyById(vacancyId)
            }
            _isLiked.value = newIsLiked
        }
    }

    fun shareVacancy(context: Context) {
        currentVacancy?.let { vacancy ->
            sharingInteractor.shareVacancy(context, vacancy.name, vacancy.vacancyUrl)
        } ?: run {
            // Если вакансия еще не загружена
            sharingInteractor.shareVacancy(context, null, null)
        }
    }

    private fun handleRequestError(message: String) {
        val (errorType, displayMessage) = mapErrorMessage(message)
        _vacancyState.value = VacancyDetailsState.Error(errorType, displayMessage)
    }

    private fun mapErrorMessage(errMsg: String?): Pair<ErrorType, String> {
        return when (errMsg) {
            "404" -> {
                viewModelScope.launch {
                    favouritesInteractor.deleteVacancyById(vacancyId)
                }
                ErrorType.DENIED_VACANCY to errorMessageProvider.vacancyDenied()
            }

            else -> ErrorType.SERVER_ERROR to errorMessageProvider.serverError()
        }
    }
}
