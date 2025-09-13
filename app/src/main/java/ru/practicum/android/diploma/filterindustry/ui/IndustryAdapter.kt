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

    inner class IndustryViewHolder(
        private val binding: ItemIndustryCheckboxBinding) : RecyclerView.ViewHolder(binding.root
        ) {
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

    private var selectedIndustryId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val binding = ItemIndustryCheckboxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IndustryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val industry = items[position]
        val isSelected = industry.id.toString() == selectedIndustryId
        holder.bind(industry, isSelected)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Industry>, selectedId: String?) {
        items = newItems
        selectedIndustryId = selectedId
        notifyDataSetChanged()
    }
}
