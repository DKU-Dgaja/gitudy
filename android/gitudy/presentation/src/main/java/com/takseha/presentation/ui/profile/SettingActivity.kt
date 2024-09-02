package com.takseha.presentation.ui.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.takseha.presentation.R
import com.takseha.presentation.ui.common.SnackBarHelper
import com.takseha.presentation.viewmodel.profile.SettingHomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingActivity : AppCompatActivity() {
    private val settingHomeViewModel: SettingHomeViewModel by viewModels()
    private lateinit var snackBarHelper: SnackBarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        snackBarHelper = SnackBarHelper(this)
        lifecycleScope.launch {
            settingHomeViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        settingHomeViewModel.resetSnackbarMessage()
                    }
                }
            }
        }
    }
}