package ru.practicum.android.diploma.common.data.domain.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VacanciesDto(
    @SerializedName("found") val found: Int,
    @SerializedName("items") val items: List<VacancyDto>,
    @SerializedName("page") val page: Int,
    @SerializedName("pages") val pages: Int
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
