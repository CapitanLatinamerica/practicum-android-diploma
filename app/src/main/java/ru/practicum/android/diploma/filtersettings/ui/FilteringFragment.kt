package ru.practicum.android.diploma.filtersettings.ui

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilteringBinding

class FilteringFragment : Fragment() {

    private var _binding: FragmentFilteringBinding? = null
    private val binding: FragmentFilteringBinding
        get() = _binding!!

    private val viewModel: FilteringViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilteringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.filterState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        binding.salaryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSalaryTextChanged(s?.toString() ?: "")
                updateClearButtonVisibility()
                handleSalaryHintColor()
            }

            override fun afterTextChanged(s: Editable?) = Unit

        })

        binding.salaryEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.salaryEditText.clearFocus()
                true
            } else {
                false
            }
        }

        binding.salaryEditText.setOnFocusChangeListener { _, hasFocus ->
            handleSalaryHintColor()
            updateClearButtonVisibility()
            handleKeyboardVisibility(hasFocus)
        }

        binding.clearIcon.setOnClickListener {
            binding.salaryEditText.text?.clear()

        }

        binding.workplaceEdit.apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_filteringFragment_to_workplaceFragment)
            }
        }

        binding.industryEdit.apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_filteringFragment_to_industryFragment)
            }
        }

        viewModel.buttonsVisibilityState.observe(viewLifecycleOwner) { visible ->
            handleVisibilityButtonsState(visible)
        }

        binding.salaryCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onOnlyWithSalaryToggled(isChecked)
        }

        binding.deleteButton.setOnClickListener {
            viewModel.clearAllParams()

        }
//        binding.industryEdit.apply {
//            setOnClickListener {
//                val selectedIndustryId = viewModel.selectedIndustryId ?: ""
//                val action = FilteringFragmentDirections.actionFilteringFragmentToIndustryFragment(selectedIndustryId)
//                findNavController().navigate(action)
//            }
//        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("workplaceUpdated")
            ?.observe(viewLifecycleOwner) { updated ->
                if (updated) {
                    viewModel.loadFilterSettings()
                    findNavController().currentBackStackEntry?.savedStateHandle?.set("workplaceUpdated", false)
                }
            }

//        Обновляем состояние выбранной отрасли во ViewModel
//        parentFragmentManager.setFragmentResultListener("selectedIndustryKey", viewLifecycleOwner) { _, bundle ->
//            val selectedId = bundle.getString("selectedIndustryId")
//            val selectedName = bundle.getString("selectedIndustryName")
//
//            if (!selectedId.isNullOrEmpty() && !selectedName.isNullOrEmpty()) {
//                viewModel.onIndustrySelected(selectedName)
//            }
//        }
    }

    private fun renderState(state: FilterState) {
        handleWorkplaceState(state)

        handleIndustryState(state)

        handleSalaryState(state)

        handleCheckBoxSalaryState(state)

    }

    private fun updateClearButtonVisibility() {
        val hasText = binding.salaryEditText.text?.isNotEmpty() == true
        val hasFocus = binding.salaryEditText.isFocused
        binding.clearIcon.visibility = if (hasText && hasFocus) View.VISIBLE else View.GONE
    }

    private fun handleSalaryHintColor() {
        val hasText = binding.salaryEditText.text?.isNotEmpty() == true
        val hasFocus = binding.salaryEditText.isFocused
        val hintColor = when {
            hasFocus -> ContextCompat.getColor(requireContext(), R.color.blue)
            hasText -> ContextCompat.getColor(requireContext(), R.color.black_universal)
            else -> ContextCompat.getColor(requireContext(), R.color.color_hint_edit_text)
        }
        binding.salaryHint.setTextColor(hintColor)
    }

    private fun handleKeyboardVisibility(hasFocus: Boolean) {
        if (!hasFocus) {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.salaryEditText.windowToken, 0)
        }
    }

    private fun updateTextInputLayoutAppearance(
        layout: TextInputLayout,
        text: String,
        clearAction: () -> Unit
    ) {
        val hasText = text.isNotEmpty()
        if (hasText) {
            layout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear_button)
            layout.defaultHintTextColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.onPrimary))

            // Отключение ripple эффекта для endIcon
            val endIconImageButton = layout.findViewById<CheckableImageButton>(
                com.google.android.material.R.id.text_input_end_icon
            )
            endIconImageButton?.apply {
                background = null
                isClickable = true
                isFocusable = false
            }
            layout.setEndIconOnClickListener { clearAction() }
        } else {
            layout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_forward)
            layout.defaultHintTextColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray))
            layout.setEndIconOnClickListener(null)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFilterSettings()
    }

    private fun handleVisibilityButtonsState(hasAnyChange: Boolean) {
        val visibility = if (hasAnyChange) View.VISIBLE else View.GONE
        binding.applyButton.visibility = visibility
        binding.deleteButton.visibility = visibility
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleWorkplaceState(state: FilterState) {
        val wpCurrent = binding.workplaceEdit.text?.toString() ?: ""
        if (wpCurrent != state.country) {
            binding.workplaceEdit.setText(state.country)
        }
        updateTextInputLayoutAppearance(binding.workplace, state.country) {
            viewModel.clearWorkplace()
        }
    }

    private fun handleIndustryState(state: FilterState) {
        val indCurrent = binding.industryEdit.text?.toString() ?: ""
        if (indCurrent != state.industry) {
            binding.industryEdit.setText(state.industry)
        }
        updateTextInputLayoutAppearance(binding.industry, state.industry) {
            viewModel.clearIndustry()
        }
    }

    private fun handleSalaryState(state: FilterState) {
        if (!binding.salaryEditText.isFocused) {
            val current = binding.salaryEditText.text?.toString() ?: ""
            if (current != state.salary) {
                binding.salaryEditText.setText(state.salary)
            }
        }
    }

    private fun handleCheckBoxSalaryState(state: FilterState) {
        with(binding) {
            salaryCheckBox.setOnCheckedChangeListener(null)
            salaryCheckBox.isChecked = state.onlyWithSalary
            salaryCheckBox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onOnlyWithSalaryToggled(isChecked)
            }
        }
    }
}
