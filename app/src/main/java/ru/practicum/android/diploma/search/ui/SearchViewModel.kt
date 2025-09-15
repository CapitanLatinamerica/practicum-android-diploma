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
import ru.practicum.android.diploma.ErrorType
import ru.practicum.android.diploma.Event
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.FilteredVacancyParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase
import ru.practicum.android.diploma.filtersettings.ui.mapper.FilterParametersMapper
import ru.practicum.android.diploma.search.domain.model.VacanciesPage
import ru.practicum.android.diploma.search.domain.usecase.SearchUseCase
import ru.practicum.android.diploma.search.ui.model.VacancyToVacancyUiMapper
import ru.practicum.android.diploma.search.ui.model.VacancyUi

class SearchViewModel(
    private val searchUseCase: SearchUseCase,
    private val vacancyToVacancyUiMapper: VacancyToVacancyUiMapper,
    private val errorMessageProvider: ErrorMessageProvider,
    private val filteringUseCase: FilteringUseCase,
    private val filterParametersMapper: FilterParametersMapper
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
    var isFilterParametersBlank = false

    private var currentFilterParams = FilteredVacancyParameters(
        areaId = null,
        industryId = null,
        text = null,
        salary = null,
        page = null,
        onlyWithSalary = null
    )

    init {
//        _searchState.postValue(SearchState.Initial)
        viewModelScope.launch {
            val saved = filteringUseCase.loadParameters()
            val mapped = filterParametersMapper.mapToSearchParams(saved)
            currentFilterParams = mapped
        }
        var isFilterParametersBlank = false
        _searchState.value = SearchState.Initial(isFilterParametersBlank)
    }

    fun checkFilterStatus() {
        viewModelScope.launch {
            isFilterParametersBlank = filteringUseCase.isNotBlank()
        }
    }

    fun clearSearch() {
        debounceJob?.cancel()
        collectJob?.cancel()
        latestSearchText = null
        resetPaging()
//        _searchState.value = SearchState.Initial
        _searchState.postValue(SearchState.Initial(isFilterParametersBlank))
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
            val params = buildParamsForSearch(page, query)

            searchUseCase.searchVacancies(params)
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

        val newUi = pageObj.items.map { vacancyToVacancyUiMapper.mapToUi(it) }
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
        val (errorType, displayMessage) = mapErrorMessage(message)
        if (page == 1) {
            _searchState.postValue(SearchState.Error(errorType, displayMessage))
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

    private fun mapErrorMessage(errMsg: String?): Pair<ErrorType, String> {
        return when (errMsg) {
            "Проверьте подключение к интернету" -> ErrorType.NO_INTERNET to errorMessageProvider.noInternet()
            "Ошибка сервера" -> ErrorType.SERVER_ERROR to errorMessageProvider.serverError()
            else -> ErrorType.SERVER_ERROR to errorMessageProvider.serverError()
        }
    }

    private fun buildParamsForSearch(page: Int, text: String?): FilteredVacancyParameters {
        val finalText = text?.takeIf { it.isNotBlank() }

        val areaId = currentFilterParams.areaId?.takeIf { it != 0 }
        val industryId = currentFilterParams.industryId?.takeIf { it != 0 }
        val salary = currentFilterParams.salary?.takeIf { it != 0 }
        val onlyWithSalary = currentFilterParams.onlyWithSalary?.takeIf { it }

        return FilteredVacancyParameters(
            areaId = areaId,
            industryId = industryId,
            text = finalText,
            salary = salary,
            page = page,
            onlyWithSalary = onlyWithSalary
        )
    }

    // Это событие, когда была нажата кнопка "Применить" на экране фильтров
    private fun applyFilters(newParams: FilteredVacancyParameters) {
        currentFilterParams = FilteredVacancyParameters(
            areaId = newParams.areaId,
            industryId = newParams.industryId,
            text = null,
            salary = newParams.salary,
            page = null,
            onlyWithSalary = newParams.onlyWithSalary
        )

        resetPaging()

        val lastText = latestSearchText
        if (!lastText.isNullOrBlank()) {
            _searchState.postValue(SearchState.Loading)
            loadPage(lastText, 1, isFirstRequest = true)
        } else {
//            _searchState.postValue(SearchState.Initial)
            _searchState.postValue(SearchState.Initial(isFilterParametersBlank))
        }
    }

    // Это обработчик внешнего сигнала, что фильтры были изменены (или просто сохранились)
    fun onFiltersApplied(performSearch: Boolean) {
        viewModelScope.launch {
            val savedParams = filteringUseCase.loadParameters()
            val mapped = filterParametersMapper.mapToSearchParams(savedParams)

            if (mapped != currentFilterParams) {
                if (performSearch) {
                    applyFilters(mapped)
                } else {
                    currentFilterParams = mapped
                }
            }
        }
    }
}
