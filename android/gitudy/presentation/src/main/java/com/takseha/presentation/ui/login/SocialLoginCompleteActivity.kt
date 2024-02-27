package com.takseha.presentation.ui.login

import android.content.Intent
import android.graphics.BlurMaskFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivitySocialLoginCompleteBinding
class SocialLoginCompleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySocialLoginCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_login_complete)
        setBinding()

        binding.confirmBtn.setOnClickListener {
//                val transaction = supportFragmentManager.beginTransaction()
//                val fragment = PopupFragment()
//                transaction.replace(R.id.loginPopupFragmentContainerView, fragment)
//                transaction.commit()
            startActivity(Intent(this, AgreementActivity::class.java))
        }
    }

    private fun setBinding() {
        binding = ActivitySocialLoginCompleteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}