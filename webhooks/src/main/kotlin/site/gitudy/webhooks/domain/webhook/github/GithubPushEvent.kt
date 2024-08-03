package site.gitudy.webhooks.domain.webhook.github

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubPushEvent(
    val repository: Repository,
    val pusher: Pusher,
    val commits: List<Commit>,
)

data class Repository(
    val id: Long,
    @JsonProperty("full_name") val fullName: String,
    val private: Boolean,
    val description: String?,
    val fork: Boolean,
    val url: String,
    @JsonProperty("svn_url") val svnUrl: String?,
    @JsonProperty("stargazers_count") val stargazersCount: Int,
    val language: String?,
    val disabled: Boolean,
    val topics: List<String>,
    val visibility: String,
)

data class Pusher(
    val name: String,
    val email: String,
)

data class Commit(
    val id: String,
    @JsonProperty("tree_id") val treeId: String,
    val distinct: Boolean,
    val message: String,
    val timestamp: String,
    val url: String,
    val author: CommitAuthor,
    val committer: CommitCommitter,
    val added: List<String>,
    val removed: List<String>,
    val modified: List<String>,
)

data class CommitAuthor(
    val name: String,
    val email: String,
    val username: String,
)

data class CommitCommitter(
    val name: String,
    val email: String,
    val username: String,
)
