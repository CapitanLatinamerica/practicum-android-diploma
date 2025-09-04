package ru.practicum.android.diploma.common.data.domain.api

data class AreaDtoItem(
    val areas: List<Area>,
    val id: String,
    val name: String,
    val parentId: AreaDtoItem?
)
