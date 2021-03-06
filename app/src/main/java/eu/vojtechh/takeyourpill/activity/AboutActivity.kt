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
import eu.vojtechh.takeyourpill.klass.viewBinding


class AboutActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityAboutBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonClose.setOnClickListener {
            finish()
        }
        binding.buttonGithub.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/vojta-horanek/take-your-pill")
            )
            startActivity(browserIntent)
        }

        binding.buttonIcons.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/ShimonHoranek/material-icons")
            )
            startActivity(browserIntent)
        }

        binding.buttonLicence.setOnClickListener {
            val isVisible = binding.fragmentLibs.isVisible
            binding.fragmentLibs.isVisible = !isVisible
            val drawable = if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
            binding.buttonLicence.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_article,
                0,
                drawable,
                0
            )
        }

        binding.textVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)

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
}