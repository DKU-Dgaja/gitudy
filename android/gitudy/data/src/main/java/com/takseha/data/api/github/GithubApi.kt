package com.takseha.data.api.github

import com.takseha.data.dto.auth.register.GithubIdResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApi {
    @GET("/users/{githubId}")
    suspend fun checkCorrectGithubId(
        @Path("githubId") githubId: String
    ): Response<GithubIdResponse>
}