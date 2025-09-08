package ru.practicum.android.diploma.vacancydetails.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.Tools
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.databinding.FragmentVacancyDetailsBinding
import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetailsState
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
            showError("Vacancy ID is required")
            return
        }

        setupObservers()
        setupToolbar()
    }

    // Метод для форматирования сплошного текста в соответствие макету
    private fun formatAllTextViews() {
        val widthLeft = binding.responsibilitiesTextView.paddingLeft
        val widthRight = binding.responsibilitiesTextView.paddingRight

        binding.responsibilitiesTextView.post {
            binding.responsibilitiesTextView.text = Tools.autoFormatTextWithPaint(
                binding.responsibilitiesTextView.text.toString(),
                binding.responsibilitiesTextView.paint,
                binding.responsibilitiesTextView.width - widthLeft - widthRight
            )
        }
        binding.requirementsTextView.post {
            binding.requirementsTextView.text = Tools.autoFormatTextWithPaint(
                binding.requirementsTextView.text.toString(),
                binding.requirementsTextView.paint,
                binding.requirementsTextView.width - widthLeft - widthRight
            )
        }
        binding.conditionsTextView.post {
            binding.conditionsTextView.text = Tools.autoFormatTextWithPaint(
                binding.conditionsTextView.text.toString(),
                binding.conditionsTextView.paint,
                binding.conditionsTextView.width - widthLeft - widthRight
            )
        }
        binding.skillsTextView.post {
            binding.skillsTextView.text = Tools.autoFormatTextWithPaint(
                binding.skillsTextView.text.toString(),
                binding.skillsTextView.paint,
                binding.skillsTextView.width - widthLeft - widthRight
            )
        }
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

        Log.d(TAG, "Vacancy details received: $vacancy")

        val uiModel = detailsUiMapper.mapToUi(vacancy)

        Log.d(TAG, "Mapped UI Model: $uiModel")

        binding.detailsScrollView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        // Заполняем данные
        binding.vacancyTitle.text = vacancy.name
        binding.vacancySalary.text = uiModel.salaryText
        binding.companyName.text = uiModel.employer
        binding.companyCity.text = uiModel.area
        binding.experienceLine.text = vacancy.experience
        binding.responsibilitiesTextView.text = vacancy.description

        // Формируем текст со всеми основными данными
        val allText = buildString {
            append("ID: ${vacancy.id}\n")
            append("Название вакансии: ${vacancy.name}\n")
            append("Currency: ${vacancy.salaryCurrency.toString()}\n")
            append("Зарплата от: ${vacancy.salaryFrom.toString()}\n")
            append("Зарплата до: ${vacancy.salaryTo.toString()}\n")
            append("Лого: ${vacancy.logo.toString()}\n")
            append("Area: ${vacancy.area}\n")
            append("Компания: ${vacancy.employer}\n")
            append("Опыт: ${vacancy.experience}\n")
            append("Employment: ${vacancy.employment}\n")
            append("Расписание: ${vacancy.schedule}\n")
            append("Описание: ${vacancy.description}\n")
        }
        Glide.with(this)
            .load(uiModel.logoUrl)
            .placeholder(R.drawable.placeholder_vacancy)
            .into(binding.innerLogo)

        binding.skillsTextView.text = allText


        // Форматируем текстовые поля
//        formatAllTextViews()
    }

    // Показать состояние ошибки
    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.detailsScrollView.visibility = View.GONE
        binding.placeholdersBlock.visibility = View.VISIBLE

        // Покажем тост с сообщением об ошибке
        android.widget.Toast.makeText(
            requireContext(),
            message,
            android.widget.Toast.LENGTH_LONG
        ).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "VacancyDetailsFragment"
    }

}
