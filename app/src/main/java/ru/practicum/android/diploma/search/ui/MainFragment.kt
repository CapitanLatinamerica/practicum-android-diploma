package ru.practicum.android.diploma.search.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.ErrorType
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

    private var onVacancyClickDebounce: ((VacancyUi) -> Unit)? = null

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

        viewModel.isFilterApplied.observe(viewLifecycleOwner) { applied ->
            switchFilterMarkIcon(applied)
        }
        viewModel.checkFilterStatus()

        onVacancyClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { vacancy ->
            val action = MainFragmentDirections.actionMainFragmentToVacancyDetailsFragment(vacancy.id)
            findNavController().navigate(action)
        }

        adapter = VacanciesAdapter { vacancy ->
            onVacancyClickDebounce?.invoke(vacancy)
        }

        binding.recyclerViewMain.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMain.adapter = adapter

        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.isBottomLoading.observe(viewLifecycleOwner) { loading ->
            adapter?.showLoadingFooter(loading)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { toastMessage ->
                Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
            }
        }

        binding.recyclerViewMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScroll(dy, recyclerView)
            }
        })

        // Cлушатель результата от FilteringFragment о том, запускать поиск или просто обновить параметры
        setFragmentResultListener("filters_applied") { _, bundle ->
            val performSearch = bundle.getBoolean("perform_search", false)
            viewModel.onFiltersApplied(performSearch)
        }

        defineListeners()
    }

    private fun defineListeners() {
        binding.editText.doOnTextChanged { text, _, _, _ ->
            handleTextChange(text)
        }

        binding.btnEditAction.setOnClickListener {
            handleEditText()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.filter -> {
                    findNavController().navigate(R.id.action_mainFragment_to_filteringFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun onScroll(dy: Int, recyclerView: RecyclerView) {
        if (dy <= 0) return
        val lm = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val lastVisible = lm.findLastVisibleItemPosition()
        val itemCount = adapter?.itemCount ?: 0
        if (lastVisible >= itemCount - 1) {
            viewModel.onLastItemReached()
        }
    }

    private fun handleEditText() {
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

    private fun handleTextChange(s: CharSequence?) {
        val text = s?.toString().orEmpty().trim()
        updateEditActionIcon(text.isNotEmpty())
        if (text.isBlank()) {
            adapter?.updateData(emptyList())
            binding.countVacancies.visibility = View.GONE
            viewModel.clearSearch()
        } else {
            viewModel.searchDebounce(text)
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.Initial -> showInitial()
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.found, state.vacancies)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.Error -> showError(state.errorType, state.errorMessage)
        }
    }

    private fun showInitial() {
        with(binding) {
            progressbar.visibility = View.GONE
            recyclerViewMain.visibility = View.GONE
            placeholderMainScreen.setImageResource(R.drawable.placeholder_main_screen)
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

    private fun switchFilterMarkIcon(enabled: Boolean) {
        val id = binding.toolbar.menu.findItem(R.id.filter)
        var iconId = R.drawable.ic_filter
        if (enabled) {
            iconId = R.drawable.ic_filter_active
        }
        id?.icon = ContextCompat.getDrawable(requireContext(), iconId)

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

    private fun showError(errorType: ErrorType, errorMessage: String) {
        val placeholderImage = when (errorType) {
            ErrorType.NO_INTERNET -> R.drawable.no_internet_placeholder
            else -> R.drawable.server_error_placeholder
        }
        with(binding) {
            progressbar.visibility = View.GONE
            recyclerViewMain.visibility = View.GONE
            placeholderMainScreen.visibility = View.VISIBLE
            placeholderMainScreen.setImageResource(placeholderImage)
            placeholderText.visibility = View.VISIBLE
            placeholderText.text = errorMessage
            countVacancies.visibility = View.GONE
        }
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
