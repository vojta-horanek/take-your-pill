package eu.vojtechh.takeyourpill.activity

import android.content.Intent
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
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.ActivityMainBinding
import eu.vojtechh.takeyourpill.fragment.HistoryFragment
import eu.vojtechh.takeyourpill.fragment.HomeFragment
import eu.vojtechh.takeyourpill.fragment.SettingsFragment
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.MainViewModel

const val REQUEST_CODE_INTRO = 22

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

        // if (firstRun)
        //val intent = Intent(this, AppIntroActivity::class.java)
        //startActivityForResult(intent, REQUEST_CODE_INTRO)

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
                    is HomeFragment -> {
                        view.bottomNavigation.visibility = View.VISIBLE
                    }
                    is HistoryFragment, is SettingsFragment -> {
                        view.bottomNavigation.visibility = View.VISIBLE
                    }
                    else -> {
                        view.bottomNavigation.visibility = View.INVISIBLE
                    }
                }
            }
        }, true)


        view.bottomNavigation.setOnNavigationItemReselectedListener { /* Disables reselection */ }
        view.bottomNavigation.setOnNavigationItemSelectedListener {
            currentNavigationFragment?.apply {
                exitTransition = MaterialFadeThrough()
            }
            true
        }

        view.bottomNavigation.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode != RESULT_OK) {
                finish()
            }
        }
    }

}