package com.takseha.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.profile.Bookmark
import com.takseha.presentation.R
import com.takseha.presentation.adapter.BookmarkListRVAdapter
import com.takseha.presentation.databinding.ActivityBookmarksBinding
import com.takseha.presentation.ui.common.SnackBarHelper
import com.takseha.presentation.ui.feed.StudyApplyActivity
import com.takseha.presentation.viewmodel.profile.ProfileHomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BookmarksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarksBinding
    private val viewModel: ProfileHomeViewModel by viewModels()
    private lateinit var snackBarHelper: SnackBarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)
        setBinding()

        snackBarHelper = SnackBarHelper(this)
        lifecycleScope.launch {
            viewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        viewModel.resetSnackbarMessage()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getBookmarks(null, 50)
            viewModel.bookmarksState.collectLatest {
                if (!it.isBookmarksEmpty) {
                    binding.isNoBookmarkLayout.visibility = View.GONE
                } else {
                    binding.isNoBookmarkLayout.visibility = View.VISIBLE
                }
                setBookmarkList(it.bookmarksInfo)
            }
        }
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setBookmarkList(bookmarks: List<Bookmark>) {
        with(binding) {
            val bookmarkListRVAdapter = BookmarkListRVAdapter(this@BookmarksActivity, bookmarks)

            bookmarkList.adapter = bookmarkListRVAdapter
            bookmarkList.layoutManager = LinearLayoutManager(this@BookmarksActivity)

            clickBookmarkItem(bookmarkListRVAdapter, bookmarks)
        }
    }

    private fun clickBookmarkItem(bookmarkListRVAdapter: BookmarkListRVAdapter, bookmarks: List<Bookmark>) {
        bookmarkListRVAdapter.onClickListener = object : BookmarkListRVAdapter.OnClickListener {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(this@BookmarksActivity, StudyApplyActivity::class.java)
                intent.putExtra("studyInfoId", bookmarks[position].studyInfoId)
                intent.putExtra("studyImgColor", bookmarks[position].studyInfoWithIdResponse.profileImageUrl)
                intent.putExtra("studyStatus", bookmarks[position].studyInfoWithIdResponse.status)
                startActivity(intent)
            }

            override fun bookmarkClick(view: View, position: Int) {
                lifecycleScope.launch {
                    viewModel.setBookmarkStatus(bookmarks[position].studyInfoId, null, 50)
                }
            }
        }
    }

    private fun setBinding() {
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}