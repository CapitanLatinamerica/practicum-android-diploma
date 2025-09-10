package ru.practicum.android.diploma.favourites.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.VacancyItemBinding
import ru.practicum.android.diploma.search.ui.model.VacancyUi

class FavouritesAdapter(
    private val clickListener: VacanciesClickListener
) : RecyclerView.Adapter<FavouritesViewHolder>() {

    private val vacancies = mutableListOf<VacancyUi>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return FavouritesViewHolder(VacancyItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        val vacancy = vacancies[position]
        holder.bind(vacancy)
        holder.itemView.setOnClickListener {
            clickListener.onVacancyClick(vacancy)
        }
    }

    override fun getItemCount(): Int {
        return vacancies.size
    }

    fun updateData(newVacancies: List<VacancyUi>) {
        vacancies.clear()
        vacancies.addAll(newVacancies)
        notifyDataSetChanged()
    }

    fun interface VacanciesClickListener {
        fun onVacancyClick(vacancyUi: VacancyUi)
    }
}
