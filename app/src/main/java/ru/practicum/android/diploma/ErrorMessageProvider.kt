package ru.practicum.android.diploma

interface ErrorMessageProvider {
    fun serverError(): String
    fun nothingFound(): String
    fun noInternet(): String
}
