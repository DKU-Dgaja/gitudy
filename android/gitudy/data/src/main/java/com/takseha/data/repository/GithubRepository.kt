package com.takseha.data.repository

import com.takseha.data.api.github.GithubApi
import com.takseha.data.api.gitudy.RetrofitInstance

class GithubRepository {
    private val client = RetrofitInstance.getInstance().create(GithubApi::class.java)

    suspend fun checkCorrectGithubId(githubId: String) = client.checkCorrectGithubId(githubId)
}