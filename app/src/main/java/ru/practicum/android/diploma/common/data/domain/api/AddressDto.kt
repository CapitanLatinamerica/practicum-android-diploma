package ru.practicum.android.diploma.common.data.domain.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddressDto(
    @SerializedName("building") val building: String,
    @SerializedName("city") val city: String,
    @SerializedName("id") val id: String,
    @SerializedName("raw") val raw: String,
    @SerializedName("street") val street: String
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
