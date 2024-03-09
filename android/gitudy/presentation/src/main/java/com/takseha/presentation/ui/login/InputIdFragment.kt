package com.takseha.presentation.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.takseha.common.model.SPKey
import com.takseha.common.util.SharedPreferences
import com.takseha.presentation.databinding.FragmentInputIdBinding

class InputIdFragment : Fragment() {
    private var _binding: FragmentInputIdBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: SharedPreferences

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

        prefs = SharedPreferences(requireActivity().applicationContext)

        with(binding) {
            inputIdEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    confirmBtn.isEnabled = inputIdEditText.length() > 0
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            confirmBtn.setOnClickListener {
                prefs.savePref(
                    SPKey.GITHUB_ID,
                    inputIdEditText.text.toString()
                )
                Log.d(
                    "InputIdFragment",
                    "githubId: ${prefs.loadPref(SPKey.GITHUB_ID, "0")}"
                )

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}