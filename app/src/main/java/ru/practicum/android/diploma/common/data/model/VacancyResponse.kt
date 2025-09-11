package ru.practicum.android.diploma.common.data.model

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.common.data.domain.api.AddressDto
import ru.practicum.android.diploma.common.data.domain.api.AreaDto
import ru.practicum.android.diploma.common.data.domain.api.ContactsDto
import ru.practicum.android.diploma.common.data.domain.api.EmployerDto
import ru.practicum.android.diploma.common.data.domain.api.EmploymentDto
import ru.practicum.android.diploma.common.data.domain.api.ExperienceDto
import ru.practicum.android.diploma.common.data.domain.api.IndustryDto
import ru.practicum.android.diploma.common.data.domain.api.SalaryDto
import ru.practicum.android.diploma.common.data.domain.api.ScheduleDto

data class VacancyResponse(
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
) : NetResponse()
