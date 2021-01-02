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
import androidx.navigation.ui.NavigationUI
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.ActivityMainBinding
import eu.vojtechh.takeyourpill.fragment.HistoryFragment
import eu.vojtechh.takeyourpill.fragment.HomeFragment
import eu.vojtechh.takeyourpill.fragment.PreferencesFragment
import eu.vojtechh.takeyourpill.fragment.dialog.HistoryViewDialog
import eu.vojtechh.takeyourpill.klass.Constants
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.MainViewModel

const val REQUEST_CODE_INTRO = 22

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (Pref.firstRun) {
            val intent = Intent(this, AppIntroActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_INTRO)
        }

        supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(
                fm: FragmentManager,
                fragment: Fragment,
                v: View,
                savedInstanceState: Bundle?
            ) {
                TransitionManager.beginDelayedTransition(
                    binding.root,
                    Slide(Gravity.BOTTOM).excludeTarget(R.id.navHostFragment, true)
                )
                when (fragment) {
                    is HomeFragment, is HistoryFragment, is PreferencesFragment, is HistoryViewDialog, is MaterialTimePicker -> {
                        if (fragment is MaterialTimePicker) {
                            if (fragment.tag == Constants.TAG_TIME_PICKER_HISTORY_VIEW) {
                                binding.bottomNavigation.visibility = View.VISIBLE
                            } else {
                                binding.bottomNavigation.visibility = View.INVISIBLE
                            }
                        } else {
                            binding.bottomNavigation.visibility = View.VISIBLE
                        }
                    }
                    else -> {
                        binding.bottomNavigation.visibility = View.INVISIBLE
                    }
                }
            }
        }, true)

        binding.bottomNavigation.setOnNavigationItemReselectedListener { }
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                Pref.firstRun = false
            } else {
                finish()
            }
        }
    }

}