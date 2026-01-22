package com.inesengel.travelapp.UI.view.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.components.BottomNavigationContainer
import com.inesengel.travelapp.UI.viewmodel.SettingsViewModel
import com.inesengel.travelapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: SettingsViewModel by viewModels()
    private val bottomNavDestinations = setOf(
        R.id.main_destination_fragment,
        R.id.favorites_fragment,
        R.id.profile_fragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setupDarkMode()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupBottomBar()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavComposeView.visibility =
                if (destination.id in bottomNavDestinations) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    private fun setupDarkMode() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isDarkMode.collect { isDarkMode ->
                    AppCompatDelegate.setDefaultNightMode(
                        if (isDarkMode) {
                            AppCompatDelegate.MODE_NIGHT_YES
                        } else {
                            AppCompatDelegate.MODE_NIGHT_NO
                        }
                    )
                }
            }
        }
    }

    private fun setupBottomBar() {
        binding.bottomNavComposeView.setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BottomNavigationContainer(
                        navController = navController,
                        onAddDestination = {
                            navController.navigate(R.id.add_destination_fragment)
                        }
                    )
                }
            }
        }
    }
}