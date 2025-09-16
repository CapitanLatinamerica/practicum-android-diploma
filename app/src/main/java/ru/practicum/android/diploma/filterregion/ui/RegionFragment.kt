package ru.practicum.android.diploma.filterregion.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.databinding.FragmentRegionBinding
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase

class RegionFragment : Fragment() {
    private var _binding: FragmentRegionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegionViewModel by viewModel()
    private val filteringUseCase: FilteringUseCase by inject()

    private var allRegions: List<Area> = emptyList()
    private var adapter: RegionAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupToolbar()
        setupSearch()
        setupObservers()

        loadRegionsFromUseCase()
    }

    private fun setupRecyclerView() {
        adapter = RegionAdapter { selectedRegion ->
            lifecycleScope.launch {
                // Сохраняем выбранный регион в SharedPreferences через FilteringUseCase
                val currentParams = filteringUseCase.loadParameters() ?: FilterParameters()
                // Получаем название страны по parentId региона
                val countryName = if (selectedRegion.parentId != null) {
                    when (val result = viewModel.findCountryByRegion(selectedRegion.parentId)) {
                        is Resource.Success -> {
                            result.data?.name ?: run {
                                currentParams.country
                            }
                        }

                        is Resource.Error -> {
                            currentParams.country
                        }
                    }
                } else {
                    // Если parentId null, оставляем текущее значение
                    currentParams.country
                }

                val updatedParams = currentParams.copy(
                    country = countryName,
                    countryId = selectedRegion.parentId ?: currentParams.countryId,
                    region = selectedRegion.name,
                    regionId = selectedRegion.id
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
                is RegionState.Empty -> showEmpty(state.message)
                is RegionState.Error -> showError(state.message)
            }
        }
    }

    private fun loadRegionsFromUseCase() {
        lifecycleScope.launch {
            val params = filteringUseCase.loadParameters()
            val countryId = params?.countryId ?: 0 // Если null, используем 0

            if (countryId == 0) {
                // Если countryId = 0, загружаем все регионы всех стран
                viewModel.getRegions(null)
            } else {
                // Иначе загружаем регионы конкретной страны
                viewModel.getRegions(countryId)
            }
        }
    }

    private fun showEmpty(message: String) {
        binding.progressbar.visibility = View.GONE
        binding.regionRecyclerView.visibility = View.GONE
        binding.placeholderImage.setImageResource(R.drawable.fav_error_cat_meme)
        binding.placeholderText.setText(R.string.no_region)
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE
        Toast.makeText(requireContext(), "Нет результатов: $message", Toast.LENGTH_SHORT).show()
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

        // Сохраняем все регионы для фильтрации
        allRegions = regions
        adapter?.update(regions)
    }

    private fun showError(message: String) {
        binding.progressbar.visibility = View.GONE
        binding.regionRecyclerView.visibility = View.GONE
        binding.placeholderImage.setImageResource(R.drawable.error_region) // Иконка ошибки
        binding.placeholderText.setText(R.string.error_region) // Ошибка загрузки
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE
        Toast.makeText(requireContext(), "Ошибка: $message", Toast.LENGTH_SHORT).show()
    }

    private fun setupSearch() {
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSearchIcon(s)
                viewModel.filterRegions(s.toString()) // ← вызываем ViewModel
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        binding.btnEditAction.setOnClickListener {
            if (binding.editText.text.isNotEmpty()) {
                binding.editText.text.clear()
            }
        }
    }

    private fun updateSearchIcon(text: CharSequence?) {
        val isEmpty = text.isNullOrEmpty()
        binding.btnEditAction.setImageResource(
            if (isEmpty) R.drawable.ic_search else R.drawable.ic_clear_button
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }
}
