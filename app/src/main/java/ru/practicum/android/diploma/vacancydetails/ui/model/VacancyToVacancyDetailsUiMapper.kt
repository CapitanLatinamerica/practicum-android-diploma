package ru.practicum.android.diploma.vacancydetails.ui.model

import ru.practicum.android.diploma.common.domain.entity.Vacancy

class VacancyToVacancyDetailsUiMapper {

    fun mapToUi(vacancy: Vacancy): VacancyDetailsUi {
        return VacancyDetailsUi(
            id = vacancy.id,
            name = vacancy.name ?: "Название не указано",
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

    private fun formatSalary(from: Int?, to: Int?, currency: String?): String {
        fun format(salary: Int): String {
            val symbols = java.text.DecimalFormatSymbols.getInstance(java.util.Locale.getDefault()).apply {
                groupingSeparator = '\u00A0'
                decimalSeparator = ','
            }
            val decimalFormat = java.text.DecimalFormat("#,###", symbols)
            return decimalFormat.format(salary)
        }

        val symbol = currencySymbol(currency)
        return when {
            from == null && to == null -> "Зарплата не указана"
            from != null && to != null && from == to -> "${format(from)} $symbol"
            from != null && to != null -> "от ${format(from)} до ${format(to)} $symbol"
            from != null -> "от ${format(from)} $symbol"
            to != null -> "${format(to)} $symbol"
            else -> "Зарплата не указана"
        }
    }

    private fun currencySymbol(currency: String?): String {
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
        return when {
            vacancy.salaryFrom != null && vacancy.salaryTo != null -> {
                "от ${vacancy.salaryFrom} до ${vacancy.salaryTo} ${vacancy.salaryCurrency ?: ""}"
            }
            vacancy.salaryFrom != null -> {
                "от ${vacancy.salaryFrom} ${vacancy.salaryCurrency ?: ""}"
            }
            vacancy.salaryTo != null -> {
                "до ${vacancy.salaryTo} ${vacancy.salaryCurrency ?: ""}"
            }
            else -> "Зарплата не указана"
        }.trim()
    }
}
