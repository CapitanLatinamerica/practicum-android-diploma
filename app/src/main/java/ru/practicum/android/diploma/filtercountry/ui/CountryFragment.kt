package ru.practicum.android.diploma.filtercountry.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.databinding.FragmentCountryBinding

class CountryFragment : Fragment() {

    private var _binding: FragmentCountryBinding? = null
    private val binding: FragmentCountryBinding
        get() = _binding!!

    private val viewModel: CountryViewModel by viewModel()

    private val adapter = AreaAdapter { area ->
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "selectedCountry", area
        )
        findNavController().navigateUp()
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
            }
        }
    }

    private fun showContent(countries: List<Area>) {
        binding.countryRecyclerView.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE

        adapter.update(countries)
    }

    private fun showError() {
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
        _binding = null
    }
}
