package ru.practicum.android.diploma.vacancydetails.data

import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetails
import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetailsRepository

class VacancyDetailsRepositoryImpl : VacancyDetailsRepository {

    override suspend fun getVacancyDetails(vacancyId: String): VacancyDetails {
        // Временная заглушка - возвращаем тестовые данные
        return VacancyDetails(
            id = vacancyId,
            title = "Senior Android Developer",
            salary = "от 200 000 ₽",
            companyName = "Яндекс",
            companyLogo = null,
            companyCity = "Москва",
            experience = "От 3 лет",
            employmentType = "Полнейшая занятость",
            description = "Разработка мобильных приложений",
            responsibilities = "Разработка новых функций приложения\nУчастие в код-ревью\nОптимизация " +
                "производительности",
            requirements = "Опыт работы с Kotlin от 3 лет\nЗнание Android SDK\nОпыт работы с REST API",
            conditions = "Гибкий график работы\nМедицинская страховка\nСпортивный зал",
            skills = "Kotlin\nAndroid SDK\nREST API\nCoroutines"
        )
    }
}
