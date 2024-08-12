package com.takseha.presentation.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import com.takseha.presentation.R
import com.takseha.presentation.databinding.LayoutDialogBinding

// CustomDialog 클래스
class CustomDialog(context: Context) {
    private val dialog: Dialog = Dialog(context, R.style.CustomDialogTheme)
    private val binding: LayoutDialogBinding = LayoutDialogBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        binding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        binding.confirmBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams().apply {
            copyFrom(dialog.window?.attributes)
            dimAmount = 0.7f // 배경 어두운 정도 (0.0f - 1.0f)
        }
        dialog.window?.attributes = lp
    }

    fun setAlertText(text: String) {
        binding.alertText.text = text
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
