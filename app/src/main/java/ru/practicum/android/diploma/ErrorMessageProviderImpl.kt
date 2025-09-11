package ru.practicum.android.diploma

import android.content.Context

class ErrorMessageProviderImpl(private val context: Context) : ErrorMessageProvider {
    override fun serverError(): String {
        return context.getString(R.string.server_error)
    }

    override fun nothingFound(): String {
        return context.getString(R.string.nothing_found)
    }

    override fun noInternet(): String {
        return context.getString(R.string.no_internet)
    }
}
