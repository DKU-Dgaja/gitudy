package com.takseha.presentation.ui.mystudy

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityAddTodoBinding
import com.takseha.presentation.databinding.LayoutDialogBinding
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
            var todoDate = closeTimeText.text.toString()

            todoTitleText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

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

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

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

            applyBtn.setOnClickListener {
                todoDate = closeTimeText.text.toString()
                showAddTodoDialog(studyInfoId, title, detail, todoLink, todoDate)
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
                closeTimeText.text = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                closeTimeText.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.GS_900
                    )
                )
            },
            year, month, day
        )
        datePickerDialog.show()
    }


    private fun showAddTodoDialog(studyInfoId: Int, title: String, todoLink: String, detail: String, todoDate: String) {
        val dialogBinding = LayoutDialogBinding.inflate(layoutInflater)
        val dialogView = dialogBinding.root
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttributes = dialog.window?.attributes
        windowAttributes?.dimAmount = 0.8f
        dialog.window?.attributes = windowAttributes
        val confirmBtn = dialogBinding.confirmBtn
        val cancelBtn = dialogBinding.cancelBtn

        confirmBtn.setOnClickListener {
            viewModel.makeNewTodo(studyInfoId, title, detail, todoLink, todoDate)
            dialog.dismiss()
            finish()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun setBinding() {
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}