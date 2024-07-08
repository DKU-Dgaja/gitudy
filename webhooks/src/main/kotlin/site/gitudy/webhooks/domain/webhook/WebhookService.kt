package site.gitudy.webhooks.domain.webhook

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.kohsuke.github.GHEvent
import org.springframework.stereotype.Service
import site.gitudy.webhooks.domain.webhook.github.*
import site.gitudy.webhooks.utils.logger
import site.gitudy.webhooks.utils.suspendError
import site.gitudy.webhooks.utils.suspendInfo

@Service
class WebhookService(
    private val webhookRepository: WebhookRepository,
) {
    private val log = logger<WebhookService>()

    suspend fun save(
        githubDelivery: String,
        githubEvent: String,
        githubHookId: Long,
        githubTargetId: Long,
        githubTargetType: String,
        gitHubPushEvent: GitHubPushEvent,
    ) {
        if (GHEvent.valueOf(githubEvent.uppercase()) != GHEvent.PUSH) {
            log.suspendError("Invalid event type : $githubEvent")
            return
        }

        val metadata = GithubMetadata(githubDelivery, githubEvent, githubHookId, githubTargetId, githubTargetType)
        val githubWebhook = GithubWebhook.of(metadata = metadata, gitHubPushEvent = gitHubPushEvent)

        log.suspendInfo("Saving webhook : $githubWebhook")
        webhookRepository.save(githubWebhook).awaitSingleOrNull()
            ?: log.suspendError("Failed to save webhook : $githubWebhook")
    }
}
