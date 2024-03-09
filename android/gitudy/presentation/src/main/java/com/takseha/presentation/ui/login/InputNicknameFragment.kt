package com.takseha.presentation.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.takseha.common.model.SPKey
import com.takseha.common.util.SharedPreferences
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentInputNicknameBinding
import com.takseha.presentation.databinding.LayoutSnackbarRedBinding

class InputNicknameFragment : Fragment() {
    private var _binding: FragmentInputNicknameBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: SharedPreferences
    private val maxLength = 6

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

        prefs = SharedPreferences(requireActivity().applicationContext)

        with(binding) {
            inputNicknameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    var nicknameLength = inputNicknameEditText.length()
                    val nicknameLengthText = getString(R.string.text_length)
                    val snackBarText = getString(R.string.alert_text_length)

                    if (nicknameLength > 0) {
                        confirmBtn.isEnabled = true
                        nicknameLengthWithMax.text = String.format(nicknameLengthText, nicknameLength, maxLength)
                        if (nicknameLength > maxLength) {
                            confirmBtn.isEnabled = false
                            nicknameLengthWithMax.setTextColor(ContextCompat.getColor(requireContext(), R.color.BASIC_RED))
                            makeSnackBar(String.format(snackBarText, maxLength)).apply {
                                anchorView = confirmBtn
                            }.show()
                        } else {
                            confirmBtn.isEnabled = true
                            nicknameLengthWithMax.setTextColor(ContextCompat.getColor(requireContext(), R.color.GS_500))
                        }
                    } else {
                        confirmBtn.isEnabled = false
                        nicknameLengthWithMax.text = String.format(nicknameLengthText, nicknameLength, maxLength)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            confirmBtn.setOnClickListener {
                prefs.savePref(
                    SPKey.GITUDY_NAME,
                    inputNicknameEditText.text.toString()
                )
                Log.d(
                    "InputNicknameFragment",
                    "gitudyName: ${prefs.loadPref(SPKey.GITUDY_NAME, "0")}"
                )
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
}