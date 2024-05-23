package com.takseha.presentation.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityMainHomeBinding
import com.takseha.presentation.ui.feed.FeedHomeFragment
import com.takseha.presentation.ui.mystudy.MyStudyHomeFragment
import com.takseha.presentation.ui.profile.ProfileHomeFragment

class MainHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainHomeBinding

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            super.handleOnBackCancelled()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home)
        setInit()
        setMainFragmentView(savedInstanceState)
    }

    private fun setInit() {
        setBinding()
        setNoBackPressed()
    }

    private fun setBinding() {
        binding = ActivityMainHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
    private fun setNoBackPressed() {
        this.onBackPressedDispatcher.addCallback(this, callback)
    }
    private fun setMainFragmentView(savedInstanceState: Bundle?) {
        with(binding) {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, MainHomeFragment()).commit()
                navHome.isChecked = true
            }

            navHome.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, MainHomeFragment()).commit()
                navHome.isChecked = true
            }
            navMyStudy.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, MyStudyHomeFragment()).commit()
                navMyStudy.isChecked = true
            }
            navFeed.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, FeedHomeFragment()).commit()
                navFeed.isChecked = true
            }
            navProfile.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, ProfileHomeFragment()).commit()
                navProfile.isChecked = true
            }
        }
    }
}