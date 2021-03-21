package eu.vojtechh.takeyourpill.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.mikepenz.aboutlibraries.LibsBuilder
import eu.vojtechh.takeyourpill.BuildConfig
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.databinding.ActivityAboutBinding
import eu.vojtechh.takeyourpill.klass.onClick
import eu.vojtechh.takeyourpill.klass.viewBinding


class AboutActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityAboutBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.run {
            buttonClose.onClick { finish() }

            buttonGithub.onClick {
                openUrl("https://github.com/vojta-horanek/take-your-pill")
            }

            buttonIcons.onClick {
                openUrl("https://github.com/ShimonHoranek/material-icons")
            }

            buttonLicence.onClick { switchLicenceFragment() }

            textVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
        }

        val fragment = LibsBuilder()
            .withAboutIconShown(false)
            .withVersionShown(false)
            .withShowLoadingProgress(true)
            .supportFragment()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragmentLibs, fragment, "fragment_libs")
            .commit()
    }

    private fun openUrl(url: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        startActivity(browserIntent)
    }

    private fun switchLicenceFragment() {
        val isVisible = binding.fragmentLibs.isVisible
        binding.fragmentLibs.isVisible = !isVisible
        val drawable =
            if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
        binding.buttonLicence.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_article,
            0,
            drawable,
            0
        )
    }
}