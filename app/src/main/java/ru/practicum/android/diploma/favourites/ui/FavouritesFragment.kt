package ru.practicum.android.diploma.favourites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.Tools
import ru.practicum.android.diploma.databinding.FragmentFavouritesBinding

class FavouritesFragment : Fragment() {

    private val favouritesViewModel: FavouritesViewModel by viewModel()

    private var _binding: FragmentFavouritesBinding? = null
    private val binding: FragmentFavouritesBinding
        get() = _binding!!

    private var adapter: FavouritesAdapter? = null

    private var debouncedClick: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        debouncedClick = Tools.debounce(
            delayMillis = CLICK_DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false
        ) { vacancyId ->
            navigateToVacancyDetails(vacancyId)
        }

        adapter = FavouritesAdapter() { vacancyUi ->
            debouncedClick?.invoke(vacancyUi.id)
        }

        binding.favoritesRecyclerView.adapter = adapter
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        favouritesViewModel.favouritesState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }

        favouritesViewModel.getFavourites()
    }

    private fun navigateToVacancyDetails(vacancyId: String) {
        val action = FavouritesFragmentDirections.actionFavouritesFragmentToVacancyDetailsFragment(vacancyId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.favoritesRecyclerView.adapter = null
        adapter = null
        debouncedClick = null
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(favouritesState: FavouritesState) {
        when (favouritesState) {
            is FavouritesState.Empty -> {
                binding.container.visibility = View.VISIBLE
                binding.favoritesRecyclerView.visibility = View.GONE
            }

            is FavouritesState.Content -> {
                adapter?.updateData(favouritesState.favouritesList)
                binding.container.visibility = View.GONE
                binding.favoritesRecyclerView.visibility = View.VISIBLE
            }

            is FavouritesState.Error -> {
                binding.container.visibility = View.VISIBLE
                binding.emptyListTextView.setText(R.string.nothing_found)
                binding.placeholderImage.setImageResource(R.drawable.fav_error_cat_meme)
                binding.favoritesRecyclerView.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
