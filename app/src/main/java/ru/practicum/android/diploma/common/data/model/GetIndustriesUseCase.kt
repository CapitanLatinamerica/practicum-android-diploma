package ru.practicum.android.diploma.common.data.model

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Industry

interface GetIndustriesUseCase {
    suspend fun execute(): Resource<List<Industry>>
}
