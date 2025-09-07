package ru.practicum.android.diploma.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.Tools.debounce
import ru.practicum.android.diploma.databinding.FragmentMainBinding
import ru.practicum.android.diploma.search.ui.model.VacancyUi

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding!!

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

    private val viewModel: SearchViewModel by viewModel()

    private var adapter: VacanciesAdapter? = null

    private lateinit var onVacancyClickDebounce: (VacancyUi) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onVacancyClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { //Тут будет логика перехода на другой экран с помощью SafeArgs
        }

        adapter = VacanciesAdapter { vacancy ->
            onVacancyClickDebounce(vacancy)
        }

        binding.recyclerViewMain.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMain.adapter = adapter

        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s?.toString().orEmpty().trim()
                updateEditActionIcon(text.isNotEmpty())
                if (text.isBlank()) {
                    adapter?.updateData(emptyList())
                    binding.countVacancies.visibility = View.GONE
                    viewModel.clearSearch()
                } else {
                    viewModel.searchDebounce(text)
                }
                updatePlaceholderForInput()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.editText.setOnFocusChangeListener { _, _ ->
            updatePlaceholderForInput()
        }

        binding.btnEditAction.setOnClickListener {
            val text = binding.editText.text?.toString().orEmpty()
            if (text.isNotEmpty()) {
                binding.editText.setText("")
                viewModel.clearSearch()
                updateEditActionIcon(false)
            } else {
                binding.editText.requestFocus()
                showKeyboard(binding.editText)
            }
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.Initial -> showInitial()
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.found, state.vacancies)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.Error -> showError(state.errorMessage)
        }
    }

    private fun showInitial() {
        with(binding) {
            progressbar.visibility = View.GONE
            recyclerViewMain.visibility = View.GONE
            updatePlaceholderForInput()
            placeholderText.visibility = View.GONE
            countVacancies.visibility = View.GONE
        }
    }

    private fun showLoading() {
        with(binding) {
            progressbar.visibility = View.VISIBLE
            recyclerViewMain.visibility = View.GONE
            placeholderMainScreen.visibility = View.GONE
            placeholderText.visibility = View.GONE
            countVacancies.visibility = View.GONE
        }
    }

    private fun showContent(found: Int, vacancies: List<VacancyUi>) {
        with(binding) {
            progressbar.visibility = View.GONE
            placeholderMainScreen.visibility = View.GONE
            placeholderText.visibility = View.GONE
            recyclerViewMain.visibility = View.VISIBLE
            countVacancies.visibility = View.VISIBLE
            countVacancies.text = formatFoundText(found)
        }
        adapter?.updateData(vacancies)
    }

    private fun showEmpty(message: String) {
        with(binding) {
            progressbar.visibility = View.GONE
            recyclerViewMain.visibility = View.GONE
            placeholderMainScreen.visibility = View.VISIBLE
            placeholderMainScreen.setImageResource(R.drawable.fav_error_cat_meme)
            placeholderText.visibility = View.VISIBLE
            placeholderText.text = message
            countVacancies.visibility = View.VISIBLE
            countVacancies.text = getString(R.string.no_vacancies_title)
        }
    }

    private fun showError(errorMessage: String) {
        with(binding) {
            progressbar.visibility = View.GONE
            recyclerViewMain.visibility = View.GONE
            placeholderMainScreen.visibility = View.VISIBLE
            placeholderMainScreen.setImageResource(R.drawable.favorites_placeholder)
            placeholderText.visibility = View.VISIBLE
            placeholderText.text = errorMessage
            countVacancies.visibility = View.GONE
        }
    }

    private fun updatePlaceholderForInput() {
        val inputText = binding.editText.text?.toString().orEmpty()
        if (inputText.isNotEmpty()) {
            binding.placeholderMainScreen.visibility = View.GONE
            return
        }
        binding.placeholderMainScreen.visibility = View.VISIBLE
        binding.placeholderMainScreen.setImageResource(R.drawable.placeholder_main_screen)
    }

    private fun updateEditActionIcon(hasText: Boolean) {
        val icon = if (hasText) R.drawable.ic_clear_button else R.drawable.ic_search
        binding.btnEditAction.setImageResource(icon)
    }

    private fun showKeyboard(view: View) {
        view.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun formatFoundText(found: Int): String {
        return getString(R.string.found_vacancies_count, found)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
