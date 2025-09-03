package ru.practicum.android.diploma.common.data.model

import ru.practicum.android.diploma.common.data.domain.api.Industry

data class IndustriesResponse(
    val industriesDto: List<Industry>
) : NetResponse()
