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
 * @author Dhaval Patel
 * @version 1.0
 * @since 04 January 2018
 */
internal object DialogHelper {

    /**
     * Show Image Provide Picker Dialog. This will streamline the code to pick/capture image
     *
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
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        //In the EXPANDED STATE apply a new MaterialShapeDrawable with rounded corners
                        val newMaterialShapeDrawable: MaterialShapeDrawable =
                            createMaterialShapeDrawable(bottomSheet, context)
                        ViewCompat.setBackground(bottomSheet, newMaterialShapeDrawable)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
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

    private fun createMaterialShapeDrawable(
        bottomSheet: View,
        context: Context
    ): MaterialShapeDrawable {
        val shapeAppearanceModel =
            //Create a ShapeAppearanceModel with the same shapeAppearanceOverlay used in the style
            ShapeAppearanceModel.builder(
                context,
                0,
                R.style.AppTheme_Theme_CustomShapeAppearanceBottomSheetDialog
            )
                .build()

        //Create a new MaterialShapeDrawable (you can't use the original MaterialShapeDrawable in the BottomSheet)
        val currentMaterialShapeDrawable = bottomSheet.background as MaterialShapeDrawable
        val newMaterialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
        //Copy the attributes in the new MaterialShapeDrawable
        newMaterialShapeDrawable.initializeElevationOverlay(context)
        newMaterialShapeDrawable.fillColor = currentMaterialShapeDrawable.fillColor
        newMaterialShapeDrawable.tintList = currentMaterialShapeDrawable.tintList
        newMaterialShapeDrawable.elevation = currentMaterialShapeDrawable.elevation
        newMaterialShapeDrawable.strokeWidth = currentMaterialShapeDrawable.strokeWidth
        newMaterialShapeDrawable.strokeColor = currentMaterialShapeDrawable.strokeColor
        return newMaterialShapeDrawable
    }
}
