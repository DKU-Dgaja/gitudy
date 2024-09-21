package com.takseha.presentation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentDeleteAccountBinding
import com.takseha.presentation.ui.common.CustomCheckDialog
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.viewmodel.profile.SettingHomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeleteAccountFragment : Fragment() {
    private var _binding: FragmentDeleteAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingHomeViewModel by activityViewModels()
    private lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)

        with(binding) {
            backBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }

            deleteReasonSelectRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                deleteAccountBtn.isEnabled = true
                newReasonEditText.visibility = if (checkedId == reason4CheckBtn.id) VISIBLE else GONE
            }

            deleteAccountBtn.setOnClickListener {
                message = setMessage()
                showDeleteAccountDialog(message)
            }
            cancelBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
        }
    }

    private fun setMessage(): String {
        return with(binding) {
            when {
                reason1CheckBtn.isChecked -> getString(R.string.profile_delete_account_reason1)
                reason2CheckBtn.isChecked -> getString(R.string.profile_delete_account_reason2)
                reason3CheckBtn.isChecked -> getString(R.string.profile_delete_account_reason3)
                else -> newReasonEditText.text.toString()
            }
        }
    }

    private fun showDeleteAccountDialog(message: String) {
        val customCheckDialog = CustomCheckDialog(requireContext())
        customCheckDialog.setAlertText(getString(R.string.alert_delete_account_title))
        customCheckDialog.setAlertDetailText(getString(R.string.alert_delete_account_detail))
        customCheckDialog.setCancelBtnText(getString(R.string.alert_logout_cancel))
        customCheckDialog.setConfirmBtnText(getString(R.string.alert_delete_account_confirm))
        customCheckDialog.setOnConfirmClickListener {
            with(binding) {
                loadingIndicator.visibility = VISIBLE
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.deleteUserAccount(message)
                viewModel.deleteResponseState.collectLatest {
                    if (it != null) {
                        with(binding) {
                            loadingIndicator.visibility = GONE
                            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                        if (it) {
                            findNavController().navigate(R.id.action_deleteAccountFragment_to_deleteAccountCompleteFragment)
                        }
                        // TODO: todo 생성 실패 시 로직 구현!
                    }
                }
            }
        }
        customCheckDialog.show()
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