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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentInputIdBinding
import com.takseha.presentation.viewmodel.RegisterViewModel

class InputIdFragment : Fragment() {
    private var _binding: FragmentInputIdBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: SP
    private lateinit var viewModel: RegisterViewModel

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
        prefs = SP(requireActivity().applicationContext)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        with(binding) {
            inputIdEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val githubIdLength = inputIdEditText.length()

                    isIdOkBtn.isEnabled = githubIdLength > 0
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            isIdOkBtn.setOnClickListener {
                var githubId = inputIdEditText.text.toString()

                viewModel.checkGithubId(githubId)
                viewModel.isCorrectId.observe(viewLifecycleOwner) {
                    Log.e("InputIdFragment", "$it")
                    if (it) {
                        idCheckText.apply {
                            text = getString(R.string.alert_id_ok)
                            setTextColor(
                                ContextCompat.getColor(
                                requireContext(),
                                R.color.GS_500
                            ))
                        }
                        confirmBtn.isEnabled = true
                    } else {
                        idCheckText.apply {
                            text = getString(R.string.alert_id_not_ok)
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

            confirmBtn.setOnClickListener { view ->
                prefs.savePref(
                    SPKey.GITHUB_ID,
                    inputIdEditText.text.toString()
                )
                Log.d(
                    "InputIdFragment",
                    "githubId: ${prefs.loadPref(SPKey.GITHUB_ID, "0")}"
                )
                viewModel.getRegisterTokens()
                view.findNavController()
                    .navigate(R.id.action_inputIdFragment_to_loginCompleteFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}