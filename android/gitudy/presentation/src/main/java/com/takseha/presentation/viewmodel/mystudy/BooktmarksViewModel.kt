package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.Category
import com.takseha.data.dto.profile.Bookmark
import com.takseha.data.repository.gitudy.GitudyBookmarksRepository
import com.takseha.data.repository.gitudy.GitudyCategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BooktmarksViewModel: ViewModel() {
    private var gitudyBookmarksRepository: GitudyBookmarksRepository = GitudyBookmarksRepository()
    private var gitudyCategoryRepository: GitudyCategoryRepository = GitudyCategoryRepository()

    private var _uiState = MutableStateFlow(BookmarksUiState())
    val uiState = _uiState.asStateFlow()

    private var _cursorIdxRes = MutableLiveData<Long?>()
    val cursorIdxRes: LiveData<Long?>
        get() = _cursorIdxRes

    fun getBookmarks(cursorIdx: Long?, limit: Long) = viewModelScope.launch {
        val bookmarksResponse = gitudyBookmarksRepository.getBookmarks(
            cursorIdx,
            limit
        )
        if (bookmarksResponse.isSuccessful) {
            val bookmarksInfo = bookmarksResponse.body()!!

            _cursorIdxRes.value = bookmarksInfo.cursorIdx
            Log.d("BooktmarksViewModel", _cursorIdxRes.value.toString())

            if (bookmarksInfo.bookmarkInfoList.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isBookmarksEmpty = true,
                    )
                }
            } else {
                val bookmarksWithStatus = bookmarksInfo.bookmarkInfoList.map { bookmark ->
                    val bookmarkStatus = checkBookmarkStatus(bookmark.studyInfoId)
                    BookmarkWithStatus(
                        bookmark,
                        bookmarkStatus ?: false
                    )
                }
                _uiState.update { it.copy(
                    bookmarksWithStatusInfo = bookmarksWithStatus,
                    isBookmarksEmpty = false
                ) }
            }
        } else {
            Log.e(
                "BooktmarksViewModel",
                "bookmarksResponse status: ${bookmarksResponse.code()}\nbookmarksResponse message: ${bookmarksResponse.message()}"
            )
        }
    }

    private suspend fun checkBookmarkStatus(studyInfoId: Int): Boolean? {
        val bookmarkStatusResponse = gitudyBookmarksRepository.checkBookmarkStatus(
            studyInfoId
        )
        if (bookmarkStatusResponse.isSuccessful) {
            return bookmarkStatusResponse.body()!!.myBookmark
        } else {
            Log.e(
                "BooktmarksViewModel",
                "bookmarkStatusResponse status: ${bookmarkStatusResponse.code()}\nbookmarkStatusResponse message: ${bookmarkStatusResponse.message()}"
            )
        }
        return null
    }

    fun setBookmarkStatus(studyInfoId: Int) = viewModelScope.launch {
        val setBookmarkResponse = gitudyBookmarksRepository.setBookmarkStatus(
            studyInfoId
        )

        if (setBookmarkResponse.isSuccessful) {
            Log.d("BooktmarksViewModel", setBookmarkResponse.code().toString())
        } else {
            Log.e(
                "BooktmarksViewModel",
                "setBookmarkResponse status: ${setBookmarkResponse.code()}\nsetBookmarkResponse message: ${setBookmarkResponse.errorBody()?.string()}"
            )
        }
    }
}

data class BookmarksUiState(
    var bookmarksWithStatusInfo: List<BookmarkWithStatus> = listOf(),
    var isBookmarksEmpty: Boolean = false
)

data class BookmarkWithStatus(
    var bookmarkInfo: Bookmark? = null,
    var isMyBookmark: Boolean = false,
    var category: List<Category> = listOf()
)