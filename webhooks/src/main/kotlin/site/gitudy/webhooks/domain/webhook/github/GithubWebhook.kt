package site.gitudy.webhooks.domain.webhook.github

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document("github_webhook")
data class GithubWebhook(
    @Id val id: String? = null,
    @Field("repository") val repository: GithubRepository,
    @Field("pusher") val pusher: GithubPusher,
    @Field("commits") val commits: List<GithubCommit>,
    @Field("metadata") val metadata: GithubMetadata,
) {
    companion object {
        fun of(
            metadata: GithubMetadata,
            gitHubPushEvent: GitHubPushEvent
        ): GithubWebhook {
            val githubRepository = GithubRepository(
                id = gitHubPushEvent.repository.id,
                fullName = gitHubPushEvent.repository.fullName,
                private = gitHubPushEvent.repository.private,
                description = gitHubPushEvent.repository.description,
                fork = gitHubPushEvent.repository.fork,
                url = gitHubPushEvent.repository.url,
                svnUrl = gitHubPushEvent.repository.svnUrl,
                stargazersCount = gitHubPushEvent.repository.stargazersCount,
                language = gitHubPushEvent.repository.language,
                disabled = gitHubPushEvent.repository.disabled,
                topics = gitHubPushEvent.repository.topics,
                visibility = gitHubPushEvent.repository.visibility
            )

            val githubPusher = GithubPusher(
                name = gitHubPushEvent.pusher.name,
                email = gitHubPushEvent.pusher.email
            )

            val githubCommits = gitHubPushEvent.commits.map {
                GithubCommit(
                    id = it.id,
                    treeId = it.treeId,
                    distinct = it.distinct,
                    message = it.message,
                    timestamp = it.timestamp,
                    url = it.url,
                    author = GithubCommitAuthor(
                        name = it.author.name,
                        email = it.author.email,
                        username = it.author.username
                    ),
                    committer = GithubCommitCommitter(
                        name = it.committer.name,
                        email = it.committer.email,
                        username = it.committer.username
                    ),
                    added = it.added,
                    removed = it.removed,
                    modified = it.modified
                )
            }
            return GithubWebhook(repository = githubRepository, pusher = githubPusher, commits = githubCommits, metadata = metadata)
        }
    }
}

data class GithubRepository(
    @Field("id") val id: Long,
    @Field("full_name") val fullName: String,
    @Field("private") val private: Boolean,
    @Field("description") val description: String?,
    @Field("fork") val fork: Boolean,
    @Field("url") val url: String,
    @Field("svn_url") val svnUrl: String?,
    @Field("stargazers_count") val stargazersCount: Int,
    @Field("language") val language: String?,
    @Field("disabled") val disabled: Boolean,
    @Field("topics") val topics: List<String>,
    @Field("visibility") val visibility: String,
)

data class GithubPusher(
    @Field("name") val name: String,
    @Field("email") val email: String,
)

data class GithubCommit(
    @Field("id") val id: String,
    @Field("tree_id") val treeId: String,
    @Field("distinct") val distinct: Boolean,
    @Field("message") val message: String,
    @Field("timestamp") val timestamp: String,
    @Field("url") val url: String,
    @Field("author") val author: GithubCommitAuthor,
    @Field("committer") val committer: GithubCommitCommitter,
    @Field("added") val added: List<String>,
    @Field("removed") val removed: List<String>,
    @Field("modified") val modified: List<String>,
)

data class GithubCommitAuthor(
    @Field("name") val name: String,
    @Field("email") val email: String,
    @Field("username") val username: String,
)

data class GithubCommitCommitter(
    @Field("name") val name: String,
    @Field("email") val email: String,
    @Field("username") val username: String,
)

data class GithubMetadata(
    @Field("github_delivery") val githubDelivery: String,
    @Field("github_event") val githubEvent: String,
    @Field("github_hook_id") val githubHookId: Long,
    @Field("github_target_id") val githubTargetId: Long,
    @Field("github_target_type") val githubTargetType: String,
)