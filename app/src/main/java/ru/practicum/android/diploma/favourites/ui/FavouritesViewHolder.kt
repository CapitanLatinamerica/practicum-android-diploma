package ru.practicum.android.diploma.favourites.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.VacancyItemBinding
import ru.practicum.android.diploma.search.ui.model.VacancyUi

class FavouritesViewHolder(
    private val binding: VacancyItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VacancyUi) {
        binding.nameVacancy.text = "${item.name}, ${item.area}"
        binding.nameCompany.text = item.employer
        binding.salary.text = item.salary
        Glide.with(itemView)
            .load(item.logo)
            .fitCenter()
            .placeholder(R.drawable.placeholder_vacancy)
            .into(binding.logoCompany)
    }
}
