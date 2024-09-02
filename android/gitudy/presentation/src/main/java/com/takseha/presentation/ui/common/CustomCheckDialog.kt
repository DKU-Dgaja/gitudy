package com.takseha.presentation.ui.common

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.takseha.presentation.R
import com.takseha.presentation.databinding.LayoutDialogCheckBinding

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
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
            clearFocusFromActivityRoot()
            dialog.dismiss()
        }
    }

    fun setOnConfirmClickListener(listener: () -> Unit) {
        binding.confirmBtn.setOnClickListener {
            listener()
            clearFocusFromActivityRoot()
            dialog.dismiss()
        }
    }

    private fun clearFocusFromActivityRoot() {
        val imm = binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0) // 키보드 숨기기
        val activityRootView = (binding.root.context as? Activity)?.window?.decorView?.findViewById<View>(android.R.id.content)
        activityRootView?.clearFocus()
    }

    fun show() {
        dialog.show()
    }
}
