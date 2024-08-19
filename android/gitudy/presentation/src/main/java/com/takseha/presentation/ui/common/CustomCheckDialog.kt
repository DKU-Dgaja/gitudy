package com.takseha.presentation.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import com.takseha.presentation.R
import com.takseha.presentation.databinding.LayoutDialogCheckBinding
import com.takseha.presentation.databinding.LayoutDialogSetBinding

// CustomDialog 클래스
class CustomCheckDialog(context: Context) {
    private val dialog: Dialog = Dialog(context, R.style.CustomDialogTheme)
    private val binding: LayoutDialogCheckBinding = LayoutDialogCheckBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        binding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        binding.confirmBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun setAlertText(text: String) {
        binding.alertText.text = text
    }

    fun setAlertDetailText(text: String) {
        binding.alertDetailText.text = text
    }

    fun setCancelBtnText(text: String) {
        binding.cancelBtn.text = text
    }

    fun setConfirmBtnText(text: String) {
        binding.confirmBtn.text = text
    }

    fun setOnCancelClickListener(listener: () -> Unit) {
        binding.cancelBtn.setOnClickListener {
            listener()
            dialog.dismiss()
        }
    }

    fun setOnConfirmClickListener(listener: () -> Unit) {
        binding.confirmBtn.setOnClickListener {
            listener()
            dialog.dismiss()
        }
    }

    fun show() {
        dialog.show()
    }
}
