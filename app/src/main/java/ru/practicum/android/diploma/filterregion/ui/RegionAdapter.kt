package ru.practicum.android.diploma.filterregion.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.common.domain.entity.Area

// Адаптер для отображения списка регионов в RecyclerView
class RegionAdapter(
    private val onItemClickListener: (Area) -> Unit // обработчик клика на регион
) : RecyclerView.Adapter<RegionViewHolder>() {

    private var regions: List<Area> = emptyList() // список регионов

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RegionViewHolder(inflater, parent, onItemClickListener) // создание ViewHolder
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        val region = regions[position]
        holder.bind(region) // привязка данных региона к ViewHolder
    }

    override fun getItemCount(): Int = regions.size // количество элементов списка

    // Обновление данных в адаптере и перерисовка списка
    fun update(regions: List<Area>) {
        this.regions = regions
        notifyDataSetChanged()
    }
}
