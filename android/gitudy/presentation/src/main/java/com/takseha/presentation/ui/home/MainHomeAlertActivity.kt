package com.takseha.presentation.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.home.Notice
import com.takseha.presentation.R
import com.takseha.presentation.adapter.NoticeListRVAdapter
import com.takseha.presentation.databinding.ActivityMainHomeAlertBinding
import com.takseha.presentation.ui.mystudy.MyStudyMainActivity
import com.takseha.presentation.viewmodel.home.MainHomeAlertViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainHomeAlertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainHomeAlertBinding
    private val viewModel: MainHomeAlertViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home_alert)
        window.statusBarColor = ContextCompat.getColor(this, R.color.BACKGROUND)

        viewModel.getNoticeList(null, 50)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                setNoticeList(it)
            }
        }
    }

    private fun setNoticeList(noticeList: List<Notice>) {
        with(binding) {
            val noticeListRVAdapter = NoticeListRVAdapter(this@MainHomeAlertActivity, noticeList)
            alertList.adapter = noticeListRVAdapter
            alertList.layoutManager = LinearLayoutManager(this@MainHomeAlertActivity)

            clickNoticeItem(noticeListRVAdapter, noticeList)
        }
    }

    private fun clickNoticeItem(noticeListRVAdapter: NoticeListRVAdapter, noticeList: List<Notice>) {
        noticeListRVAdapter.onClickListener = object : NoticeListRVAdapter.OnClickListener {
            override fun onClick(view: View, position: Int) {
                val notice = noticeList[position]

                if (notice.title.contains("신청")) {   // 스터디 가입 신청
                    // TODO: 스터디 가입신청 리스트로 이동
                } else if (notice.title.contains("완료")) {    // 스터디 가입 완료
                    // 해당 스터디 상세 페이지로 이동
                    val intent = Intent(this@MainHomeAlertActivity, MyStudyMainActivity::class.java)
                    intent.putExtra("studyInfoId", noticeList[position].studyInfoId)
                } else if (notice.title.contains("업데이트")) {  // 스터디 TO-DO 업데이트
                    val intent = Intent(this@MainHomeAlertActivity, MyStudyMainActivity::class.java)
                    intent.putExtra("studyInfoId", noticeList[position].studyInfoId)
                }
                startActivity(intent)
            }
        }
    }
}