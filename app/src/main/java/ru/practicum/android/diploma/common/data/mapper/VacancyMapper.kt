package ru.practicum.android.diploma.common.data.mapper

import ru.practicum.android.diploma.common.data.domain.api.VacancyDto
import ru.practicum.android.diploma.common.domain.entity.Vacancy

object VacancyMapper {
    fun map(vacancyDto: VacancyDto): Vacancy {
        return Vacancy(
            id = vacancyDto.id,
            name = vacancyDto.name,
            salaryCurrency = vacancyDto.salary.currency,
            salaryFrom = vacancyDto.salary.from,
            salaryTo = vacancyDto.salary.to,
            logo = vacancyDto.employer.logo,
            area = vacancyDto.area.name,
            employer = vacancyDto.employer.name,
            experience = vacancyDto.experience.name,
            employment = vacancyDto.employment.name,
            schedule = vacancyDto.schedule.name,
            description = vacancyDto.description
        )
    }
}
