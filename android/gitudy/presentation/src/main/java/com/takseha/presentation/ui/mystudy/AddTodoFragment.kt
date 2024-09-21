package com.takseha.presentation.ui.mystudy

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentAddTodoBinding
import com.takseha.presentation.ui.common.CustomSetDialog
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.viewmodel.mystudy.AddTodoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddTodoFragment : Fragment() {
    private var _binding: FragmentAddTodoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTodoViewModel by activityViewModels()
    private var studyInfoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studyInfoId = requireActivity().intent.getIntExtra("studyInfoId", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)

        with(binding) {
            var title = todoTitleText.text.toString()
            var detail = todoDetailText.text.toString()
            var todoLink = todoLinkText.text.toString()
            var todoDate: String

            todoTitleText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    var titleLength = todoTitleText.length()
                    val titleLengthText = getString(R.string.text_length)

                    if (titleLength > 0) {
                        titleTextLengthAlert.visibility = View.GONE
                        titleTextLength.visibility = View.VISIBLE
                        titleTextLength.text =
                            String.format(titleLengthText, titleLength, 20)
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    title = todoTitleText.text.toString()
                    applyBtn.isEnabled =
                        title.isNotEmpty() && detail.isNotEmpty()
                }
            })

            todoDetailText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    var detailLength = todoDetailText.length()
                    val detailLengthText = getString(R.string.text_length)

                    if (detailLength > 0) {
                        bodyTextLengthAlert.visibility = View.GONE
                        bodyTextLength.visibility = View.VISIBLE
                        bodyTextLength.text =
                            String.format(detailLengthText, detailLength, 50)
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    detail = todoDetailText.text.toString()
                    applyBtn.isEnabled =
                        title.isNotEmpty() && detail.isNotEmpty()
                }
            })

            todoLinkText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    todoLink = todoLinkText.text.toString()
                }
            })

            closeTimeText.text = LocalDate.now().toString()
            closeTime.setOnClickListener {
                showDatePickerDialog(closeTimeText)
            }

            backBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }

            applyBtn.setOnClickListener {
                todoDate = closeTimeText.text.toString()
                showAddTodoDialog(studyInfoId, title, todoLink, detail, todoDate)
            }
        }
    }

    private fun showDatePickerDialog(closeTimeText: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), R.style.CustomDatePicker,
            { _, selectedYear, selectedMonth, selectedDay ->
                closeTimeText.text = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                closeTimeText.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.GS_900
                    )
                )
            },
            year, month, day
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun showAddTodoDialog(studyInfoId: Int, title: String, todoLink: String, detail: String, todoDate: String) {
        val customSetDialog = CustomSetDialog(requireContext())
        customSetDialog.setAlertText(getString(R.string.to_do_add))
        customSetDialog.setOnConfirmClickListener {
            with(binding) {
                loadingIndicator.visibility = VISIBLE
                backBtn.isEnabled = false
                applyBtn.isEnabled = false
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.resetResponseState()
                viewModel.makeNewTodo(studyInfoId, title, todoLink, detail, todoDate)
                viewModel.responseState.collectLatest {
                    if (it != null) {
                        with(binding) {
                            loadingIndicator.visibility = GONE
                            backBtn.isEnabled = true
                        }
                        if (it) {
                            findNavController().popBackStack()
                        }
                        // TODO: todo 생성 실패 시 로직 구현!
                    }
                }
            }
        }
        customSetDialog.show()
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