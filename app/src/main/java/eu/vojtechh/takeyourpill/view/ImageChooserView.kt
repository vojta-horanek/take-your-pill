package eu.vojtechh.takeyourpill.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.klass.onClick

class ImageChooserView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var imageListener: () -> Unit = {}
    private var deleteListener: () -> Unit = {}
    private lateinit var imageChooserCard: MaterialCardView
    private lateinit var imageChooserPhoto: AppCompatImageView
    private lateinit var imageChooserDelete: AppCompatImageView

    init {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.layout_image_chooser, this)

        orientation = VERTICAL

        imageChooserCard = view.findViewById(R.id.image_chooser_card)
        imageChooserPhoto = view.findViewById(R.id.image_chooser_photo)
        imageChooserDelete = view.findViewById(R.id.image_chooser_delete)

        imageChooserPhoto.onClick { imageListener() }
        imageChooserDelete.onClick { deleteListener() }

    }

    fun setImageDrawable(drawable: Drawable?) {
        imageChooserPhoto.setImageDrawable(drawable)
    }

    fun setDeleteVisible(visible: Boolean) {
        imageChooserDelete.isVisible = visible
    }

    fun setOnImageClickListener(listener: () -> Unit) {
        imageListener = listener
    }

    fun setOnDeleteClickListener(listener: () -> Unit) {
        deleteListener = listener
    }
}