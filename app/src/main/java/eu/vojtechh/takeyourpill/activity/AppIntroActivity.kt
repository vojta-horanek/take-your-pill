package eu.vojtechh.takeyourpill.activity

import android.os.Bundle
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
import eu.vojtechh.takeyourpill.R


class AppIntroActivity : IntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(
            SimpleSlide.Builder()
                .title(R.string.intro_welcome)
                .description(R.string.intro_welcome_description)
                .image(R.drawable.ic_empty_view)
                .background(android.R.color.white)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title(R.string.intro_new_pill)
                .description(R.string.intro_new_pill_description)
                .image(R.drawable.ic_add_fab)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build()
        )
        addSlide(
            SimpleSlide.Builder()
                .title(R.string.intro_reminder)
                .description(R.string.intro_reminder_description)
                .image(R.drawable.ic_fab_alarm)
                .background(R.color.colorBlue)
                .backgroundDark(R.color.colorDarkBlue)
                .scrollable(false)
                .build()
        )
        addSlide(
            SimpleSlide.Builder()
                .title(R.string.intro_history)
                .description(R.string.intro_history_description)
                .image(R.drawable.ic_fab_history)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build()
        )
    }


}