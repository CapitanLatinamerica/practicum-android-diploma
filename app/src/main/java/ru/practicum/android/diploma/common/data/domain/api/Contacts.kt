package ru.practicum.android.diploma.common.data.domain.api

data class Contacts(
    val email: String,
    val id: String,
    val name: String,
    val phones: List<Phone>
)
