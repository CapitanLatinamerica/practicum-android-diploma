package ru.practicum.android.diploma.vacancydetails.ui

import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.widget.TextView
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

    fun setupEmail(email: String?, emailTextView: TextView) {
        if (email.isNullOrBlank()) {
            emailTextView.visibility = View.GONE
            return
        }

        emailTextView.text = buildString {
            append(fragment.getString(R.string.email_icon))
            append("   ")
            append(email)
        }
        emailTextView.visibility = View.VISIBLE

        emailTextView.setOnClickListener {
            openEmailClient(email)
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
            data = "mailto:$email".toUri()
        }
        fragment.startActivity(Intent.createChooser(intent, fragment.getString(R.string.choose_email_app)))
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:${phoneNumber.filter { it.isDigit() }}".toUri()
        }
        fragment.startActivity(intent)
    }
}
