package ru.practicum.android.diploma.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.ErrorMessageProvider
import ru.practicum.android.diploma.Event
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

    private val _isBottomLoading = MutableLiveData(false)
    val isBottomLoading: LiveData<Boolean> = _isBottomLoading

    private val _toastMessage = MutableLiveData<Event<String>?>()
    val toastMessage: LiveData<Event<String>?> = _toastMessage

    private var latestSearchText: String? = null

    private var debounceJob: Job? = null
    private var collectJob: Job? = null

    private var currentPage = 0
    private var maxPages = Int.MAX_VALUE
    private var isNextPageLoading = false
    private val requestedPages = mutableSetOf<Int>()
    private val vacanciesList = mutableListOf<VacancyUi>()

    init {
        _searchState.value = SearchState.Initial
    }

    fun clearSearch() {
        debounceJob?.cancel()
        collectJob?.cancel()
        latestSearchText = null
        resetPaging()
        _searchState.postValue(SearchState.Initial)
        _isBottomLoading.postValue(false)
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
        if (query.isBlank()) {
            clearSearch()
            return
        }
        collectJob?.cancel()
        resetPaging()
        _searchState.postValue(SearchState.Loading)
        loadPage(query, 1, isFirstRequest = true)
    }

    fun onLastItemReached() {
        val query = latestSearchText ?: return
        if (query.isBlank() || isNextPageLoading || currentPage >= maxPages) return
        loadPage(query, currentPage + 1, isFirstRequest = false)
    }

    private fun loadPage(query: String, page: Int, isFirstRequest: Boolean) {
        if (requestedPages.contains(page)) return

        startLoading(page, isFirstRequest)

        collectJob = viewModelScope.launch {
            searchUseCase.searchVacancies(query, page)
                .onCompletion { finishLoading(page) }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val pageObj = resource.data!!
                            handleSuccess(page, pageObj)
                        }

                        is Resource.Error -> {
                            handleRequestError(page, resource.message.orEmpty())
                        }
                    }
                }
        }
    }

    private fun startLoading(page: Int, isFirstRequest: Boolean) {
        requestedPages.add(page)
        isNextPageLoading = true
        if (!isFirstRequest) {
            _isBottomLoading.postValue(true)
        } else {
            _isBottomLoading.postValue(false)
        }
    }

    private fun handleSuccess(page: Int, pageObj: VacanciesPage) {
        currentPage = pageObj.page
        maxPages = pageObj.pages

        val newUi = pageObj.items.map { mapper.mapToUi(it) }
        if (page == 1) {
            vacanciesList.clear()
            vacanciesList.addAll(newUi)
        } else {
            vacanciesList.addAll(newUi)
        }

        if (page == 1 && vacanciesList.isEmpty()) {
            _searchState.postValue(
                SearchState.Empty(errorMessageProvider.nothingFound())
            )
        } else {
            _searchState.postValue(
                SearchState.Content(found = pageObj.found, vacancies = vacanciesList.toList())
            )
        }
    }

    private fun handleRequestError(page: Int, message: String) {
        val displayMessage = mapErrorMessage(message)
        if (page == 1) {
            _searchState.postValue(SearchState.Error(displayMessage))
        } else {
            _toastMessage.postValue(Event(displayMessage))
        }
    }

    private fun finishLoading(page: Int) {
        requestedPages.remove(page)
        isNextPageLoading = false
        _isBottomLoading.postValue(false)
    }

    private fun resetPaging() {
        currentPage = 0
        maxPages = Int.MAX_VALUE
        isNextPageLoading = false
        requestedPages.clear()
        vacanciesList.clear()
    }

    private fun mapErrorMessage(errMsg: String?): String {
        return when (errMsg) {
            "Проверьте подключение к интернету" -> errorMessageProvider.noInternet()
            "Ошибка сервера" -> errorMessageProvider.serverError()
            else -> errorMessageProvider.serverError()
        }
    }
}
