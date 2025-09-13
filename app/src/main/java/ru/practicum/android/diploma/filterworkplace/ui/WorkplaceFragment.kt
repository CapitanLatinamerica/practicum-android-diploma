package ru.practicum.android.diploma.filterworkplace.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentWorkplaceBinding

class WorkplaceFragment : Fragment() {

    private var _binding: FragmentWorkplaceBinding? = null
    private val binding: FragmentWorkplaceBinding
        get() = _binding!!

    private val viewModel: WorkplaceViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.workplaceState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

//        binding.countryEdit.setOnClickListener {
//            showSelectionDialog(isWorkplace = true)
//        }
//        binding.regionEdit.setOnClickListener {
//            showSelectionDialog(isWorkplace = false)
//        }

        viewModel.buttonsVisibilityState.observe(viewLifecycleOwner) { visible ->
            handleVisibilityButtonsState(visible)
        }

        binding.countryEdit.apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_workplaceFragment_to_countryFragment)
            }
        }

        binding.regionEdit.apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_workplaceFragment_to_regionFragment)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

    }

//    // Для проверки текстинуптов
//    private fun showSelectionDialog(isWorkplace: Boolean) {
//        val items = listOf("Office", "Remote", "Hybrid")
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle(if (isWorkplace) "Select Workplace" else "Select Industry")
//            .setItems(items.toTypedArray()) { _, which ->
//                val selected = items[which]
//                if (isWorkplace) {
//                    viewModel.onCountrySelected(selected)
//                } else {
//                    viewModel.onRegionSelected(selected)
//                }
//            }
//            .show()
//    }

    private fun renderState(state: WorkplaceState) {

        val countryCurrent = binding.countryEdit.text?.toString() ?: ""
        if (countryCurrent != state.country) {
            binding.countryEdit.setText(state.country)
        }

        val regionCurrent = binding.regionEdit.text?.toString() ?: ""
        if (regionCurrent != state.region) {
            binding.regionEdit.setText(state.region)
        }

        state.country?.let {
            updateTextInputLayoutAppearance(binding.country, it) {
                viewModel.clearCountry()
            }
        }
        state.region?.let {
            updateTextInputLayoutAppearance(binding.region, it) {
                viewModel.clearRegion()
            }
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

    private fun handleVisibilityButtonsState(hasAnyChange: Boolean) {
        val visibility = if (hasAnyChange) View.VISIBLE else View.GONE
        binding.applyButton.visibility = visibility
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


