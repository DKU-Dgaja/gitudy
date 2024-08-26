package com.takseha.presentation.ui.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityMainHomeAlertBinding
import com.takseha.presentation.viewmodel.home.MainHomeAlertViewModel

class MainHomeAlertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainHomeAlertBinding
    private val viewModel: MainHomeAlertViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home_alert)
        window.statusBarColor = ContextCompat.getColor(this, R.color.WHITE)
    }
}