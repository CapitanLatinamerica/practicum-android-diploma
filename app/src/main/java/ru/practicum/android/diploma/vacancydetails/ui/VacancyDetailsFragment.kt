package ru.practicum.android.diploma.vacancydetails.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.Tools.formatTextWithBullets
import ru.practicum.android.diploma.databinding.FragmentVacancyDetailsBinding

class VacancyDetailsFragment : Fragment(R.layout.fragment_vacancy_details) {

    private var _binding: FragmentVacancyDetailsBinding? = null
    private val binding get() = _binding!!
    private var isLiked = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVacancyDetailsBinding.bind(view)

        // Форматируем поля
        formatAllTextViews()

        // Обработка нажатия на кнопки меню в тулбаре
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.like_btn -> {
                    isLiked = !isLiked
                    updateLikeIcon(menuItem)
                    true
                }

                else -> false
            }
        }
    }

    private fun updateLikeIcon(menuItem: MenuItem) {
        menuItem.icon = ContextCompat.getDrawable(
            requireContext(),
            if (isLiked) R.drawable.ic_liked else R.drawable.ic_unliked
        )
    }

    // Метод для форматирования сплошного текста в соответствие макету
    private fun formatAllTextViews() {
        binding.responsibilitiesTextView.formatTextWithBullets(R.string.responsibilities_text)
        binding.requirementsTextView.formatTextWithBullets(R.string.requirements_text)
        binding.conditionsTextView.formatTextWithBullets(R.string.conditions_text)
        binding.skillsTextView.formatTextWithBullets(R.string.skills_text)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
