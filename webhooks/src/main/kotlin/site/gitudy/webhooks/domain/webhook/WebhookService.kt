package site.gitudy.webhooks.domain.webhook

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.kohsuke.github.GHEvent
import org.springframework.stereotype.Service
import site.gitudy.webhooks.domain.webhook.github.*
import site.gitudy.webhooks.infrastructure.gitudy.WebhookClient
import site.gitudy.webhooks.infrastructure.gitudy.dto.Commit
import site.gitudy.webhooks.infrastructure.gitudy.dto.WebhookCommitRequest
import site.gitudy.webhooks.utils.logger
import site.gitudy.webhooks.utils.suspendError
import site.gitudy.webhooks.utils.suspendInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class WebhookService(
    private val webhookRepository: WebhookRepository,
    private val webhookClient: WebhookClient,
) {
    private val log = logger<WebhookService>()

    suspend fun saveCommit(
        githubDelivery: String,
        githubEvent: String,
        githubHookId: Long,
        githubTargetId: Long,
        githubTargetType: String,
        gitHubPushEvent: GitHubPushEvent,
    ) = coroutineScope {
        if (GHEvent.valueOf(githubEvent.uppercase()) != GHEvent.PUSH) {
            log.suspendError("Invalid event type : $githubEvent")
            return@coroutineScope
        }

        val saveWebhookCommitJob = launch {
            saveGitudyServer(gitHubPushEvent)
        }

        val saveLogDataJob = launch {
            val metadata = GithubMetadata(githubDelivery, githubEvent, githubHookId, githubTargetId, githubTargetType)
            val githubWebhook = GithubWebhook.of(metadata = metadata, gitHubPushEvent = gitHubPushEvent)

            saveLogData(githubWebhook)
        }

        joinAll(saveLogDataJob, saveWebhookCommitJob)
    }

    suspend fun saveGitudyServer(gitHubPushEvent: GitHubPushEvent) =
        gitHubPushEvent.commits.map {
            val commitDate = LocalDateTime.parse(it.timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate()
            return@map Commit(
                commitId = it.id,
                message = it.message,
                username = it.author.username,
                commitAdded = it.added,
                commitModified = it.modified,
                commitDate = commitDate
            )
        }.let {
            val request = WebhookCommitRequest(
                repositoryFullName = gitHubPushEvent.repository.fullName,
                commits = it
            )
            webhookClient.saveWebhookCommit(request)
            log.info("Saving gitudy server : $request")
        }

    suspend fun saveLogData(githubWebhook: GithubWebhook) {
        webhookRepository.save(githubWebhook).awaitSingleOrNull()
            ?: log.suspendError("Failed to save webhook LogData : $githubWebhook")

        log.suspendInfo("Saving webhook LogData : $githubWebhook")
    }
}
