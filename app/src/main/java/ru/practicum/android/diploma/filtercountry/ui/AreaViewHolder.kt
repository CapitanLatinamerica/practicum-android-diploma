package ru.practicum.android.diploma.filtercountry.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.entity.Area

class AreaViewHolder(
    itemView: View,
    val onItemClickListener: (Area) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private val areaItem: TextView = itemView.findViewById(R.id.countryName)

    fun bind(area: Area) {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener(area)
            }
        }
        areaItem.text = area.name
    }
}
