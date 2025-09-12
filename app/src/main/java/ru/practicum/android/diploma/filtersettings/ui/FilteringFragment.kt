package ru.practicum.android.diploma.filtersettings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        binding.workplace.setOnClickListener {
            showWorkplaceDialog()
        }

        binding.industryEdit.apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_filteringFragment_to_industryFragment)
            }
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
            }
            .show()
    }

    private fun navigateToIndustryFragment() {
        findNavController().navigate(R.id.action_filteringFragment_to_industryFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
