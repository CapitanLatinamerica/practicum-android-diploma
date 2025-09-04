package ru.practicum.android.diploma.common.domain.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Vacancy(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("salaryCurrency") val salaryCurrency: String,
    @SerializedName("salaryFrom") val salaryFrom: Int,
    @SerializedName("salaryTo") val salaryTo: Int,
    @SerializedName("logo") val logo: String,
    @SerializedName("area") val area: String,
    @SerializedName("employer") val employer: String,
    @SerializedName("experience") val experience: String,
    @SerializedName("employment") val employment: String,
    @SerializedName("schedule") val schedule: String,
    @SerializedName("description") val description: String
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
