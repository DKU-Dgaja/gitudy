package com.takseha.data.dto.profile

import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.RepositoryInfo
import java.io.Serializable

data class CommitWithStudyName(
    val studyName: String = "",
    val studyRepo: RepositoryInfo? = null,
    val commit: Commit? = null,
): Serializable
