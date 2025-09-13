package ru.practicum.android.diploma.filtersettings.data

import android.content.SharedPreferences
import com.google.gson.Gson

class FilterStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {
    companion object {
        const val FILTER_PARAMETERS_NAME = "filter_param_prefs"
        private const val FILTER_PARAMETERS_KEY = "filter_parameters"
    }

    fun save(params: FilterParameters) {
        val json = gson.toJson(params)
        sharedPreferences.edit()
            .putString(FILTER_PARAMETERS_KEY, json)
            .apply()
    }

    fun load(): FilterParameters? {
        val json = sharedPreferences.getString(FILTER_PARAMETERS_KEY, null) ?: return null
        return kotlin.runCatching { gson.fromJson(json, FilterParameters::class.java) }.getOrNull()
    }

    fun clear() {
        sharedPreferences.edit()
            .remove(FILTER_PARAMETERS_KEY)
            .apply()
    }
}
