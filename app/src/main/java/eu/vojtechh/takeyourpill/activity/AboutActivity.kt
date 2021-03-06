package eu.vojtechh.takeyourpill.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        binding.textVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)

        val fragment = LibsBuilder()
            .withAboutMinimalDesign(true)
            .withAboutIconShown(false)
            .withVersionShown(false)
            .supportFragment()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragmentLibs, fragment, "fragment_libs")
            .commit()
    }
}