package ru.practicum.android.diploma.filterregion.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.Tools
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.databinding.FragmentRegionBinding

class RegionFragment : Fragment() {
    private var _binding: FragmentRegionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegionViewModel by viewModel()

    private var allRegions: List<Area> = emptyList()
    private var adapter: RegionAdapter? = null

    private val networkChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Tools.isConnected(context)) {
                // Интернет появился - перезагружаем данные
                viewModel.loadRegionsFromUseCase()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        // Настройка тулбара
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupSearch()
        viewModel.regionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegionState.Loading -> showLoading()
                is RegionState.Content -> showContent(state.regions)
                is RegionState.Empty -> showEmpty()
                is RegionState.Error -> showError()
                RegionState.RegionSelected -> onRegionSelected()
            }
        }

        viewModel.loadRegionsFromUseCase()
    }

    private fun setupRecyclerView() {
        adapter = RegionAdapter { selectedRegion ->
            viewModel.loadFilterParameters(selectedRegion)
        }
        binding.regionRecyclerView.adapter = adapter
        binding.regionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onRegionSelected() {
        findNavController().navigateUp()
    }

    private fun showEmpty() {
        binding.progressbar.visibility = View.GONE
        binding.regionRecyclerView.visibility = View.GONE
        binding.placeholderImage.setImageResource(R.drawable.fav_error_cat_meme)
        binding.placeholderText.setText(R.string.no_region)
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE
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

    private fun showError() {
        binding.progressbar.visibility = View.GONE
        binding.regionRecyclerView.visibility = View.GONE
        binding.placeholderImage.setImageResource(R.drawable.error_region) // Иконка ошибки
        binding.placeholderText.setText(R.string.error_region) // Ошибка загрузки
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(networkChangeReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(networkChangeReceiver)
    }
}
