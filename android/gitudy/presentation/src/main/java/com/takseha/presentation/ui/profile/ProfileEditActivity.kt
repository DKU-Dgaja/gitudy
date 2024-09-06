package com.takseha.presentation.ui.profile

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.takseha.data.dto.auth.auth.UserInfoUpdatePageResponse
import com.takseha.data.dto.mystudy.SocialInfo
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityProfileEditBinding
import com.takseha.presentation.databinding.LayoutSnackbarRedBinding
import com.takseha.presentation.ui.common.CustomSetDialog
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.ui.common.SnackBarHelper
import com.takseha.presentation.viewmodel.profile.ProfileEditViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding
    private val viewModel: ProfileEditViewModel by viewModels()
    private lateinit var snackBarHelper: SnackBarHelper
    private val maxLength = 10

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)
        setBinding()

        snackBarHelper = SnackBarHelper(this)
        lifecycleScope.launch {
            viewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        viewModel.resetSnackbarMessage()
                    }
                }
            }
        }

        viewModel.getUserProfileInfo()
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                setUserInfo(it)
            }
        }

        with(binding) {
            backBtn.setOnClickListener {
                finish()
            }

            nicknameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    var nicknameLength = nicknameEditText.length()

                    nicknameValidationCheckBtn.visibility = VISIBLE
                    validationCheckedImg.visibility = GONE
                    alertText.text = ""

                    applyBtn.isEnabled = false    // 확인 버튼 초기화

                    if (nicknameLength > 0) {
                        // 현재 닉네임 중복 확인 방지 로직 추가
                        if (nicknameEditText.text.toString() == viewModel.uiState.value.name) {
                            nicknameValidationCheckBtn.isEnabled = false
                        } else {
                            if (nicknameLength > maxLength) {
                                nicknameValidationCheckBtn.isEnabled = false
                                makeSnackBar(
                                    getString(
                                        R.string.alert_text_length,
                                        maxLength
                                    )
                                ).apply {
                                    anchorView = applyBtn
                                }.show()
                            } else {
                                nicknameValidationCheckBtn.isEnabled = true
                            }
                            if (!isValidNickname(s.toString())) {
                                nicknameValidationCheckBtn.isEnabled = false
                                makeSnackBar(getString(R.string.alert_text_emoji)).apply {
                                    anchorView = applyBtn
                                }.show()
                            }
                        }
                    } else {
                        nicknameValidationCheckBtn.isEnabled = false
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            nicknameValidationCheckBtn.setOnClickListener {
                lifecycleScope.launch {
                    var name = nicknameEditText.text.toString()

                    viewModel.resetCorrectName()
                    viewModel.checkNickname(name)
                    viewModel.isCorrectName.collectLatest {
                        if (it != null) {
                            if (it) {
                                nicknameValidationCheckBtn.visibility = GONE
                                validationCheckedImg.visibility = VISIBLE

                                applyBtn.isEnabled = isApplyEnable()
                            } else {
                                alertText.text = getString(R.string.alert_name_not_ok)
                                validationCheckedImg.visibility = GONE
                                applyBtn.isEnabled = false
                            }
                        }
                    }
                }
            }

            githubLinkEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    githubLinkEditLayout.setBackgroundResource(R.drawable.box_stroke_black_r12)
                } else {
                    githubLinkEditLayout.setBackgroundResource(R.drawable.box_stroke_300_r12)
                }
            }
            githubLinkEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val githubLinkLength = githubLinkEditText.length()

                    githubLinkDeleteBtn.visibility = if (githubLinkLength > 0) VISIBLE else GONE
                    applyBtn.isEnabled = isApplyEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            githubLinkDeleteBtn.setOnClickListener {
                githubLinkEditText.setText("")
            }

            blogLinkEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    blogLinkEditLayout.setBackgroundResource(R.drawable.box_stroke_black_r12)
                } else {
                    blogLinkEditLayout.setBackgroundResource(R.drawable.box_stroke_300_r12)
                }
            }
            blogLinkEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val blogLinkLength = blogLinkEditText.length()
                    blogLinkDeleteBtn.visibility = if (blogLinkLength > 0) VISIBLE else GONE
                    applyBtn.isEnabled = isApplyEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            blogLinkDeleteBtn.setOnClickListener {
                blogLinkEditText.setText("")
            }

            linkedinLinkEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    linkedinLinkEditLayout.setBackgroundResource(R.drawable.box_stroke_black_r12)
                } else {
                    linkedinLinkEditLayout.setBackgroundResource(R.drawable.box_stroke_300_r12)
                }
            }
            linkedinLinkEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val linkedinLinkLength = linkedinLinkEditText.length()
                    linkedinLinkDeleteBtn.visibility =
                        if (linkedinLinkLength > 0) VISIBLE else GONE
                    applyBtn.isEnabled = isApplyEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            linkedinLinkDeleteBtn.setOnClickListener {
                linkedinLinkEditText.setText("")
            }

            profileOpenSwitch.setOnCheckedChangeListener { _, isChecked ->
                applyBtn.isEnabled = isApplyEnable()
            }

            applyBtn.setOnClickListener {
                val name = nicknameEditText.text.toString()
                val profileImageUrl = viewModel.uiState.value.profileImageUrl
                val socialInfo = SocialInfo(
                    githubLink = githubLinkEditText.text.toString(),
                    blogLink = blogLinkEditText.text.toString(),
                    linkedInLink = linkedinLinkEditText.text.toString()
                )
                val profilePublicYn = profileOpenSwitch.isChecked

                showUpdateProfileInfoDialog(name, profileImageUrl, socialInfo, profilePublicYn)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && view is EditText) {
            val outRect = Rect()
            view.getGlobalVisibleRect(outRect)
            if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                view.clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setUserInfo(
        userInfo: UserInfoUpdatePageResponse
    ) {
        with(binding) {
            nicknameEditText.setText(userInfo.name)
            // 현재 닉네임 중복 확인 방지
            nicknameValidationCheckBtn.isEnabled =
                nicknameEditText.text.toString() != userInfo.name
            Glide.with(this@ProfileEditActivity)
                .load(userInfo.profileImageUrl)
                .error(R.drawable.logo_profile_default)
                .into(profileImg)
            githubLinkEditText.setText(userInfo.socialInfo?.githubLink)
            blogLinkEditText.setText(userInfo.socialInfo?.blogLink)
            linkedinLinkEditText.setText(userInfo.socialInfo?.linkedInLink)
            profileOpenSwitch.isChecked = userInfo.profilePublicYn ?: false

            applyBtn.isEnabled = false
        }
    }

    private fun isApplyEnable(): Boolean {
        var isEnable: Boolean
        val uiState = viewModel.uiState.value

        with(binding) {
            val name = nicknameEditText.text.toString()
            val socialInfo = SocialInfo(
                githubLink = githubLinkEditText.text.toString(),
                blogLink = blogLinkEditText.text.toString(),
                linkedInLink = linkedinLinkEditText.text.toString()
            )
            val profilePublicYn = profileOpenSwitch.isChecked

            isEnable =
                uiState.name != name || uiState.socialInfo != socialInfo || uiState.profilePublicYn != profilePublicYn
        }

        return isEnable
    }

    private fun isValidNickname(text: String): Boolean {
        val regex = "^[a-zA-Z0-9ㄱ-ㅎ가가-힣]*$"
        val emojiRegex = "[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+"

        return regex.toRegex().matches(text) && !emojiRegex.toRegex().containsMatchIn(text)
    }

    private fun makeSnackBar(message: String): Snackbar {
        val snackBar = Snackbar.make(binding.root, "Red SnackBar", Snackbar.LENGTH_SHORT)
        val binding = LayoutSnackbarRedBinding.inflate(layoutInflater)

        @Suppress("RestrictedApi")
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout

        with(snackBarLayout) {
            removeAllViews()
            setPadding(22, 0, 22, 20)
            setBackgroundColor(
                ContextCompat.getColor(
                    this@ProfileEditActivity,
                    R.color.TRANSPARENT
                )
            )
            addView(binding.root, 0)
        }

        with(binding) {
            snackBarText.text = message
        }

        return snackBar
    }

    private fun showUpdateProfileInfoDialog(name: String, profileImageUrl: String, socialInfo: SocialInfo?, profilePublicYn: Boolean) {
        val customSetDialog = CustomSetDialog(this)
        customSetDialog.setAlertText(getString(R.string.profile_update_info))
        customSetDialog.setOnConfirmClickListener {
            lifecycleScope.launch { viewModel.updateUserInfo(name, profileImageUrl, socialInfo, profilePublicYn) }
            finish()
        }
        customSetDialog.show()
    }

    private fun setBinding() {
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}
