package ru.practicum.android.diploma.common.data.model

import ru.practicum.android.diploma.common.data.domain.api.Area

data class AreasResponse(
    val areaDto: ArrayList<Area>
) : NetResponse()
