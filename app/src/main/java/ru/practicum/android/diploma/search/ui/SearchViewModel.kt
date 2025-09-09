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

    private var latestSearchText: String? = null

    private var debounceJob: Job? = null
    private var collectJob: Job? = null

    private var currentPage = 0
    private var maxPages = Int.MAX_VALUE
    private var isNextPageLoading = false
    private val requestedPages = mutableSetOf<Int>()
    private val seenIds = LinkedHashSet<String>()
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
        loadPage(query, 1, isInitial = true)
    }

    fun onLastItemReached() {
        val query = latestSearchText ?: return
        if (query.isBlank() || isNextPageLoading || currentPage >= maxPages) return
        loadPage(query, currentPage + 1, isInitial = false)
    }

    private fun loadPage(query: String, page: Int, isInitial: Boolean) {
        if (requestedPages.contains(page)) return

        requestedPages.add(page)
        isNextPageLoading = true
        if (!isInitial) {
            _isBottomLoading.postValue(true)
        } else {
            _isBottomLoading.postValue(false)
        }

        collectJob = viewModelScope.launch {
            searchUseCase.searchVacancies(query, page).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val pageObj = resource.data!!
                        currentPage = pageObj.page
                        maxPages = pageObj.pages

                        val newUi = pageObj.items.map { mapper.mapToUi(it) }
                        val filtered = newUi.filter { seenIds.add(it.id) }
                        if (page == 1) {
                            vacanciesList.clear()
                            vacanciesList.addAll(filtered)
                        } else {
                            vacanciesList.addAll(filtered)
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

                    is Resource.Error -> {
                        val errMsg = resource.message.orEmpty()
                        if (page == 1) {
                            val displayMessage = when (errMsg) {
                                "Проверьте подключение к интернету" -> errorMessageProvider.noInternet()
                                "Ошибка сервера" -> errorMessageProvider.serverError()
                                else -> errorMessageProvider.serverError()
                            }
                            _searchState.postValue(SearchState.Error(displayMessage))
                        }
                    }
                }
            }
            requestedPages.remove(page)
            isNextPageLoading = false
            _isBottomLoading.postValue(false)
        }
    }

    private fun resetPaging() {
        currentPage = 0
        maxPages = Int.MAX_VALUE
        isNextPageLoading = false
        requestedPages.clear()
        seenIds.clear()
        vacanciesList.clear()
    }
}
