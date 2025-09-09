package ru.practicum.android.diploma.vacancydetails.ui.model

import ru.practicum.android.diploma.common.domain.entity.Vacancy

class VacancyToVacancyDetailsUiMapper {

    fun mapToUi(vacancy: Vacancy): VacancyDetailsUi {
        return VacancyDetailsUi(
            id = vacancy.id,
            name = vacancy.name,
            salaryText = buildSalaryText(vacancy),
            logoUrl = vacancy.logo,
            area = vacancy.area ?: "Регион не указан",
            employer = vacancy.employer ?: "Компания не указана",
            experience = vacancy.experience ?: "Опыт не указан",
            employment = vacancy.employment ?: "Тип занятости не указан",
            schedule = vacancy.schedule ?: "График не указан",
            description = vacancy.description ?: "Описание отсутствует"
        )
    }

    fun currencySymbol(currency: String?): String {
        val symbols = mapOf(
            "RUB" to "₽",
            "BYR" to "ꀷ",
            "USD" to "$",
            "EUR" to "€",
            "KZT" to "₸",
            "UAH" to "₴",
            "AZN" to "₼",
            "GEL" to "₾",
            "KGT" to "⃀",
        )
        return symbols[currency] ?: currency.orEmpty()
    }

    private fun buildSalaryText(vacancy: Vacancy): String {
        val currencySymbol = currencySymbol(vacancy.salaryCurrency)

        return when {
            vacancy.salaryFrom != null && vacancy.salaryTo != null -> {
                "от ${vacancy.salaryFrom} до ${vacancy.salaryTo} $currencySymbol"
            }
            vacancy.salaryFrom != null -> {
                "от ${vacancy.salaryFrom} $currencySymbol"
            }
            vacancy.salaryTo != null -> {
                "до ${vacancy.salaryTo} $currencySymbol"
            }
            else -> "Зарплата не указана"
        }.trim()
    }
}
