package ru.practicum.android.diploma.vacancydetails.ui

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.entity.Phone

class ContactHandler(private val fragment: Fragment) {

    fun setupPhones(phones: List<Phone>?, phoneTextView: TextView) {
        if (phones.isNullOrEmpty()) {
            phoneTextView.visibility = View.GONE
            return
        }

        val phonesText = buildString {
            phones.forEachIndexed { index, phone ->
                if (index > 0) append("\n")
                append("📞 ${phone.number}")
                if (!phone.comment.isNullOrBlank()) {
                    append(" (${phone.comment})")
                }
            }
        }

        phoneTextView.text = phonesText
        phoneTextView.visibility = View.VISIBLE

        phoneTextView.setOnClickListener {
            if (phones.size == 1) {
                makePhoneCall(phones.first().number)
            } else {
                showPhoneSelectionDialog(phones)
            }
        }
    }

    fun setupEmail(email: String?, textView: TextView) {
        if (!email.isNullOrBlank()) {
            textView.visibility = View.VISIBLE
            textView.text = email
            textView.setOnClickListener {
                openEmailClient(email)
            }
            // Добавляем возможность клика
            textView.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.blue))
            textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        } else {
            textView.visibility = View.GONE
        }
    }

    private fun showPhoneSelectionDialog(phones: List<Phone>) {
        val items = phones.map { phone ->
            "${phone.number} ${phone.comment?.let { "($it)" } ?: ""}"
        }.toTypedArray()

        AlertDialog.Builder(fragment.requireContext())
            .setTitle(R.string.choose_phone_number)
            .setItems(items) { dialog, which ->
                makePhoneCall(phones[which].number)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openEmailClient(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri() // только email приложения
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, R.string.share_vacancy_title)
        }

        try {
            fragment.startActivity(Intent.createChooser(intent, "Выберите почтовое приложение"))
        } catch (e: ActivityNotFoundException) {
            Log.e("VacancyDetails", "No email app found", e)
            Toast.makeText(
                fragment.requireContext(),
                R.string.no_email_app,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
        }

        try {
            fragment.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("ContactHandler", "No dialer app found", e)
            Toast.makeText(
                fragment.requireContext(),
                fragment.getString(R.string.no_dialer_app),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
