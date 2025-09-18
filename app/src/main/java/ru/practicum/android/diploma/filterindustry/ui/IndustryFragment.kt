package ru.practicum.android.diploma.filterindustry.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase

class IndustryFragment : Fragment() {
    private val filteringUseCase: FilteringUseCase by inject()

    private var _binding: FragmentIndustryBinding? = null
    private val binding: FragmentIndustryBinding
        get() = _binding!!
    private var adapter = IndustryAdapter(onItemClick = { industry -> })

    private val viewModel: IndustryVIewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        adapter = IndustryAdapter { selectedIndustry ->
            adapter.selectIndustry(selectedIndustry)
            updateApplyButtonVisibility()
        }

        binding.industryRecyclerView.adapter = adapter

        lifecycleScope.launch {
            val params = filteringUseCase.loadParameters()
            val selectedIndustryId = params?.industryId?.toString() ?: "" // industry хранится как String ID

            viewModel.getIndustries(selectedIndustryId) // передаем ID для выделения
        }

        viewModel.industryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is IndustryState.Content -> {
                    adapter.updateItems(state.industryList, state.selectedIndustryId)
                    updateApplyButtonVisibility()
                    onIndustryStateHideElements()
                }

                IndustryState.Error -> {
                    Toast.makeText(requireActivity(), "Ошибка загрузки отраслей", Toast.LENGTH_SHORT).show()
                    onIndustryStateErrorShowElements()
                }
            }
        }
        defineListeners()
        updateApplyButtonVisibility()
    }

    private fun defineListeners() {
        binding.industryEditText.addTextChangedListener { editable ->
            onChangedText(editable)
        }

        binding.clearIcon.setOnClickListener {
            binding.industryEditText.text?.clear()
            binding.applyButton.visibility = View.GONE
        }

        binding.applyButton.setOnClickListener {
            val selectedIndustry = adapter.getSelectedIndustry()
            lifecycleScope.launch {
                // Загружаем текущие параметры
                val currentParams = filteringUseCase.loadParameters()

                // Создаем обновленные параметры
                val updatedParams = (currentParams ?: FilterParameters()).copy(
                    industry = selectedIndustry?.name.toString(),
                    industryId = selectedIndustry?.id ?: 0
                )

                // Сохраняем
                filteringUseCase.saveParameters(updatedParams)
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun onChangedText(editable: Editable?) {
        val hasText = !editable.isNullOrEmpty()
        val iconRes = if (hasText) R.drawable.ic_clear_button else R.drawable.ic_search
        binding.clearIcon.setImageResource(iconRes)
        binding.clearIcon.visibility = View.VISIBLE
        adapter.filter(editable?.toString() ?: "")
        updateApplyButtonVisibility()
    }

    private fun onIndustryStateErrorShowElements() {
        binding.industryScrolls.visibility = View.GONE
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE
    }

    private fun onIndustryStateHideElements() {
        binding.industryScrolls.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            // Возврат на предыдущий экран
            parentFragmentManager.popBackStack()
        }
    }

    private fun updateApplyButtonVisibility() {
        binding.applyButton.visibility = if (adapter.hasSelection()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
