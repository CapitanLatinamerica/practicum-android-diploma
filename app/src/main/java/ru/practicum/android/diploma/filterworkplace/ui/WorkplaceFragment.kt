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
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.databinding.FragmentWorkplaceBinding

class WorkplaceFragment : Fragment() {

    private var _binding: FragmentWorkplaceBinding? = null
    private val binding: FragmentWorkplaceBinding
        get() = _binding!!

    private val viewModel: WorkplaceViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.workplaceState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        // Отслеживаем наличие выбранной страны для кнопки "Выбрать"
        viewModel.hasSelectedCountry.observe(viewLifecycleOwner) { hasCountry ->
            handleSelectButtonVisibility(hasCountry)
        }

        binding.countryEdit.setOnClickListener {
            val action = WorkplaceFragmentDirections.actionWorkplaceFragmentToCountryFragment()
            findNavController().navigate(action)
        }

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.workplaceFragment)
        navBackStackEntry.savedStateHandle.getLiveData<Area>("selectedCountry")
            .observe(viewLifecycleOwner) { selectedArea ->
                viewModel.onCountrySelected(selectedArea.name)
                binding.countryEdit.setText(selectedArea.name)
            }

        binding.regionEdit.setOnClickListener {
            findNavController().navigate(R.id.action_workplaceFragment_to_regionFragment)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "workplaceUpdated",
                true
            )
            findNavController().navigateUp()
        }

        binding.applyButton.setOnClickListener {
            viewModel.applyChanges()
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "workplaceUpdated",
                true
            )
            findNavController().navigateUp()
        }
    }

    private fun renderState(state: WorkplaceState) {
        val countryCurrent = binding.countryEdit.text?.toString() ?: ""
        val countryNew = state.country ?: ""
        if (countryCurrent != countryNew) {
            binding.countryEdit.setText(countryNew)
        }

        val regionCurrent = binding.regionEdit.text?.toString() ?: ""
        val regionNew = state.region ?: ""
        if (regionCurrent != regionNew) {
            binding.regionEdit.setText(regionNew)
        }

        updateTextInputLayoutAppearance(binding.country, countryNew) {
            viewModel.clearCountry()
        }

        updateTextInputLayoutAppearance(binding.region, regionNew) {
            viewModel.clearRegion()
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

    private fun handleSelectButtonVisibility(hasCountry: Boolean) {
        binding.applyButton.visibility = if (hasCountry) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
