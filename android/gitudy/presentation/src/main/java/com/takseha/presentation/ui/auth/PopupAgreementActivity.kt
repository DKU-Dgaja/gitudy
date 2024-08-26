package com.takseha.presentation.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.takseha.presentation.R

class PopupAgreementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_agreement)
        window.statusBarColor = ContextCompat.getColor(this, R.color.WHITE)
    }
}