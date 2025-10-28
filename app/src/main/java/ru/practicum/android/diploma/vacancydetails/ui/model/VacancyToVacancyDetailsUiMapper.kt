package ru.practicum.android.diploma.vacancydetails.ui.model

import ru.practicum.android.diploma.common.domain.entity.Vacancy
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

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

    private fun buildSalaryText(vacancy: Vacancy): String {
        return formatSalary(vacancy.salaryFrom, vacancy.salaryTo, vacancy.salaryCurrency)
    }

    private fun formatSalary(from: Int?, to: Int?, currency: String?): String {
        // Вспомогательная функция, которая форматирует целое число с разделением тысяч
        fun format(salary: Int): String {
            val symbols = DecimalFormatSymbols(Locale.getDefault())
            symbols.groupingSeparator = '\u00A0'
            symbols.decimalSeparator = ','

            val decimalFormat = DecimalFormat("#,###", symbols)
            return decimalFormat.format(salary)
        }

        return when {
            from == null && to == null -> "Зарплата не указана"
            from != null && to != null && from == to -> "${format(from)} ${symbolFor(currency)}"
            from != null && to != null -> "от ${format(from)} до ${format(to)} ${symbolFor(currency)}"
            from != null -> "от ${format(from)} ${symbolFor(currency)}"
            to != null -> "до ${format(to)} ${symbolFor(currency)}"
            else -> "Зарплата не указана"
        }
    }

    private val currencySymbols = mapOf(
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

    // Вспомогательная функция, которая форматирует валюту
    private fun symbolFor(currency: String?): String {
        return currencySymbols[currency] ?: currency.orEmpty()
    }
}
