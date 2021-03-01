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
                .title(R.string.app_name)
                .description(R.string.intro_welcome_description)
                .image(R.drawable.ic_empty_view)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title(R.string.intro_new_pill)
                .description(R.string.intro_new_pill_description)
                .image(R.mipmap.img_new_pill)
                .background(R.color.colorAccent)
                .backgroundDark(R.color.colorAccentDark)
                .scrollable(false)
                .build()
        )
        addSlide(
            SimpleSlide.Builder()
                .title(R.string.intro_reminder)
                .description(R.string.intro_reminder_description)
                .image(R.mipmap.img_notification)
                .background(R.color.colorBlue)
                .backgroundDark(R.color._intro_colorBlueDark)
                .scrollable(false)
                .build()
        )
        addSlide(
            SimpleSlide.Builder()
                .title(R.string.intro_history)
                .description(R.string.intro_history_description)
                .image(R.mipmap.img_history)
                .background(R.color.colorOrange)
                .backgroundDark(R.color._intro_colorOrangeDark)
                .scrollable(false)
                .build()
        )
    }


}