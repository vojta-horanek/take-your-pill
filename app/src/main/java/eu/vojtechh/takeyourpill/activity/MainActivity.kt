package eu.vojtechh.takeyourpill.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.ActivityMainBinding
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val view by viewBinding(ActivityMainBinding::inflate)
    private val model: MainViewModel by viewModels()

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.navHostFragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(view.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homescreen, R.id.pills -> {
                    view.bottomNavigation.visibility = View.VISIBLE
                    view.floatingActionButton.show()
                    view.floatingActionButton.extend()

                }
                R.id.history -> {
                    view.bottomNavigation.visibility = View.VISIBLE
                    view.floatingActionButton.hide()
                }
                else -> {
                    view.bottomNavigation.visibility = View.GONE
                    view.floatingActionButton.hide()
                }
            }
        }

        view.bottomNavigation.setOnNavigationItemReselectedListener { /* Disables reselection */ }
        view.bottomNavigation.setOnNavigationItemSelectedListener {
            currentNavigationFragment?.apply {
                exitTransition = MaterialFadeThrough()
            }
            true
        }

        view.floatingActionButton.setOnClickListener {
            currentNavigationFragment?.apply {
                exitTransition = MaterialElevationScale(false)
                reenterTransition = MaterialElevationScale(true)
            }
            navController.navigate(R.id.edit)
        }

        view.bottomNavigation.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}