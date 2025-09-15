package ru.practicum.android.diploma.filterregion.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.entity.Area

// ViewHolder для одного элемента списка регионов
class RegionViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    private val onItemClickListener: (Area) -> Unit // колбэк для сообщения о клике
) : RecyclerView.ViewHolder(inflater.inflate(R.layout.country_item, parent, false)) {

    private val regionName: TextView = itemView.findViewById(R.id.countryName) // текстовое поле с именем региона

    fun bind(region: Area) {
        regionName.text = region.name
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener(region)
            }
        }
    }

}
