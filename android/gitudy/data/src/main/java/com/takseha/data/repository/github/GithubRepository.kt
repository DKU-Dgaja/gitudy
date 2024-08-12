package com.takseha.data.repository.github

import com.takseha.data.api.github.GithubService
import com.takseha.data.api.github.RetrofitInstance

class GithubRepository {
    private val client = RetrofitInstance.getInstance().create(GithubService::class.java)
    suspend fun checkCorrectGithubId(githubId: String) = client.checkCorrectGithubId(githubId)
}