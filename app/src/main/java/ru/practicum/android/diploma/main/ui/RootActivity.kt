package ru.practicum.android.diploma.main.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private val binding: ActivityRootBinding by lazy {
        ActivityRootBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        // Слушатель места, где мы находимся, чтобы скрывать панель
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.visibility = when (destination.id) {
                R.id.vacancyDetailsFragment,
                R.id.filteringFragment,
                R.id.industryFragment,
                R.id.workplaceFragment,
                R.id.countryFragment,
                R.id.regionFragment -> View.GONE
                else -> View.VISIBLE
            }
        }
    }

}
