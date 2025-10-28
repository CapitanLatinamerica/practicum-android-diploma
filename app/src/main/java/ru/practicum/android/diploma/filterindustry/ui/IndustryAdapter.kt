package ru.practicum.android.diploma.filterindustry.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.common.domain.entity.Industry
import ru.practicum.android.diploma.databinding.ItemIndustryCheckboxBinding

class IndustryAdapter(
    private var items: List<Industry> = emptyList(),
    private val onItemClick: (Industry) -> Unit
) : RecyclerView.Adapter<IndustryAdapter.IndustryViewHolder>() {

    private var filteredItems: List<Industry> = items.toList()
    private var selectedIndustryId: String? = null
    private var originalSelectedIndustryId: String? = null // Сохраняем оригинальный выбор

    inner class IndustryViewHolder(
        private val binding: ItemIndustryCheckboxBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(industry: Industry, isSelected: Boolean) {
            binding.industryTextView.text = industry.name
            binding.industryRadioButton.isChecked = isSelected

            binding.root.setOnClickListener {
                onItemClick(industry)
            }
            binding.industryRadioButton.setOnClickListener {
                onItemClick(industry)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val binding = ItemIndustryCheckboxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IndustryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val industry = filteredItems[position]
        val isSelected = industry.id.toString() == selectedIndustryId
        holder.bind(industry, isSelected)
    }

    override fun getItemCount(): Int = filteredItems.size

    fun updateItems(newItems: List<Industry>, selectedId: String?) {
        items = newItems
        selectedIndustryId = selectedId
        originalSelectedIndustryId = selectedId // Сохраняем оригинальный выбор
        filteredItems = items.toList()
        notifyDataSetChanged()
    }

    fun selectIndustry(industry: Industry) {
        selectedIndustryId = industry.id.toString()
        originalSelectedIndustryId = industry.id.toString() // Обновляем оригинальный выбор
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredItems = if (query.isBlank()) {
            items.toList()
        } else {
            items.filter { it.name.contains(query, ignoreCase = true) }
        }

        // Проверяем, есть ли выбранная отрасль в отфильтрованном списке
        val selectedInFiltered = filteredItems.any { it.id.toString() == originalSelectedIndustryId }
        selectedIndustryId = if (selectedInFiltered) originalSelectedIndustryId else null

        notifyDataSetChanged()
    }

    fun getSelectedIndustry(): Industry? {
        return filteredItems.find { it.id.toString() == originalSelectedIndustryId }
    }

    fun hasSelection(): Boolean {
        return originalSelectedIndustryId != null &&
            filteredItems.any { it.id.toString() == originalSelectedIndustryId }
    }
}
