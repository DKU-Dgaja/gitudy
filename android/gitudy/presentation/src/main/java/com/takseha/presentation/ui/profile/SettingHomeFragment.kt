package com.takseha.presentation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentSettingHomeBinding
import com.takseha.presentation.ui.common.CustomCheckDialog
import com.takseha.presentation.viewmodel.profile.SettingHomeViewModel
import kotlinx.coroutines.launch

class SettingHomeFragment : Fragment() {
    private var _binding: FragmentSettingHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingHomeViewModel by activityViewModels()
    private var pushAlarmYn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pushAlarmYn = requireActivity().intent?.getBooleanExtra("pushAlarmYn", false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            backBtn.setOnClickListener {
                requireActivity().finish()
            }

            customerServiceLayout.setOnClickListener {
                it.findNavController().navigate(R.id.action_settingHomeFragment_to_customerServiceFragment)
            }

            pushAlertSwitch.apply {
                isChecked = pushAlarmYn
                setOnClickListener {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.updatePushAlarmYn(pushAlertSwitch.isChecked)
                    }
                }
            }

            deleteAccountLayout.setOnClickListener {
                it.findNavController().navigate(R.id.action_settingHomeFragment_to_deleteAccountFragment)
            }

            logoutBtn.setOnClickListener {
                showLogoutDialog()
            }
        }
    }

    private fun showLogoutDialog() {
        val customCheckDialog = CustomCheckDialog(requireContext())
        customCheckDialog.setAlertText(getString(R.string.alert_logout_title))
        customCheckDialog.setAlertDetailText(getString(R.string.alert_logout_detail))
        customCheckDialog.setCancelBtnText(getString(R.string.alert_logout_cancel))
        customCheckDialog.setConfirmBtnText(getString(R.string.alert_logout_confirm))
        customCheckDialog.setOnConfirmClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.logout()
                findNavController().navigate(R.id.action_settingHomeFragment_to_logoutCompleteFragment)
            }
        }
        customCheckDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}