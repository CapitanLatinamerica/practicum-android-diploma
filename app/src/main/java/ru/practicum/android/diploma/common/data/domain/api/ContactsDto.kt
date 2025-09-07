package ru.practicum.android.diploma.common.data.domain.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ContactsDto(
    @SerializedName("email") val email: String,
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phones") val phoneDtos: List<PhoneDto>
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
