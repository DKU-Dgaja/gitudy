package com.takseha.presentation.ui.common

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.takseha.presentation.R
import com.takseha.presentation.databinding.LayoutSnackbarGreyBinding

class SnackBarHelper(private val context: Context) {

    fun makeSnackBar(rootView: View, message: String): Snackbar {
        val snackBar = Snackbar.make(rootView, "", Snackbar.LENGTH_LONG)
        val binding = LayoutSnackbarGreyBinding.inflate((context as AppCompatActivity).layoutInflater)

        @Suppress("RestrictedApi")
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout

        with(snackBarLayout) {
            removeAllViews()
            setPadding(22, 0, 22, 20)
            setBackgroundColor(ContextCompat.getColor(context, R.color.TRANSPARENT))
            addView(binding.root, 0)
        }

        with(binding) {
            snackBarText.text = message
        }

        return snackBar
    }
}