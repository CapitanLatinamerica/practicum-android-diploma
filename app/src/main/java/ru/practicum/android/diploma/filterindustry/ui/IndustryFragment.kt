package ru.practicum.android.diploma.filterindustry.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.Tools
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase

class IndustryFragment : Fragment() {
    private val filteringUseCase: FilteringUseCase by inject()

    private var _binding: FragmentIndustryBinding? = null
    private val binding: FragmentIndustryBinding
        get() = _binding!!
    private var adapter = IndustryAdapter(onItemClick = { industry -> })

    private val networkChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Tools.isConnected(context)) {
                // Интернет появился - перезагружаем данные
                viewModel.getIndustries()
            }
        }
    }

    private val viewModel: IndustryVIewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIndustryBinding.inflate(inflater, container, false)
        binding.applyButton.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = IndustryAdapter { selectedIndustry ->
            adapter.selectIndustry(selectedIndustry)
            binding.applyButton.visibility = if (adapter.hasSelection()) View.VISIBLE else View.GONE
        }

        binding.industryRecyclerView.adapter = adapter

        lifecycleScope.launch {
            val params = filteringUseCase.loadParameters()
            val selectedIndustryId = params?.industryId?.toString() ?: ""

            viewModel.getIndustries(selectedIndustryId) // передаем ID для выделения
        }

        viewModel.industryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                IndustryState.Loading -> {
                    showLoading()
                }

                is IndustryState.Content -> {
                    binding.progressbar.visibility = View.GONE
                    adapter.updateItems(state.industryList, state.selectedIndustryId)
                    binding.applyButton.visibility = if (adapter.hasSelection()) View.VISIBLE else View.GONE
                    onIndustryStateHideElements()
                }

                IndustryState.Error -> {
                    binding.progressbar.visibility = View.GONE
                    onIndustryStateErrorShowElements()
                }
            }
        }
        defineListeners()
        binding.applyButton.visibility = if (adapter.hasSelection()) View.VISIBLE else View.GONE
    }

    private fun defineListeners() {
        binding.industryEditText.addTextChangedListener { editable ->
            onChangedText(editable)
            if (adapter.itemCount == 0 && binding.industryEditText.text.isNotEmpty()) {
                binding.industryScrolls.visibility = View.GONE
                binding.placeholderImage.visibility = View.VISIBLE
                binding.placeholderImage.setImageResource(R.drawable.fav_error_cat_meme)
                binding.placeholderText.visibility = View.VISIBLE
                binding.placeholderText.text = "Отрасль не найдена"
                binding.applyButton.visibility = if (adapter.hasSelection()) View.VISIBLE else View.GONE
            } else {
                binding.industryScrolls.visibility = View.VISIBLE
                binding.placeholderImage.visibility = View.GONE
                binding.placeholderImage.setImageResource(R.drawable.error_region)
                binding.placeholderText.visibility = View.GONE
                binding.placeholderText.text = getString(R.string.error_region)
                binding.applyButton.visibility = if (adapter.hasSelection()) View.VISIBLE else View.GONE
            }
        }

        binding.clearIcon.setOnClickListener {
            binding.industryEditText.text?.clear()
            binding.applyButton.visibility = if (adapter.hasSelection()) View.VISIBLE else View.GONE
        }

        binding.applyButton.setOnClickListener {
            val selectedIndustry = adapter.getSelectedIndustry()
            lifecycleScope.launch {
                // Загружаем текущие параметры
                val currentParams = filteringUseCase.loadParameters()

                // Создаем обновленные параметры
                val updatedParams = (currentParams ?: FilterParameters()).copy(
                    industry = selectedIndustry?.name.toString(),
                    industryId = selectedIndustry?.id ?: 0
                )

                // Сохраняем
                filteringUseCase.saveParameters(updatedParams)
                parentFragmentManager.popBackStack()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun onChangedText(editable: Editable?) {
        val hasText = !editable.isNullOrEmpty()
        val iconRes = if (hasText) R.drawable.ic_clear_button else R.drawable.ic_search
        binding.clearIcon.setImageResource(iconRes)
        binding.clearIcon.visibility = View.VISIBLE
        adapter.filter(editable?.toString() ?: "")
        binding.applyButton.visibility = if (adapter.hasSelection()) View.VISIBLE else View.GONE
    }

    private fun onIndustryStateErrorShowElements() {
        binding.industryScrolls.visibility = View.GONE
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE
    }

    private fun onIndustryStateHideElements() {
        binding.industryScrolls.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE
        binding.industryScrolls.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
