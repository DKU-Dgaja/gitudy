package site.gitudy.webhooks.infrastructure.gitudy.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class WebhookCommitRequest(
    @JsonProperty("repository_full_name") val repositoryFullName: String,
    @JsonProperty("commits") val commits: List<Commit>
)

data class Commit(
    @JsonProperty("commit_id") val commitId: String,
    @JsonProperty("message") val message: String,
    @JsonProperty("username") val username: String,
    @JsonProperty("commit_added") val commitAdded: List<String>,
    @JsonProperty("commit_modified") val commitModified: List<String>,
    @JsonProperty("commit_date") val commitDate: LocalDate
)