package eu.vojtechh.takeyourpill.activity

//import com.mikepenz.aboutlibraries.LibsBuilder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.psdev.licensesdialog.LicensesDialogFragment
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

            buttonLicence.onClick { openLicencesDialog() }

            textVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
        }

    }

    private fun openUrl(url: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        startActivity(browserIntent)
    }

    private fun openLicencesDialog() {
        LicensesDialogFragment.Builder(this)
            .setNotices(R.raw.notices)
            .setShowFullLicenseText(false)
            .setIncludeOwnLicense(true)
            .build()
            .show(supportFragmentManager, null)
    }
}