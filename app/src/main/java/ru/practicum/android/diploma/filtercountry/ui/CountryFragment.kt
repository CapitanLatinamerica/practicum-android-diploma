package ru.practicum.android.diploma.filtercountry.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.Tools
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.databinding.FragmentCountryBinding
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase

class CountryFragment : Fragment() {

    private var _binding: FragmentCountryBinding? = null
    private val binding: FragmentCountryBinding
        get() = _binding!!

    private val viewModel: CountryViewModel by viewModel()
    private val filteringUseCase: FilteringUseCase by inject()

    private val adapter = AreaAdapter { area ->
        // Сохраняем выбранную страну напрямую в SharedPreferences
        lifecycleScope.launch {
            val currentParams = filteringUseCase.loadParameters() ?: FilterParameters()
            val updatedParams = currentParams.copy(
                country = area.name,
                countryId = area.id
            )
            filteringUseCase.saveParameters(updatedParams)
            findNavController().navigateUp()
        }
    }

    private val networkChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Tools.isConnected(context)) {
                viewModel.getCountries()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupToolbar()

        viewModel.getCountries()
    }

    private fun setupRecyclerView() {
        binding.countryRecyclerView.adapter = adapter
        binding.countryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewModel.countryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CountryState.Content -> showContent(state.countries)
                is CountryState.Error -> showError()
                is CountryState.Loading -> showLoading()
            }
        }
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE
        binding.countryRecyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE
    }

    private fun showContent(countries: List<Area>) {
        binding.progressbar.visibility = View.GONE
        binding.countryRecyclerView.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE

        adapter.update(countries)
    }

    private fun showError() {
        binding.progressbar.visibility = View.GONE
        binding.countryRecyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(networkChangeReceiver)
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // Регистрируем receiver
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(networkChangeReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        // Отписка
        requireContext().unregisterReceiver(networkChangeReceiver)
    }
}
