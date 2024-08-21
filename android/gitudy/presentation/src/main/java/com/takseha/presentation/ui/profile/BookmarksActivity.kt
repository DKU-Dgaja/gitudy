package com.takseha.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.data.dto.profile.Bookmark
import com.takseha.presentation.R
import com.takseha.presentation.adapter.BookmarkListRVAdapter
import com.takseha.presentation.adapter.FeedRVAdapter
import com.takseha.presentation.databinding.ActivityBookmarksBinding
import com.takseha.presentation.ui.feed.StudyApplyActivity
import com.takseha.presentation.viewmodel.mystudy.BookmarkWithStatus
import com.takseha.presentation.viewmodel.mystudy.BooktmarksViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BookmarksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarksBinding
    private val viewModel: BooktmarksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)
        setBinding()

        lifecycleScope.launch {
            viewModel.getBookmarks(null, 10)
            viewModel.uiState.collectLatest {
                if (!it.isBookmarksEmpty) {
                    binding.isNoBookmarkLayout.visibility = View.GONE
                    setBookmarkList(it.bookmarksWithStatusInfo)
                } else {
                    binding.isNoBookmarkLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setBookmarkList(bookmarks: List<BookmarkWithStatus>) {
        with(binding) {
            val bookmarkListRVAdapter = BookmarkListRVAdapter(this@BookmarksActivity, bookmarks)

            bookmarkList.adapter = bookmarkListRVAdapter
            bookmarkList.layoutManager = LinearLayoutManager(this@BookmarksActivity)

            clickFeedItem(bookmarkListRVAdapter, bookmarks)
        }
    }

    private fun clickFeedItem(bookmarkListRVAdapter: BookmarkListRVAdapter, bookmarks: List<BookmarkWithStatus>) {
        bookmarkListRVAdapter.onClickListener = object : BookmarkListRVAdapter.OnClickListener {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(this@BookmarksActivity, StudyApplyActivity::class.java)
                intent.putExtra("studyInfoId", bookmarks[position].bookmarkInfo?.studyInfoId)
                intent.putExtra("studyImgColor", bookmarks[position].bookmarkInfo?.studyInfoWithIdResponse?.profileImageUrl)
                startActivity(intent)
            }

            override fun bookmarkClick(view: View, position: Int) {
                viewModel.setBookmarkStatus(bookmarks[position].bookmarkInfo?.studyInfoId ?: 0)
            }
        }
    }

    private fun setBinding() {
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}