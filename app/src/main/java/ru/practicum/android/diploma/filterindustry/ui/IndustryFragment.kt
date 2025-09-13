package ru.practicum.android.diploma.filterindustry.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding

class IndustryFragment : Fragment() {
    private var _binding: FragmentIndustryBinding? = null
    private val binding: FragmentIndustryBinding
        get() = _binding!!
    private val adapter = IndustryAdapter(onItemClick = { industry ->
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
        setupToolbar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.industryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is IndustryState.Content -> {
                    Toast.makeText(requireActivity(), "${state.industryList}", Toast.LENGTH_SHORT).show()
                }

                IndustryState.Error -> {
                    Toast.makeText(requireActivity(), "$state", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.industryRecyclerView.adapter = adapter
        viewModel.industryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is IndustryState.Content -> adapter.updateItems(state.industryList, selectedId = null)
                is IndustryState.Error ->
                    Toast.makeText(requireContext(), "Ошибка загрузки отраслей", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getIndustries()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            // Возврат на предыдущий экран
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
