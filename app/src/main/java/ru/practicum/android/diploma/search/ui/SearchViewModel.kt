package ru.practicum.android.diploma.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.ErrorMessageProvider
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.search.domain.model.VacanciesPage
import ru.practicum.android.diploma.search.domain.usecase.SearchUseCase
import ru.practicum.android.diploma.search.ui.model.VacancyToVacancyUiMapper
import ru.practicum.android.diploma.search.ui.model.VacancyUi

class SearchViewModel(
    private val searchUseCase: SearchUseCase,
    private val mapper: VacancyToVacancyUiMapper,
    private val errorMessageProvider: ErrorMessageProvider
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private var latestSearchText: String? = null

    private var debounceJob: Job? = null
    private var collectJob: Job? = null

    init {
        if (_searchState.value == null) {
            _searchState.value = SearchState.Initial
        }
    }

    fun clearSearch() {
        debounceJob?.cancel()
        collectJob?.cancel()
        latestSearchText = null
        _searchState.postValue(SearchState.Initial)
    }

    fun searchDebounce(changedText: String) {
        if (changedText == latestSearchText) return
        latestSearchText = changedText

        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    private fun searchRequest(query: String) {
        if (query.isBlank()) return
        collectJob?.cancel()

        renderState(SearchState.Loading)

        collectJob = viewModelScope.launch {
            searchUseCase.searchVacancies(query).collect { resource ->
                processResult(resource)
            }
        }
    }

    private fun processResult(resource: Resource<VacanciesPage>) {
        when (resource) {
            is Resource.Success -> {
                val page = resource.data
                if (page == null || page.items.isEmpty()) {
                    _searchState.postValue(SearchState.Empty(errorMessageProvider.nothingFound()))
                } else {
                    val uiList: List<VacancyUi> = page.items.map { mapper.mapToUi(it) }
                    _searchState.postValue(SearchState.Content(found = page.found, vacancies = uiList))
                }
            }

            is Resource.Error -> {
                val errorMessage = resource.message.orEmpty()
                val displayMessage = when (errorMessage) {
                    "Проверьте подключение к интернету" -> errorMessageProvider.noInternet()
                    "Ошибка сервера" -> errorMessageProvider.serverError()
                    else -> errorMessageProvider.serverError()
                }
                _searchState.postValue(SearchState.Error(displayMessage))
            }
        }
    }

    private fun renderState(state: SearchState) {
        _searchState.postValue(state)
    }
}
