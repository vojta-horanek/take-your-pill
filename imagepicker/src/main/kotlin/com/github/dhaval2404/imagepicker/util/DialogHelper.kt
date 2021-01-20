package com.github.dhaval2404.imagepicker.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.ViewCompat
import com.github.dhaval2404.imagepicker.R
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.github.dhaval2404.imagepicker.databinding.DialogChooseAppBinding
import com.github.dhaval2404.imagepicker.listener.DismissListener
import com.github.dhaval2404.imagepicker.listener.ResultListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

/**
 * Show Dialog
 *
 * @author Dhaval Patel / Vojtěch Hořánek
 * @version 2.0
 * @since 04 January 2018
 */
internal object DialogHelper {

    /**
     * Show Image Provide Picker Dialog. This will streamline the code to pick/capture image
     */
    fun showChooseAppDialog(
        context: Context,
        listener: ResultListener<ImageProvider>,
        dismissListener: DismissListener?
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val customView = DialogChooseAppBinding.inflate(layoutInflater)

        val dialog = BottomSheetDialog(context).apply {
            setContentView(customView.root)
            setOnDismissListener {
                dismissListener?.onDismiss()
            }
            setOnCancelListener {
                listener.onResult(null)
            }
            apply {
                behavior.addBottomSheetCallback(
                    getBottomSheetCallback({ bottomSheet, newState ->
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            ViewCompat.setBackground(bottomSheet, createRoundDrawable(bottomSheet))
                        }
                    }) { _, _ -> })
            }
        }

        dialog.show()

        // Handle Camera option click
        customView.lytCameraPick.setOnClickListener {
            listener.onResult(ImageProvider.CAMERA)
            dialog.dismiss()
        }

        // Handle Gallery option click
        customView.lytGalleryPick.setOnClickListener {
            listener.onResult(ImageProvider.GALLERY)
            dialog.dismiss()
        }
    }

    private fun createRoundDrawable(
        bottomSheet: View
    ): MaterialShapeDrawable {
        val shapeAppearanceModel =
            ShapeAppearanceModel.builder(
                bottomSheet.context,
                0,
                R.style.AppTheme_Theme_CustomShapeAppearanceBottomSheetDialog
            ).build()

        val currentDrawable = bottomSheet.background as MaterialShapeDrawable
        return MaterialShapeDrawable(shapeAppearanceModel).apply {
            initializeElevationOverlay(bottomSheet.context)
            fillColor = currentDrawable.fillColor
            tintList = currentDrawable.tintList
            elevation = currentDrawable.elevation
            strokeWidth = currentDrawable.strokeWidth
            strokeColor = currentDrawable.strokeColor
        }
    }

    private fun getBottomSheetCallback(
        onStateChanged: (View, Int) -> Unit, onSlide: (View, Float) -> Unit
    ) =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) =
                onStateChanged(bottomSheet, newState)

            override fun onSlide(bottomSheet: View, slideOffset: Float) =
                onSlide(bottomSheet, slideOffset)
        }
}
