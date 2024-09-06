package com.takseha.presentation.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentInputNicknameBinding
import com.takseha.presentation.databinding.LayoutSnackbarRedBinding
import com.takseha.presentation.firebase.MyFirebaseMessagingService
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.viewmodel.auth.RegisterViewModel
import kotlinx.coroutines.launch

class InputNicknameFragment : Fragment() {
    private var _binding: FragmentInputNicknameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by activityViewModels()
    private val maxLength = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.BACKGROUND)
        setupUI(view)

        // registerInfoState 업데이트
        val args: InputNicknameFragmentArgs by navArgs()
        viewModel.setPushAlarmYn(args.pushAlarmYn)

        with(binding) {
            inputNicknameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    var nicknameLength = inputNicknameEditText.length()
                    val nicknameLengthText = getString(R.string.text_length)

                    isNameOkBtn.visibility = VISIBLE
                    validationCheckedImg.visibility = GONE
                    confirmBtn.isEnabled = false    // 확인 버튼 초기화

                    if (nicknameLength > 0) {
                        isNameOkBtn.isEnabled = true
                        nicknameLengthWithMax.text =
                            String.format(nicknameLengthText, nicknameLength, maxLength)
                        if (nicknameLength > maxLength) {
                            isNameOkBtn.isEnabled = false
                            nicknameLengthWithMax.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.BASIC_RED
                                )
                            )
                            makeSnackBar(getString(R.string.alert_text_length, maxLength)).apply {
                                anchorView = confirmBtn
                            }.show()
                        } else {
                            isNameOkBtn.isEnabled = true
                            nicknameLengthWithMax.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.GS_500
                                )
                            )
                        }
                        if (!isValidNickname(s.toString())) {
                            isNameOkBtn.isEnabled = false
                            nicknameLengthWithMax.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.BASIC_RED
                                )
                            )
                            makeSnackBar(getString(R.string.alert_text_emoji)).apply {
                                anchorView = confirmBtn
                            }.show()
                        }

                    } else {
                        isNameOkBtn.isEnabled = false
                        nicknameLengthWithMax.text =
                            String.format(nicknameLengthText, nicknameLength, maxLength)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            isNameOkBtn.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    var name = inputNicknameEditText.text.toString()

                    viewModel.resetCorrectName()
                    viewModel.checkNickname(name)
                    viewModel.isCorrectName.collect {
                        if (it != null) {
                            if (it) {
                                nicknameLengthWithMax.apply {
                                    text = getString(R.string.alert_name_ok)
                                    setTextColor(
                                        ContextCompat.getColor(requireContext(), R.color.GS_500)
                                    )
                                }
                                isNameOkBtn.visibility = GONE
                                validationCheckedImg.visibility = VISIBLE
                                confirmBtn.isEnabled = true
                            } else {
                                nicknameLengthWithMax.apply {
                                    text = getString(R.string.alert_name_not_ok)
                                    setTextColor(
                                        ContextCompat.getColor(requireContext(), R.color.BASIC_RED)
                                    )
                                }
                                validationCheckedImg.visibility = GONE
                                confirmBtn.isEnabled = false
                            }
                        }
                    }
                }
            }

            confirmBtn.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val fcmToken = MyFirebaseMessagingService.getFirebaseToken().toString()
                    viewModel.apply {
                        setNickname(inputNicknameEditText.text.toString())
                        setFCMToken(fcmToken)
                        getRegisterTokens()
                    }
                    view.findNavController()
                        .navigate(R.id.action_inputNicknameFragment_to_loginCompleteFragment)
                }
            }
        }
    }

    private fun isValidNickname(text: String): Boolean {
        val regex = "^[a-zA-Z0-9ㄱ-ㅎ가가-힣]*$"
        val emojiRegex = "[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+"

        return regex.toRegex().matches(text) && !emojiRegex.toRegex().containsMatchIn(text)
    }

    private fun makeSnackBar(message: String): Snackbar {
        val snackBar = Snackbar.make(requireView(), "Red SnackBar", Snackbar.LENGTH_SHORT)
        val binding = LayoutSnackbarRedBinding.inflate(layoutInflater)

        @Suppress("RestrictedApi")
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout

        with(snackBarLayout) {
            removeAllViews()
            setPadding(22, 0, 22, 20)
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.TRANSPARENT))
            addView(binding.root, 0)
        }

        with(binding) {
            snackBarText.text = message
        }

        return snackBar
    }

    private fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    activity?.let { KeyboardUtils.hideKeyboard(it) }
                }
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}