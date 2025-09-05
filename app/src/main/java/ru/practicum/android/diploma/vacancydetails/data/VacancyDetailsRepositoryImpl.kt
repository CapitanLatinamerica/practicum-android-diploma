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
            employmentType = "Полная занятость",
            description = "Разработка мобильных приложений",
            responsibilities = listOf(
                "Разработка новых функций приложения",
                "Участие в код-ревью",
                "Оптимизация производительности"
            ),
            requirements = listOf(
                "Опыт работы с Kotlin от 3 лет",
                "Знание Android SDK",
                "Опыт работы с REST API"
            ),
            conditions = listOf(
                "Гибкий график работы",
                "Медицинская страховка",
                "Спортивный зал"
            ),
            skills = listOf("Kotlin", "Android SDK", "REST API", "Coroutines"),
            contactPerson = "Иван Иванов",
            contactEmail = "hr@yandex.ru",
            contactPhones = listOf("+7 (495) 123-45-67")
        )
    }

    override suspend fun addToFavorites(vacancy: VacancyDetails) {
        // Заглушка - потом реализуете
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        // Заглушка - потом реализуете
    }

    override suspend fun isVacancyFavorite(vacancyId: String): Boolean {
        // Заглушка - потом реализуете
        return false
    }
}
