package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.StudyCountResponse
import com.takseha.data.dto.feed.StudyRankResponse
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.data.dto.mystudy.StudyConvention
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.data.dto.mystudy.StudyMember
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyStudyMainViewModel() : BaseViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyMemberRepository = GitudyMemberRepository()

    private val _myStudyState = MutableStateFlow(MyStudyMainInfoState())
    val myStudyState = _myStudyState.asStateFlow()

    private val _commentState = MutableStateFlow<List<StudyComment>>(emptyList())
    val commentState = _commentState.asStateFlow()

    fun getMyStudyInfo(studyInfoId: Int) = viewModelScope.launch {
        val result = safeApiResponse {
            val myStudyInfoResponse = async { gitudyStudyRepository.getStudyInfo(studyInfoId) }
            val urgentTodo = async { getUrgentTodo(studyInfoId) }
            val rankAndScore = async { getStudyRank(studyInfoId)!! }
            val convention = null
            val studyMemberList = async { getStudyMemberList(studyInfoId) }
            Quintuple(
                myStudyInfoResponse.await(),
                urgentTodo.await(),
                rankAndScore.await(),
                convention,
                studyMemberList.await()
            )
        }
        result?.let { (
                          myStudyInfoResponse,
                          urgentTodo,
                          rankAndScore,
                          convention,
                          studyMemberList
                      ) ->
            if (myStudyInfoResponse.isSuccessful) {
                val myStudyInfo = myStudyInfoResponse.body()!!

                if (urgentTodo == null) {
                    _myStudyState.update {
                        it.copy(
                            myStudyInfo = myStudyInfo,
                            todoInfo = urgentTodo,
                            conventionInfo = convention,
                            studyMemberListInfo = studyMemberList,
                            rankAndScore = rankAndScore,
                            isUrgentTodo = false
                        )
                    }
                } else {
                    _myStudyState.update {
                        it.copy(
                            myStudyInfo = myStudyInfo,
                            todoInfo = urgentTodo,
                            conventionInfo = convention,
                            studyMemberListInfo = studyMemberList,
                            rankAndScore = rankAndScore,
                            isUrgentTodo = true
                        )
                    }
                }
                Log.d("MyStudyMainViewModel", "_myStudyState: ${_myStudyState.value}")
            } else {
                Log.e(
                    "MyStudyMainViewModel",
                    "myStudyInfoResponse status: ${myStudyInfoResponse.code()}\nmyStudyInfoResponse message: ${
                        myStudyInfoResponse.errorBody()?.string()
                    }"
                )
            }
        } ?: Log.e("MyStudyMainViewModel", "API 호출 실패")
    }

    fun getStudyComments(studyInfoId: Int, limit: Long) = viewModelScope.launch {
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

    private suspend fun getUrgentTodo(studyInfoId: Int): Todo? {
        return try {
            val response = gitudyStudyRepository.getTodoProgress(
                studyInfoId
            )
            if (response.isSuccessful) {
                response.body()?.todo
            } else {
                Log.e(
                    "MyStudyMainViewModel",
                    "urgentTodoResponse status: ${response.code()}\nurgentTodoResponse message: ${
                        response.errorBody()?.string()
                    }"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("MyStudyMainViewModel", "Error fetching getUserInfo()", e)
            null
        }
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

    private suspend fun getStudyMemberList(studyInfoId: Int): List<StudyMember> {
        return try {
            var response =  gitudyMemberRepository.getStudyMemberList(studyInfoId, true)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                Log.e(
                    "MyStudyMainViewModel",
                    "studyMemberListResponse status: ${response.code()}\nstudyMemberListResponse message: ${
                        response.errorBody()?.string()
                    }"
                )
                listOf()
            }
        } catch (e: Exception) {
            Log.e("ProfileHomeViewModel", "Error fetching getUserInfo()", e)
            listOf()
        }
    }

    private suspend fun getStudyRank(studyInfoId: Int): StudyRankResponse? {
        return try {
            val response = gitudyStudyRepository.getStudyRank(studyInfoId)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                Log.e(
                    "MyStudyMainViewModel",
                    "studyRankResponse status: ${response.code()}\nstudyRankResponse message: ${
                        response.errorBody()?.string()
                    }"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("MyStudyMainViewModel", "Error fetching getUserInfo()", e)
            null
        }
    }

    suspend fun makeStudyComment(studyInfoId: Int, content: String, limit: Long) {
        safeApiCall(
            apiCall = { gitudyStudyRepository.makeStudyComment(studyInfoId, content) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    getStudyComments(studyInfoId, limit)
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