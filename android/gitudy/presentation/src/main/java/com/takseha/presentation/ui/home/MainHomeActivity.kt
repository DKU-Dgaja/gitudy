package com.takseha.presentation.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityMainHomeBinding
import com.takseha.presentation.ui.feed.FeedHomeFragment
import com.takseha.presentation.ui.mystudy.MyStudyHomeFragment
import com.takseha.presentation.ui.profile.ProfileHomeFragment
import com.takseha.presentation.viewmodel.MainHomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainHomeBinding
    private lateinit var viewModel: MainHomeViewModel
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
        setViewModel()
    }

    private fun setBinding() {
        binding = ActivityMainHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
    private fun setNoBackPressed() {
        this.onBackPressedDispatcher.addCallback(this, callback)
    }
    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[MainHomeViewModel::class.java]
        viewModel.getUserInfo()
    }
    private fun setMainFragmentView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val fragment = MainHomeFragment()

            sendUserInfo(fragment)
            supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, fragment).commit()
            binding.navHome.isChecked = true
        }
        with(binding) {
            navHome.setOnClickListener {
                val fragment = MainHomeFragment()

                sendUserInfo(fragment)
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, fragment).commit()
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
                val fragment = ProfileHomeFragment()

                sendUserInfo(fragment)
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, fragment).commit()
                navProfile.isChecked = true
            }
        }
    }
    private fun sendUserInfo(fragment: Fragment) {
        var bundle = Bundle()

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                bundle.putSerializable("userInfo", it)
                fragment.arguments = bundle
            }
        }
    }

    // TODO : sendMyStudyList, sendStudyList function 만들기
}