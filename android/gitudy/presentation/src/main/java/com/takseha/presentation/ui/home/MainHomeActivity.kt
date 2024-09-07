package com.takseha.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityMainHomeBinding
import com.takseha.presentation.ui.common.SnackBarHelper
import com.takseha.presentation.ui.feed.FeedHomeFragment
import com.takseha.presentation.ui.mystudy.MyStudyHomeFragment
import com.takseha.presentation.ui.profile.ProfileHomeFragment
import com.takseha.presentation.viewmodel.common.BaseViewModel
import com.takseha.presentation.viewmodel.feed.FeedHomeViewModel
import com.takseha.presentation.viewmodel.feed.StudyApplyViewModel
import com.takseha.presentation.viewmodel.home.MainHomeAlertViewModel
import com.takseha.presentation.viewmodel.home.MainHomeViewModel
import com.takseha.presentation.viewmodel.mystudy.MyStudyHomeViewModel
import com.takseha.presentation.viewmodel.profile.ProfileHomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainHomeBinding
    private val mainHomeViewModel: MainHomeViewModel by viewModels()
    private val myStudyHomeViewModel: MyStudyHomeViewModel by viewModels()
    private val feedHomeViewModel: FeedHomeViewModel by viewModels()
    private val profileHomeViewModel: ProfileHomeViewModel by viewModels()
    private lateinit var snackBarHelper: SnackBarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home)

        // 뒤로가기 금지
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })

        setBinding()
        setMainFragmentView(savedInstanceState)

        snackBarHelper = SnackBarHelper(this)
        lifecycleScope.launch {
            mainHomeViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        mainHomeViewModel.resetSnackbarMessage()
                    }
                }
            }
        }

        lifecycleScope.launch {
            myStudyHomeViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        myStudyHomeViewModel.resetSnackbarMessage()
                    }
                }
            }
        }

        lifecycleScope.launch {
            feedHomeViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        feedHomeViewModel.resetSnackbarMessage()
                    }
                }
            }
        }

        lifecycleScope.launch {
            profileHomeViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        profileHomeViewModel.resetSnackbarMessage()
                    }
                }
            }
        }
    }

    private fun setBinding() {
        binding = ActivityMainHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
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