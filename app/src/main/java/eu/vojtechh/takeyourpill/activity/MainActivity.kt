package eu.vojtechh.takeyourpill.activity

import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.ActivityMainBinding
import eu.vojtechh.takeyourpill.fragment.HistoryFragment
import eu.vojtechh.takeyourpill.fragment.HomeFragment
import eu.vojtechh.takeyourpill.fragment.PillsFragment
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

        /* We must have two "hiders" since if I show the FAB in this registerFragmentLifecycleCallbacks it slides up */
        supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(
                fm: FragmentManager,
                fragment: Fragment,
                v: View,
                savedInstanceState: Bundle?
            ) {
                TransitionManager.beginDelayedTransition(
                    view.root,
                    Slide(Gravity.BOTTOM).excludeTarget(R.id.navHostFragment, true)
                )
                when (fragment) {
                    is HomeFragment, is PillsFragment, is HistoryFragment -> {
                        view.bottomNavigation.visibility = View.VISIBLE
                    }
                    else -> {
                        view.bottomNavigation.visibility = View.GONE
                    }
                }
            }
        }, true)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homescreen, R.id.pills -> {
                    view.floatingActionButton.show()
                    view.floatingActionButton.extend()
                }
                R.id.history -> {
                    view.floatingActionButton.hide()
                }
                else -> {
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