package site.gitudy.webhooks.infrastructure.gitudy.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class WebhookCommitRequest(
    @JsonProperty("commit_id") val commitId: String,
    @JsonProperty("message") val message: String,
    @JsonProperty("username") val username: String,
    @JsonProperty("repository_full_name") val repositoryFullName: String,
    @JsonProperty("commit_date") val commitDate: LocalDate
)