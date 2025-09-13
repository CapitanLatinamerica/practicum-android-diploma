package ru.practicum.android.diploma.filterindustry.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding

class IndustryFragment : Fragment() {
    private val args: IndustryFragmentArgs by navArgs()

    private var _binding: FragmentIndustryBinding? = null
    private val binding: FragmentIndustryBinding
        get() = _binding!!
    private var adapter = IndustryAdapter(onItemClick = { industry ->
        // Обработка клика по элементу
        Toast.makeText(requireContext(), "Выбрана отрасль: ${industry.name}", Toast.LENGTH_SHORT).show()
        // При необходимости передать выбор в FilteringFragment или закрыть фрагмент
    })

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

        viewModel.industryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is IndustryState.Content -> {
                    adapter.updateItems(state.industryList, selectedId = null)
                    updateApplyButtonVisibility()
                    adapter.updateItems(state.industryList, args.selectedIndustryId)
                    binding.industryScrolls.visibility = View.VISIBLE
                    binding.placeholderImage.visibility = View.GONE
                    binding.placeholderText.visibility = View.GONE
                }
                IndustryState.Error -> {
                    Toast.makeText(requireActivity(), "Ошибка загрузки отраслей", Toast.LENGTH_SHORT).show()
                    binding.industryScrolls.visibility = View.GONE
                    binding.placeholderImage.visibility = View.VISIBLE
                    binding.placeholderText.visibility = View.VISIBLE
                }
            }
        }
        viewModel.getIndustries()
        binding.industryEditText.addTextChangedListener { editable ->
            val hasText = !editable.isNullOrEmpty()
            val iconRes = if (hasText) R.drawable.ic_clear_button else R.drawable.ic_search
            binding.clearIcon.setImageResource(iconRes)
            binding.clearIcon.visibility = View.VISIBLE

            adapter.filter(editable?.toString() ?: "")
            updateApplyButtonVisibility()
        }

        binding.clearIcon.setOnClickListener {
            binding.industryEditText.text?.clear()
            binding.applyButton.visibility = View.GONE
        }

        binding.applyButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Выбрана отрасль: ${adapter.selectedIndustryId}",
                Toast.LENGTH_SHORT).show()
        }

        updateApplyButtonVisibility()
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
