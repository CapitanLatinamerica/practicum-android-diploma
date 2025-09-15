package ru.practicum.android.diploma.filterregion.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.databinding.FragmentRegionBinding
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase

class RegionFragment : Fragment() {
    private var _binding: FragmentRegionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegionViewModel by viewModel()
    private val filteringUseCase: FilteringUseCase by inject()

    private lateinit var adapter: RegionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupToolbar()
        setupObservers()

        loadRegionsFromUseCase()
    }

    private fun setupRecyclerView() {
        adapter = RegionAdapter { selectedRegion ->
            lifecycleScope.launch {
                // Сохраняем выбранный регион в SharedPreferences через FilteringUseCase
                val currentParams = filteringUseCase.loadParameters() ?: return@launch

                Log.d("RegionFragment", "Текущие параметры: $currentParams")
                val updatedParams = currentParams.copy(
                    region = selectedRegion.name, // сохраняем название региона
                    regionId = selectedRegion.id   // сохраняем ID региона
                )

                filteringUseCase.saveParameters(updatedParams)

                parentFragmentManager.popBackStack()
            }
        }
        binding.regionRecyclerView.adapter = adapter
        binding.regionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupObservers() {
        viewModel.regionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegionState.Loading -> showLoading()
                is RegionState.Content -> showContent(state.regions)
                is RegionState.Error -> showError()
            }
        }
    }

    private fun loadRegionsFromUseCase() {
        lifecycleScope.launch {
            val params = filteringUseCase.loadParameters()
            viewModel.getRegions(params!!.countryId)
        }
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE
        binding.regionRecyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE
    }

    private fun showContent(regions: List<Area>) {
        binding.progressbar.visibility = View.GONE
        binding.regionRecyclerView.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE
        adapter.update(regions)
    }

    private fun showError() {
        binding.progressbar.visibility = View.GONE
        binding.regionRecyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
