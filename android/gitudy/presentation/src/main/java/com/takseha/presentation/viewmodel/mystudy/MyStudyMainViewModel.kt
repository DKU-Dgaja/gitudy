package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.StudyRankResponse
import com.takseha.data.dto.mystudy.Comment
import com.takseha.data.dto.mystudy.CommentRequest
import com.takseha.data.dto.mystudy.StudyConvention
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.data.dto.mystudy.StudyMember
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyStudyMainViewModel() : BaseViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyMemberRepository = GitudyMemberRepository()

    private val _myStudyState = MutableStateFlow(MyStudyMainInfoState())
    val myStudyState = _myStudyState.asStateFlow()

    private val _commentState = MutableStateFlow<List<Comment>?>(null)
    val commentState = _commentState.asStateFlow()

    suspend fun getMyStudyInfo(studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getStudyInfo(studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val myStudyInfo = response.body()!!

                    _myStudyState.update {
                        it.copy(
                            myStudyInfo = myStudyInfo,
                            conventionInfo = null
                        )
                    }
                } else {
                    Log.e(
                        "MyStudyMainViewModel",
                        "myStudyInfoResponse status: ${response.code()}\nmyStudyInfoResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    suspend fun getStudyComments(studyInfoId: Int, limit: Long) {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getStudyComments(studyInfoId, null, limit) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _commentState.value = response.body()?.studyCommentList ?: emptyList()
                } else {
                    Log.e(
                        "MyStudyMainViewModel",
                        "studyCommentListResponse status: ${response.code()}\nstudyCommentListResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }

    suspend fun getUrgentTodo(studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getTodoProgress(studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val urgentTodo = response.body()!!.todo

                    if (urgentTodo != null) {
                        _myStudyState.update {
                            it.copy(
                                isUrgentTodo = true,
                                todoInfo = urgentTodo
                            )
                        }
                    } else {
                        _myStudyState.update {
                            it.copy(
                                isUrgentTodo = false
                            )
                        }
                    }
                } else {
                    Log.e(
                        "MyStudyMainViewModel",
                        "urgentTodoResponse status: ${response.code()}\nurgentTodoResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    private suspend fun getConvention(studyInfoId: Int): StudyConvention? {
        val conventionInfoResponse = gitudyStudyRepository.getConvention(
            studyInfoId
        )

        if (conventionInfoResponse.isSuccessful) {
            val conventionInfo = conventionInfoResponse.body()!!
            Log.d("MyStudyMainViewModel", "conventionInfo: $conventionInfo")

            if (conventionInfo.studyConventionList.isNotEmpty()) {
                return conventionInfo.studyConventionList.first()
            } else {
                Log.d("MyStudyMainViewModel", "No Convention")
                return null
            }
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "conventionInfoResponse status: ${conventionInfoResponse.code()}\nconventionInfoResponse message: ${
                    conventionInfoResponse.errorBody()?.string()
                }"
            )
        }
        // 에러 발생 시 null return
        Log.d("MyStudyMainViewModel", "Error")
        return null
    }

    suspend fun getStudyMemberList(studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyMemberRepository.getStudyMemberList(studyInfoId, true) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val studyMemberList = response.body()!!

                    _myStudyState.update {
                        it.copy(
                            studyMemberListInfo = studyMemberList
                        )
                    }
                } else {
                    Log.e(
                        "MyStudyMainViewModel",
                        "studyMemberListResponse status: ${response.code()}\nstudyMemberListResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    suspend fun getStudyRankAndScore(studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getStudyRank(studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val studyRankAndScore = response.body()!!

                    _myStudyState.update {
                        it.copy(
                            rankAndScore = studyRankAndScore
                        )
                    }
                } else {
                    Log.e(
                        "MyStudyMainViewModel",
                        "studyRankAndScoreResponse status: ${response.code()}\nstudyRankAndScoreResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    suspend fun makeStudyComment(studyInfoId: Int, content: String, limit: Long) {
        val request = CommentRequest(content)
        safeApiCall(
            apiCall = { gitudyStudyRepository.makeStudyComment(studyInfoId, request) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    viewModelScope.launch {
                        getStudyComments(studyInfoId, limit)
                    }
                    Log.d("MyStudyMainViewModel", response.code().toString())
                } else {
                    Log.e(
                        "MyStudyMainViewModel",
                        "newStudyCommentResponse status: ${response.code()}\nnewStudyCommentResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }
}

data class MyStudyMainInfoState(
    val myStudyInfo: StudyInfoResponse = StudyInfoResponse(),
    val todoInfo: Todo? = null,
    val conventionInfo: StudyConvention? = null,
    val studyMemberListInfo: List<StudyMember> = listOf(),
    val rankAndScore: StudyRankResponse = StudyRankResponse(0, 0),
    val isUrgentTodo: Boolean = true
)

data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)