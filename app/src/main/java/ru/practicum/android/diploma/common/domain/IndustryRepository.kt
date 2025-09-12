package ru.practicum.android.diploma.common.domain

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Industry

interface IndustryRepository {
    suspend fun getIndustries(): Resource<List<Industry>>
}
