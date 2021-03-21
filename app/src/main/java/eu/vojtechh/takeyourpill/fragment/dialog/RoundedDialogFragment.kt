package eu.vojtechh.takeyourpill.fragment.dialog

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import com.github.dhaval2404.imagepicker.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

open class RoundedDialogFragment() : BottomSheetDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?) =
        (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            behavior.addBottomSheetCallback(
                getBottomSheetCallback({ bottomSheet, newState ->
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        ViewCompat.setBackground(bottomSheet, createRoundDrawable(bottomSheet))
                    }
                }) { _, _ -> })
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
