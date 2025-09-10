package ru.practicum.android.diploma.vacancydetails.ui

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.Tools
import ru.practicum.android.diploma.common.domain.entity.Phone
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.databinding.FragmentVacancyDetailsBinding
import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetailsState
import ru.practicum.android.diploma.vacancydetails.ui.model.VacancyToVacancyDetailsUiMapper
import androidx.core.net.toUri

class VacancyDetailsFragment : Fragment(R.layout.fragment_vacancy_details) {

    private val detailsUiMapper = VacancyToVacancyDetailsUiMapper()
    private var _binding: FragmentVacancyDetailsBinding? = null
    private val binding get() = _binding!!

    // Внедрение зависимости репозитория через Koin
    private val viewModel: VacancyDetailsViewModel by viewModel { parametersOf(vacancyId) }
    private var vacancyId: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVacancyDetailsBinding.bind(view)

        // Получение ID вакансии из аргументов навигации
        vacancyId = arguments?.getString("vacancyId") ?: run {
            showError("Vacancy ID is required")
            return
        }

        setupObservers()
        setupToolbar()
    }

    // Настройка observers для наблюдения за состоянием ViewModel
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.vacancyState.collect { state ->
                when (state) {
                    is VacancyDetailsState.Loading -> showLoading()
                    is VacancyDetailsState.Content -> showVacancyDetails(state.vacancy)
                    is VacancyDetailsState.Error -> showError(state.message)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLiked.collect { isLiked ->
                updateLikeIconInToolbar(isLiked)
            }
        }
    }

    // Настройка тулбара - обработка кликов по меню и кнопке "Назад"
    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.like_btn -> {
                    vacancyId.let { id ->
                        val currentState = viewModel.vacancyState.value
                        val vacancyDetails = (currentState as? VacancyDetailsState.Content)?.vacancy
                        viewModel.toggleFavorite(id, vacancyDetails)
                    }
                    true
                }

                else -> false
            }
        }

        // Обработка клика по кнопке "Назад" в тулбаре
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    // Показать состояние загрузки
    private fun showLoading() {
        // Показать индикатор загрузки
        binding.detailsScrollView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    // Отображение детальной информации о вакансии
    private fun showVacancyDetails(vacancy: Vacancy) {
        val uiModel = detailsUiMapper.mapToUi(vacancy)

        binding.detailsScrollView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        // Заполняем данные
        binding.vacancyTitle.text = vacancy.name
        binding.experienceLine.text = vacancy.experience
        binding.scheduleTextView.text = vacancy.schedule
        binding.vacancySalary.text = uiModel.salaryText
        binding.vacancyDescriptionTextView.text = vacancy.description
        binding.companyName.text = vacancy.employer
        binding.companyCity.text = vacancy.area
        binding.experienceLine.text = vacancy.experience
        binding.skillsTextView.text = vacancy.skills.toString()

        Glide.with(this)
            .load(vacancy.logo)
            .fitCenter() // Масштабирование с сохранением пропорций
            .placeholder(R.drawable.placeholder_vacancy)
            .into(binding.innerLogo)

        // Форматируем описание с стилями и переносами
        formatVacancyDescription(vacancy.description ?: "")

        // Обычное форматирование для skills
        formatSkillsTextView()

        formatContactsTextViews()

        // Отображаем контакты
        setupContacts(vacancy)

        // Обработчик кнопки поделиться
        view?.findViewById<TextView>(R.id.share_btn)?.setOnClickListener {
            viewModel.shareVacancy(requireContext())
        }
    }

    // Показать состояние ошибки
    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.detailsScrollView.visibility = View.GONE
        binding.placeholdersBlock.visibility = View.VISIBLE
    }

    // Обновление иконки "лайка" в тулбаре в зависимости от статуса избранного
    private fun updateLikeIconInToolbar(isLiked: Boolean) {
        binding.toolbar.menu?.findItem(R.id.like_btn)?.let { menuItem ->
            menuItem.icon = ContextCompat.getDrawable(
                requireContext(),
                if (isLiked) R.drawable.ic_liked else R.drawable.ic_unliked
            )
        }
    }

    private fun formatVacancyDescription(description: String) {
        binding.vacancyDescriptionTextView.post {
            val widthLeft = binding.vacancyDescriptionTextView.paddingLeft
            val widthRight = binding.vacancyDescriptionTextView.paddingRight
            val availableWidth = binding.vacancyDescriptionTextView.width - widthLeft - widthRight

            val spannable = Tools.formatDescriptionTextWithPaint(
                context = requireContext(), // Передаем контекст
                text = description,
                paint = binding.vacancyDescriptionTextView.paint,
                availableWidth = availableWidth
            )

            binding.vacancyDescriptionTextView.setText(spannable, TextView.BufferType.SPANNABLE)
        }
    }

    private fun formatSkillsTextView() {
        binding.skillsTextView.post {
            val text = binding.skillsTextView.text?.toString()
            val phones = binding.phoneTextView.text?.toString()
            val email = binding.emailTextView.text?.toString()
            val widthLeft = binding.skillsTextView.paddingLeft
            val widthRight = binding.skillsTextView.paddingRight
            val availableWidth = binding.skillsTextView.width - widthLeft - widthRight

            if (!text.isNullOrBlank()) {
                binding.skillsTextView.text = Tools.formatSkillsTextWithPaint(
                    text,
                    binding.skillsTextView.paint,
                    availableWidth
                )
            }

            if (!phones.isNullOrBlank()) {
                binding.phoneTextView.text = Tools.formatSkillsTextWithPaint(
                    phones,
                    binding.phoneTextView.paint,
                    availableWidth
                )
            }

            if (!email.isNullOrBlank()) {
                binding.emailTextView.text = Tools.formatSkillsTextWithPaint(
                    email,
                    binding.emailTextView.paint,
                    availableWidth
                )
            }
        }
    }

    private fun formatContactsTextViews() {
        listOf(binding.phoneTextView, binding.emailTextView).forEach { textView ->
            textView.post {
                val text = textView.text?.toString()
                if (!text.isNullOrBlank()) {
                    val widthLeft = textView.paddingLeft
                    val widthRight = textView.paddingRight
                    val availableWidth = textView.width - widthLeft - widthRight

                    val formattedText = Tools.autoFormatTextWithPaint(
                        text,
                        textView.paint,
                        availableWidth,
                        prefix = "" // Без префиксов для контактов
                    )
                    textView.text = formattedText
                }
            }
        }
    }

    private fun setupContacts(vacancy: Vacancy) {
        // Настройка телефонов
        setupPhones(vacancy.contactPhones)

        // Настройка email
        setupEmail(vacancy.contactEmail)

        // Скрываем секцию контактов если нет данных
        if (vacancy.contactPhones.isNullOrEmpty() && vacancy.contactEmail.isNullOrBlank()) {
            binding.vacancyContacts.visibility = View.GONE
            binding.phoneTextView.visibility = View.GONE
            binding.emailTextView.visibility = View.GONE
        } else {
            binding.vacancyContacts.visibility = View.VISIBLE
        }
    }

    private fun setupPhones(phones: List<Phone>?) {
        if (phones.isNullOrEmpty()) {
            binding.phoneTextView.visibility = View.GONE
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

        binding.phoneTextView.text = phonesText
        binding.phoneTextView.visibility = View.VISIBLE

        // Делаем кликабельным
        binding.phoneTextView.setOnClickListener {
            if (phones.size == 1) {
                // Если один номер - сразу звоним
                makePhoneCall(phones.first().number)
            } else {
                // Если несколько номеров - показываем диалог выбора
                showPhoneSelectionDialog(phones)
            }
        }
    }

    private fun setupEmail(email: String?) {
        if (email.isNullOrBlank()) {
            binding.emailTextView.visibility = View.GONE
            return
        }

        binding.emailTextView.text = buildString {
        append(getString(R.string.email_icon))
        append("   ")
        append(email)
    }
        binding.emailTextView.visibility = View.VISIBLE

        // Делаем кликабельным
        binding.emailTextView.setOnClickListener {
            openEmailClient(email)
        }
    }

    private fun showPhoneSelectionDialog(phones: List<Phone>) {
        val items = phones.map { phone ->
            "${phone.number} ${phone.comment?.let { "($it)" }}"
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
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
        startActivity(Intent.createChooser(intent, getString(R.string.choose_email_app)))
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:${phoneNumber.filter { it.isDigit() }}".toUri()
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
