package com.takseha.presentation.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentPopupDetail3Binding

class PopupDetail3Fragment : Fragment() {
    private var _binding : FragmentPopupDetail3Binding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPopupDetail3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            backBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_popupDetail3Fragment_to_popupFragment)
            }
            agreeBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("IsChecked", "detail3")
                it.findNavController().navigate(R.id.action_popupDetail3Fragment_to_popupFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}