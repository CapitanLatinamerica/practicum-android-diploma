package ru.practicum.android.diploma.vacancydetails.ui

import android.os.Bundle
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

        Glide.with(this)
            .load(uiModel.logoUrl)
            .placeholder(R.drawable.placeholder_vacancy)
            .into(binding.innerLogo)

        // Форматируем текстовые поля
        formatAllTextViews()

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

    // Метод для форматирования сплошного текста в соответствие макету
    private fun formatAllTextViews() {
        val widthLeft = binding.skillsTextView.paddingLeft
        val widthRight = binding.skillsTextView.paddingRight

        listOf(
            binding.vacancyDescriptionTextView,
            binding.skillsTextView
        ).forEach { textView ->
            textView.post {
                val text = textView.text?.toString()
                if (!text.isNullOrBlank()) {
                    textView.text = Tools.autoFormatTextWithPaint(
                        text,
                        textView.paint,
                        textView.width - widthLeft - widthRight
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
