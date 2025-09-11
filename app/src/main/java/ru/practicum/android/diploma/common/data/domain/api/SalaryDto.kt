package ru.practicum.android.diploma.common.data.domain.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SalaryDto(
    @SerializedName("currency") val currency: String,
    @SerializedName("from") val from: Int,
    @SerializedName("id") val id: String,
    @SerializedName("to") val to: Int
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
