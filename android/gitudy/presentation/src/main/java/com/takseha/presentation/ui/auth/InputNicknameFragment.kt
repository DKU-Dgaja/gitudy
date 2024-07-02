package com.takseha.presentation.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentInputNicknameBinding
import com.takseha.presentation.databinding.LayoutSnackbarRedBinding
import com.takseha.presentation.viewmodel.auth.RegisterViewModel
import kotlinx.coroutines.launch

class InputNicknameFragment : Fragment() {
    private var _binding: FragmentInputNicknameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by activityViewModels()
    private val maxLength = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.BACKGROUND)
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

        // registerInfoState 업데이트
        val args: InputNicknameFragmentArgs by navArgs()
        viewModel.setPushAlarmYn(args.pushAlarmYn)
        Log.d("InputNicknameFragment", viewModel.registerInfoState.value.toString())

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
                    val snackBarText = getString(R.string.alert_text_length)

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
                            makeSnackBar(String.format(snackBarText, maxLength)).apply {
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
                    viewModel.checkNickname(name)

                    val isCorrectName = viewModel.isCorrectName.value
                    Log.e("InputNicknameFragment", isCorrectName.toString())
                    if (isCorrectName == true) {
                        nicknameLengthWithMax.apply {
                            text = getString(R.string.alert_name_ok)
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.GS_500
                                ))
                        }
                        confirmBtn.isEnabled = true
                    } else {
                        nicknameLengthWithMax.apply {
                            text = getString(R.string.alert_name_not_ok)
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.BASIC_RED
                                ))
                        }
                        confirmBtn.isEnabled = false
                    }
                }
            }

            confirmBtn.setOnClickListener {
                viewModel.setNickname(inputNicknameEditText.text.toString())
                Log.d(
                    "InputNicknameFragment", viewModel.registerInfoState.value.toString())
                it.findNavController()
                    .navigate(R.id.action_inputNicknameFragment_to_inputIdFragment)
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}