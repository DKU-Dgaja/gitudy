package com.takseha.presentation.ui.mystudy

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentUpdateTodoBinding
import com.takseha.presentation.ui.common.CustomDialog
import com.takseha.presentation.viewmodel.mystudy.TodoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class UpdateTodoFragment : Fragment() {
    private var _binding: FragmentUpdateTodoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val studyInfoId = activity?.intent?.getIntExtra("studyInfoId", 0) ?: 0
        val todoId = requireArguments().getInt("todoId")

        viewModel.getTodo(studyInfoId, todoId)
        lifecycleScope.launch {
            viewModel.todoState.collectLatest {
                with(binding) {
                    todoTitleText.setText(it.title)
                    todoLinkText.setText(it.todoLink)
                    todoDetailText.setText(it.detail)
                    closeTimeText.text = it.todoDate

                    applyBtn.isEnabled = false
                }
            }
        }

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

            closeTime.setOnClickListener {
                showDatePickerDialog(closeTimeText)
            }

            closeTimeText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    applyBtn.isEnabled =
                        title.isNotEmpty() && detail.isNotEmpty() && closeTimeText.text.isNotEmpty()
                }
            })


            backBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }

            applyBtn.setOnClickListener {
                todoDate = closeTimeText.text.toString()
                Log.d("AddTodoActivity", todoDate)
                showUpdateTodoDialog(studyInfoId, todoId, title, todoLink, detail, todoDate)
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

    private fun showUpdateTodoDialog(studyInfoId: Int, todoId: Int, title: String, todoLink: String, detail: String, todoDate: String) {
        val customDialog = CustomDialog(requireContext())
        customDialog.setAlertText(getString(R.string.to_do_update))
        customDialog.setOnConfirmClickListener {
            viewModel.updateTodo(studyInfoId, todoId, title, todoLink, detail, todoDate)
            view?.findNavController()?.popBackStack()
        }
        customDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}