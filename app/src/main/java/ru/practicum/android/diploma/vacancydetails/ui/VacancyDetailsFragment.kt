package ru.practicum.android.diploma.vacancydetails.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.ErrorType
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.ToolsText
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.databinding.FragmentVacancyDetailsBinding
import ru.practicum.android.diploma.vacancydetails.ui.model.VacancyDetailsUi
import ru.practicum.android.diploma.vacancydetails.ui.model.VacancyToVacancyDetailsUiMapper

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
            android.widget.Toast.makeText(
                requireContext(),
                "Vacancy ID is required",
                android.widget.Toast.LENGTH_LONG
            ).show()
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
                    is VacancyDetailsState.Error -> showError(state.errorType, state.message)
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
        fillData(vacancy, uiModel)

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

    private fun fillData(
        vacancy: Vacancy,
        uiModel: VacancyDetailsUi
    ) {
        binding.vacancyTitle.text = vacancy.name
        binding.experienceLine.text = vacancy.experience
        binding.scheduleTextView.text = vacancy.schedule
        binding.vacancySalary.text = uiModel.salaryText
        binding.vacancyDescriptionTextView.text = vacancy.description
        binding.companyName.text = vacancy.employer
        binding.companyCity.text = if (vacancy.address.isNullOrEmpty()) vacancy.area else vacancy.address
        binding.skillsTextView.text = vacancy.skills.toString()
    }

    // Показать состояние ошибки
    private fun showError(errorType: ErrorType, message: String) {
        val placeholderImage = when (errorType) {
            ErrorType.DENIED_VACANCY -> R.drawable.no_vacancy_placeholder
            else -> R.drawable.server_error_placeholder_vac_det
        }
        binding.progressBar.visibility = View.GONE
        binding.detailsScrollView.visibility = View.GONE
        binding.placeholdersBlock.visibility = View.VISIBLE
        binding.exatclyPlaceholder.setImageResource(placeholderImage)
        binding.placeholderText.text = message
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

            val spannable = ToolsText.formatDescriptionTextWithPaint(
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
            val widthLeft = binding.skillsTextView.paddingLeft
            val widthRight = binding.skillsTextView.paddingRight
            val availableWidth = binding.skillsTextView.width - widthLeft - widthRight

            if (!text.isNullOrBlank()) {
                binding.skillsTextView.text = ToolsText.formatSkillsTextWithPaint(
                    text,
                    binding.skillsTextView.paint,
                    availableWidth
                )
            } else {
                binding.skillsTextView.visibility = View.GONE
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

                    val formattedText = ToolsText.autoFormatTextWithPaint(
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
        ContactHandler(this).setupPhones(vacancy.contactPhones, binding.phoneTextView)

        // Настройка email
        ContactHandler(this).setupEmail(vacancy.contactEmail, binding.emailTextView)

        // Скрываем секцию контактов если нет данных
        if (vacancy.contactPhones.isNullOrEmpty() && vacancy.contactEmail.isNullOrBlank()) {
            binding.vacancyContacts.visibility = View.GONE
            binding.phoneTextView.visibility = View.GONE
//            binding.emailTextView.visibility = View.GONE
        } else {
            binding.vacancyContacts.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
