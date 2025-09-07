package ru.practicum.android.diploma.common.data.domain.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ScheduleDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
