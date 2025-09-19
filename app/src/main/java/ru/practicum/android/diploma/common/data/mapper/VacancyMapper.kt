package ru.practicum.android.diploma.common.data.mapper

import com.google.gson.Gson
import ru.practicum.android.diploma.common.data.db.entity.VacancyEntity
import ru.practicum.android.diploma.common.data.domain.api.VacancyDto
import ru.practicum.android.diploma.common.domain.entity.Phone
import ru.practicum.android.diploma.common.domain.entity.Vacancy

object VacancyMapper {
    private val gson = Gson()

    fun mapFromVacancyDtoToVacancy(vacancyDto: VacancyDto): Vacancy {
        return Vacancy(
            id = vacancyDto.id ?: "",
            name = vacancyDto.name ?: "",
            salaryCurrency = vacancyDto.salaryDto?.currency?.takeIf { it.isNotBlank() },
            salaryFrom = vacancyDto.salaryDto?.from?.takeIf { it != 0 },
            salaryTo = vacancyDto.salaryDto?.to?.takeIf { it != 0 },
            logo = vacancyDto.employerDto?.logo,
            area = vacancyDto.areaDto?.name,
            employer = vacancyDto.employerDto?.name,
            experience = vacancyDto.experienceDto?.name,
            employment = vacancyDto.employmentDto?.name,
            schedule = vacancyDto.scheduleDto?.name,
            description = vacancyDto.description,
            skills = vacancyDto.skills?.joinToString("\n") ?: "",
            contactEmail = vacancyDto.contactsDto?.email,
            contactPhones = vacancyDto.contactsDto?.phoneDtos?.map {
                Phone(it.formatted, it.comment)
            },
            contactPerson = vacancyDto.contactsDto?.name,
            vacancyUrl = vacancyDto.url,
            address = vacancyDto.addressDto?.raw?: ""
        )
    }

    fun mapFromVacancyToEntity(vacancy: Vacancy): VacancyEntity {
        return VacancyEntity(
            id = vacancy.id,
            name = vacancy.name,
            salaryCurrency = vacancy.salaryCurrency,
            salaryFrom = vacancy.salaryFrom,
            salaryTo = vacancy.salaryTo,
            logoPath = vacancy.logo,
            area = vacancy.area,
            employer = vacancy.employer,
            experience = vacancy.experience,
            employment = vacancy.employment,
            schedule = vacancy.schedule,
            description = vacancy.description,
            skills = vacancy.skills,
            contactEmail = vacancy.contactEmail,
            contactPhonesJson = vacancy.contactPhones?.let { gson.toJson(it) },
            contactPerson = vacancy.contactPerson,
            vacancyUrl = vacancy.vacancyUrl,
            address = vacancy.address
        )
    }

    fun mapFromEntityToVacancy(vacancyEntity: VacancyEntity): Vacancy {
        return Vacancy(
            id = vacancyEntity.id,
            name = vacancyEntity.name,
            salaryCurrency = vacancyEntity.salaryCurrency,
            salaryFrom = vacancyEntity.salaryFrom,
            salaryTo = vacancyEntity.salaryTo,
            logo = vacancyEntity.logoPath,
            area = vacancyEntity.area,
            employer = vacancyEntity.employer,
            experience = vacancyEntity.experience,
            employment = vacancyEntity.employment,
            schedule = vacancyEntity.schedule,
            description = vacancyEntity.description,
            skills = vacancyEntity.skills,
            contactEmail = vacancyEntity.contactEmail,
            contactPhones = vacancyEntity.contactPhonesJson?.let {
                gson.fromJson(it, Array<Phone>::class.java).toList()
            },
            contactPerson = vacancyEntity.contactPerson,
            vacancyUrl = vacancyEntity.vacancyUrl,
            address = vacancyEntity.address
        )
    }
}
