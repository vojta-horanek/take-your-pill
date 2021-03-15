package eu.vojtechh.takeyourpill.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.ActivityMainBinding
import eu.vojtechh.takeyourpill.klass.Pref
import eu.vojtechh.takeyourpill.klass.Utils
import eu.vojtechh.takeyourpill.klass.viewBinding
import eu.vojtechh.takeyourpill.viewmodel.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val requestCodeIntro = 22

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Pref.firstRun) {
            val intent = Intent(this, AppIntroActivity::class.java)
            introResult.launch(intent)
        }

        setTheme(R.style.AppTheme)
        setContentView(binding.root)

        Utils.setTheme(Pref.theme)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = when (destination.id) {
                R.id.homescreen, R.id.history, R.id.settings -> true
                else -> false
            }
        }

        binding.bottomNavigation.setOnNavigationItemReselectedListener { }
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        model.planReminders(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    val introResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                Pref.firstRun = false
            } else {
                finish()
            }
        }

}