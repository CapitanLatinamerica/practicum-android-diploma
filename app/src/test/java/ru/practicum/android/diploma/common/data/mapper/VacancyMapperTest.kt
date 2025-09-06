package ru.practicum.android.diploma.common.data.mapper

import org.junit.Assert.*
import org.junit.Test
import ru.practicum.android.diploma.common.data.domain.api.Address
import ru.practicum.android.diploma.common.data.domain.api.Area
import ru.practicum.android.diploma.common.data.domain.api.Contacts
import ru.practicum.android.diploma.common.data.domain.api.Employer
import ru.practicum.android.diploma.common.data.domain.api.Employment
import ru.practicum.android.diploma.common.data.domain.api.Experience
import ru.practicum.android.diploma.common.data.domain.api.IndustryDto
import ru.practicum.android.diploma.common.data.domain.api.Phone
import ru.practicum.android.diploma.common.data.domain.api.Salary
import ru.practicum.android.diploma.common.data.domain.api.Schedule
import ru.practicum.android.diploma.common.data.domain.api.VacancyDto

class VacancyMapperTest {
    @Test
    fun should_map_VacancyDto_to_Vacancy() {

        val vacancyDto = VacancyDto(
            address = Address(
                building = "7",
                city = "Челябинск",
                id = "6",
                raw = "Челябинск, Труда, 7",
                street = "Труда"
            ),
            area = Area(
                areas = listOf(
                    Area(
                        areas = listOf<Area>(),
                        id = "28",
                        name = "Грузия",
                        parentId = null
                    )
                ),
                id = "28",
                name = "Узян",
                parentId = null
            ),
            contacts = Contacts(
                email = "123@gmail.com",
                id = "0",
                name = "Смирнов Алексей Иванович",
                phones = listOf(
                    Phone(
                        comment = null,
                        formatted = "+7 (999) 456-78-90"
                    ), Phone(
                        comment = null,
                        formatted = "+7 (999) 654-32-10"
                    )
                )
            ),
            description = "Описание",
            employer = Employer(
                id = "1",
                logo = "https://picsum.photos/200/300",
                name = "Google"
            ),
            employment = Employment(
                id = "full",
                name = "Полная занятость"
            ),
            experience = Experience(
                id = "noExperience",
                name = "Нет опыта"
            ),
            id = "b4cb93e5-1266-45b1-a1dd-43d193bd0631",
            industryDto = IndustryDto(
                id = "49",
                name = "Услуги для населения"
            ),
            name = "DevOps Engineer в Google",
            salary = Salary(
                currency = "AUD",
                from = 1300,
                id = "8",
                to = 2300
            ),
            schedule = Schedule(
                id = "fullDay",
                name = "Полный день"
            ),
            skills = listOf("Kotlin", "JavaScript", "Swift", "HTML", "Python"),
            url = "https://picsum.photos/200/300"
        )


        val actualResult = VacancyMapper.mapFromDtoToVacancy(
            vacancyDto
        )

        assertEquals("b4cb93e5-1266-45b1-a1dd-43d193bd0631", actualResult.id)
        assertEquals("DevOps Engineer в Google", actualResult.name)
        assertEquals("AUD", actualResult.salaryCurrency)
        assertEquals(1300, actualResult.salaryFrom)
        assertEquals(2300, actualResult.salaryTo)
        assertEquals("https://picsum.photos/200/300", actualResult.logo)
        assertEquals("Узян", actualResult.area)
        assertEquals("Google", actualResult.employer)
        assertEquals("Нет опыта", actualResult.experience)
        assertEquals("Полная занятость", actualResult.employment)
        assertEquals("Полный день", actualResult.schedule)
        assertEquals("Описание", actualResult.description)

    }

}
