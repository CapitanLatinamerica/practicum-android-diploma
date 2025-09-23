package ru.practicum.android.diploma.common.data.mapper

import ru.practicum.android.diploma.common.data.domain.api.AreaDto
import ru.practicum.android.diploma.common.domain.entity.Area

object AreaMapper {

    fun mapAreaDtoToArea(areaDto: AreaDto): Area {
        return Area(
            id = areaDto.id.toInt(),
            name = areaDto.name,
            parentId = areaDto.parentId?.toInt(),
            areas = areaDto.areas?.map { item -> mapAreaDtoToArea(item) } ?: emptyList(),
        )
    }
}
