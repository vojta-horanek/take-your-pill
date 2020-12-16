package eu.vojtechh.takeyourpill.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
    }
}