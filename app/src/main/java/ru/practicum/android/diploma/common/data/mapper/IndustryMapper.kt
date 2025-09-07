package ru.practicum.android.diploma.common.data.mapper

import ru.practicum.android.diploma.common.data.domain.api.IndustryDto
import ru.practicum.android.diploma.common.domain.entity.Industry

object IndustryMapper {
    fun map(industry: IndustryDto): Industry {
        return Industry(
            id = industry.id.toInt(),
            name = industry.name
        )
    }
}
