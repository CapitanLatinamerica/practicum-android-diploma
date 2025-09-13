package ru.practicum.android.diploma.filterindustry.ui

import ru.practicum.android.diploma.common.domain.entity.Industry

sealed interface IndustryState {
    data object Error : IndustryState
    data class Content(val industryList: List<Industry>) : IndustryState
}
