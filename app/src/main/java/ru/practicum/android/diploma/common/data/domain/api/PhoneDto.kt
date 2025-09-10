package ru.practicum.android.diploma.common.data.domain.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PhoneDto(
    @SerializedName("comment") val comment: String?,
    @SerializedName("formatted") val formatted: String
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
