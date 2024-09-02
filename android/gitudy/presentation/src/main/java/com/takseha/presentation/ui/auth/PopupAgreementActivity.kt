package com.takseha.presentation.ui.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.takseha.presentation.R
import com.takseha.presentation.ui.common.SnackBarHelper
import com.takseha.presentation.viewmodel.auth.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PopupAgreementActivity : AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var snackBarHelper: SnackBarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_agreement)
        window.statusBarColor = ContextCompat.getColor(this, R.color.WHITE)

        snackBarHelper = SnackBarHelper(this)
        lifecycleScope.launch {
            registerViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        registerViewModel.resetSnackbarMessage()
                    }
                }
            }
        }
    }
}