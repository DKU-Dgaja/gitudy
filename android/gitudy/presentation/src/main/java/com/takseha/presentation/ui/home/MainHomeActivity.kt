package com.takseha.presentation.ui.home

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityMainHomeBinding
import com.takseha.presentation.ui.feed.FeedHomeFragment
import com.takseha.presentation.ui.mystudy.MyStudyHomeFragment
import com.takseha.presentation.ui.profile.ProfileHomeFragment

class MainHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home)
        window.statusBarColor = Color.argb(0xFF,0x1B,0x1B,0x25)
        setBinding()
        this.onBackPressedDispatcher.addCallback(this, callback)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, MainHomeFragment()).commit()
        }
        setMainFragmentView()
    }

    private fun setBinding() {
        binding = ActivityMainHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setMainFragmentView() {
        with(binding) {
            navHome.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, MainHomeFragment()).commit()
            }
            navMyStudy.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, MyStudyHomeFragment()).commit()
            }
            navFeed.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, FeedHomeFragment()).commit()
            }
            navProfile.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, ProfileHomeFragment()).commit()
            }
        }
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            super.handleOnBackCancelled()
        }
    }
}