package ru.practicum.android.diploma.common.data.domain.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VacancyDto(
    @SerializedName("address") val addressDto: AddressDto?,
    @SerializedName("area") val areaDto: AreaDto?,
    @SerializedName("contacts") val contactsDto: ContactsDto?,
    @SerializedName("description") val description: String?,
    @SerializedName("employer") val employerDto: EmployerDto?,
    @SerializedName("employment") val employmentDto: EmploymentDto?,
    @SerializedName("experience") val experienceDto: ExperienceDto?,
    @SerializedName("id") val id: String?,
    @SerializedName("industry") val industryDto: IndustryDto?,
    @SerializedName("name") val name: String?,
    @SerializedName("salary") val salaryDto: SalaryDto?,
    @SerializedName("schedule") val scheduleDto: ScheduleDto?,
    @SerializedName("skills") val skills: List<String>?,
    @SerializedName("url") val url: String?
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
