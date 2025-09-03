package ru.practicum.android.diploma.common.domain.entity

import com.google.gson.annotations.SerializedName

data class Area(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("parentId") val parentId: Int,
    @SerializedName("areas") val areas: ArrayList<Area>
)
