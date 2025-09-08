package ru.practicum.android.diploma.vacancydetails.domain


import com.google.gson.annotations.SerializedName

data class VacancyDetails(
    val id: String,
    val name: String,
    val salary: SalaryDetails?,
    val address: AddressDetails?,
    val experience: ExperienceDetails?,
    val schedule: ScheduleDetails?,
    val employment: EmploymentDetails?,
    val contacts: ContactsDetails?,
    val description: String?,
    val employer: EmployerDetails?,
    val area: AreaDetails?,
    val skills: List<String>?,
    val url: String?,
    val industry: IndustryDetails?
)

data class SalaryDetails(
    val id: String?,
    val currency: String?,
    val from: Int?,
    val to: Int?
)

data class AddressDetails(
    val id: String?,
    val city: String?,
    val street: String?,
    val building: String?,
    val raw: String?
)

data class ExperienceDetails(
    val id: String?,
    val name: String?
)

data class ScheduleDetails(
    val id: String?,
    val name: String?
)

data class EmploymentDetails(
    val id: String?,
    val name: String?
)

data class ContactsDetails(
    val id: String?,
    val name: String?,
    val email: String?,
    val phones: List<PhoneDetails>?
)

data class PhoneDetails(
    val comment: String?,
    val formatted: String?
)

data class EmployerDetails(
    val id: String?,
    val name: String?,
    val logo: String?
)

data class AreaDetails(
    val id: String?,
    val parentId: String?,
    val name: String?,
    val areas: List<AreaDetails>?
)

data class IndustryDetails(
    val id: String?,
    val name: String?
)
