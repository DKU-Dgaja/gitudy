package com.takseha.presentation.ui.mystudy

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityAddTodoBinding
import com.takseha.presentation.ui.common.CustomDialog
import com.takseha.presentation.viewmodel.mystudy.AddTodoViewModel
import java.time.LocalDate

class AddTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTodoBinding
    private val viewModel: AddTodoViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)
        setBinding()
        window.statusBarColor = ContextCompat.getColor(this, R.color.BACKGROUND)

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)

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
                finish()
            }

            applyBtn.setOnClickListener {
                todoDate = closeTimeText.text.toString()
                Log.d("AddTodoActivity", todoDate)
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
            this, R.style.CustomDatePicker,
            { _, selectedYear, selectedMonth, selectedDay ->
                closeTimeText.text = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                closeTimeText.setTextColor(
                    ContextCompat.getColor(
                        this,
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
        val customDialog = CustomDialog(this)
        customDialog.setAlertText(getString(R.string.to_do_add))
        customDialog.setOnConfirmClickListener {
            viewModel.makeNewTodo(studyInfoId, title, todoLink, detail, todoDate)
            finish()
        }
        customDialog.show()
    }

    private fun setBinding() {
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}