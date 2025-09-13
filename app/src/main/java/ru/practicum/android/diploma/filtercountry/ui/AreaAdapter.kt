package ru.practicum.android.diploma.filtercountry.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.entity.Area

class AreaAdapter(private val onTrackClickListener: (Area) -> Unit) :
    RecyclerView.Adapter<AreaViewHolder>() {
    var countries: List<Area> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.country_item, parent, false)
        return AreaViewHolder(view, onTrackClickListener)
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    fun update(areas: List<Area>) {
        countries = areas
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        holder.bind(countries[position])
    }
}
