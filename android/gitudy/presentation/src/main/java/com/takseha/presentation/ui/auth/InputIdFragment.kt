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
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentInputIdBinding
import com.takseha.presentation.firebase.MyFirebaseMessagingService
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.viewmodel.auth.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InputIdFragment : Fragment() {
    private var _binding: FragmentInputIdBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputIdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.BACKGROUND)
        setupUI(view)

        with(binding) {
            inputIdEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val githubIdLength = inputIdEditText.length()

                    isIdOkBtn.visibility = VISIBLE
                    validationCheckedImg.visibility = GONE
                    idCheckText.text = ""   // text 초기화
                    confirmBtn.isEnabled = false    // 확인 버튼 초기화

                    isIdOkBtn.isEnabled = githubIdLength > 0
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            isIdOkBtn.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    var githubId = inputIdEditText.text.toString()

                    viewModel.resetCorrectId()
                    viewModel.checkGithubId(githubId)
                    viewModel.isCorrectId.collectLatest {
                        if (it != null) {
                            if (it) {
                                idCheckText.apply {
                                    text = getString(R.string.alert_id_ok)
                                    setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.GS_500
                                        )
                                    )
                                }
                                isIdOkBtn.visibility = GONE
                                validationCheckedImg.visibility = VISIBLE
                                confirmBtn.isEnabled = true
                            } else {
                                idCheckText.apply {
                                    text = getString(R.string.alert_id_not_ok)
                                    setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.BASIC_RED
                                        )
                                    )
                                }
                                validationCheckedImg.visibility = GONE
                                confirmBtn.isEnabled = false
                            }
                        }
                    }
                }
            }

            confirmBtn.setOnClickListener { view ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val fcmToken = MyFirebaseMessagingService.getFirebaseToken().toString()
                    viewModel.apply {
                        setGithubId(inputIdEditText.text.toString())
                        setFCMToken(fcmToken)
                        getRegisterTokens()
                    }
                    view.findNavController()
                        .navigate(R.id.action_inputIdFragment_to_loginCompleteFragment)
                }
            }
        }
    }

    private fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { _, event ->
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