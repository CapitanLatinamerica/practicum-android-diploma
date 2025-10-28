package ru.practicum.android.diploma.vacancydetails.data

import android.content.Context
import android.content.Intent
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.vacancydetails.domain.SharingRepository

class SharingRepositoryImpl : SharingRepository {

    override fun shareVacancy(context: Context, vacancyName: String?, vacancyUrl: String?) {
        val shareText = buildShareText(context, vacancyName, vacancyUrl)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_vacancy_title))
        }

        context.startActivity(
            Intent.createChooser(
                shareIntent,
                context.getString(R.string.share_vacancy)
            )
        )
    }

    private fun buildShareText(context: Context, vacancyName: String?, vacancyUrl: String?): String {
        return if (vacancyName != null && vacancyUrl != null) {
            // Если есть и название и URL
            context.getString(R.string.share_vacancy_with_link, vacancyName, vacancyUrl)
        } else if (vacancyName != null) {
            // Если есть только название
            context.getString(R.string.share_vacancy_without_link, vacancyName)
        } else {
            // Если нет данных
            context.getString(R.string.share_vacancy_default)
        }
    }
}
