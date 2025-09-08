package ru.practicum.android.diploma.vacancydetails.ui.model

import ru.practicum.android.diploma.common.domain.entity.Vacancy

class VacancyToVacancyDetailsUiMapper {

    fun mapToUi(vacancy: Vacancy): VacancyDetailsUi {
        return VacancyDetailsUi(
            id = vacancy.id,
            name = vacancy.name,
            salaryText = formatSalary(vacancy.salaryFrom, vacancy.salaryTo, vacancy.salaryCurrency),
            logoUrl = vacancy.logo,
            area = vacancy.area,
            employer = vacancy.employer,
            experience = vacancy.experience,
            employment = vacancy.employment,
            schedule = vacancy.schedule,
            description = vacancy.description
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
}
