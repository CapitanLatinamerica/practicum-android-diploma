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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.internal.CheckableImageButton
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilteringBinding

class FilteringFragment : Fragment() {

    private var _binding: FragmentFilteringBinding? = null
    private val binding: FragmentFilteringBinding
        get() = _binding!!

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

        binding.salaryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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

        binding.workplaceEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateWorkplaceAppearance()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        updateWorkplaceAppearance()

        binding.workplaceEdit.setOnClickListener {
            showWorkplaceDialog()
        }

    }

    // Для проверки текстинуптов
    private fun showWorkplaceDialog() {
        // Здесь логика выбора места работы
        val workplaces = listOf("Office", "Remote", "Hybrid")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Workplace")
            .setItems(workplaces.toTypedArray()) { _, which ->
                binding.workplaceEdit.setText(workplaces[which])
                updateWorkplaceAppearance()
            }
            .show()
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

    private fun updateWorkplaceAppearance() {
        val hasText = binding.workplaceEdit.text?.isNotEmpty() == true
        if (hasText) {
            binding.workplace.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear_button)
            binding.workplace.defaultHintTextColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.onPrimary))

            // Отключение ripple эффекта для endIcon
            val endIconImageButton = binding.workplace.findViewById<CheckableImageButton>(
                com.google.android.material.R.id.text_input_end_icon
            )
            endIconImageButton?.apply {
                background = null
                isClickable = true
                isFocusable = false
            }

            binding.workplace.setEndIconOnClickListener {
                binding.workplaceEdit.text?.clear()
            }
        } else {
            binding.workplace.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_forward)
            binding.workplace.defaultHintTextColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray))

            binding.workplace.setEndIconOnClickListener(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
