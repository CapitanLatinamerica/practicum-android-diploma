package ru.practicum.android.diploma.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.VacancyItemBinding
import ru.practicum.android.diploma.search.ui.model.VacancyUi

class VacanciesAdapter(
    private val clickListener: VacanciesClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_FOOTER = 1
    }

    private val vacancies = mutableListOf<VacancyUi>()
    private var showFooter = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val inflater = LayoutInflater.from(parent.context)
            val binding = VacancyItemBinding.inflate(inflater, parent, false)
            VacanciesViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading_footer, parent, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VacanciesViewHolder) {
            val vacancy = vacancies[position]
            holder.bind(vacancy)
            holder.itemView.setOnClickListener {
                clickListener.onVacancyClick(vacancy)
            }
        }
    }

    override fun getItemCount(): Int {
        return vacancies.size + if (showFooter) 1 else 0
    }

    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        return if (position < vacancies.size) TYPE_ITEM else TYPE_FOOTER
    }

    fun updateData(newVacancies: List<VacancyUi>) {
        vacancies.clear()
        vacancies.addAll(newVacancies)
        notifyDataSetChanged()
    }

    fun showLoadingFooter(show: Boolean) {
        if (showFooter == show) return
        showFooter = show
        if (show) {
            notifyItemInserted(vacancies.size)
        } else {
            notifyItemRemoved(vacancies.size)
        }
    }

    fun interface VacanciesClickListener {
        fun onVacancyClick(vacancyUi: VacancyUi)
    }
}
