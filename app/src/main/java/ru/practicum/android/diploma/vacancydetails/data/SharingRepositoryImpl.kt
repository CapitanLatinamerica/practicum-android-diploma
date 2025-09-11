package ru.practicum.android.diploma.vacancydetails.data

import android.content.Context
import android.content.Intent
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.vacancydetails.domain.SharingRepository

class SharingRepositoryImpl(private val context: Context) : SharingRepository {

    override fun shareVacancy(context: Context, vacancyUrl: String?) {
        val shareText = vacancyUrl ?: context.getString(R.string.share_vacancy_default)

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
}
