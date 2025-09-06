package ru.practicum.android.diploma.common.data.domain.api

data class AreaDto(
    val areas: List<AreaDto>?,
    val id: String,
    val name: String,
    val parentId: String?
)
