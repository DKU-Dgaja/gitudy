package com.takseha.data.api.github

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApi {
    @GET("/users/{githubId}")
    suspend fun checkCorrectGithubId(
        @Path("githubId") githubId: String
    ): Response<Boolean>
}