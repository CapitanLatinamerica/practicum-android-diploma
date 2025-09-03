package ru.practicum.android.diploma.common.domain.entity

import com.google.gson.annotations.SerializedName

data class Industry(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
