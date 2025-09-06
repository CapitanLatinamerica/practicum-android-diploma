package ru.practicum.android.diploma.common.data.model

import ru.practicum.android.diploma.common.data.domain.api.AreaDto

data class AreasResponse(
    val areaDto: List<AreaDto>
) : NetResponse()
