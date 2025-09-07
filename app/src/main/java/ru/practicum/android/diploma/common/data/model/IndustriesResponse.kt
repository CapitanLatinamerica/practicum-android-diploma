package ru.practicum.android.diploma.common.data.model

import ru.practicum.android.diploma.common.data.domain.api.IndustryDto

data class IndustriesResponse(
    val industriesDto: List<IndustryDto>
) : NetResponse()
