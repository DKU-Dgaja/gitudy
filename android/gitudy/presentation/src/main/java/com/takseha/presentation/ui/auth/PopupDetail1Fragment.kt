package com.takseha.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentPopupDetail1Binding
import java.io.BufferedReader
import java.io.InputStreamReader

class PopupDetail1Fragment : Fragment() {
    private var _binding: FragmentPopupDetail1Binding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPopupDetail1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =  ContextCompat.getColor(requireContext(), R.color.WHITE)
        with(binding) {
            backBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }

            val termsText = loadAssetTextFile("terms_of_personal_info.txt")
            personalInfoTermBody.text = termsText
        }
    }

    private fun loadAssetTextFile(fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            val inputStream = requireContext().assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}