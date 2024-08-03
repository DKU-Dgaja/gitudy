package site.gitudy.webhooks.domain.webhook

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import site.gitudy.webhooks.domain.webhook.github.GitHubPushEvent
import site.gitudy.webhooks.utils.logger
import site.gitudy.webhooks.utils.suspendInfo

@RestController
@RequestMapping("/webhooks")
class WebhookController(
    private val webhookService: WebhookService
) {
    private val log = logger<WebhookController>()

    @PostMapping("/github")
    suspend fun githubWebhook(
        @RequestHeader("X-GitHub-Delivery") githubDelivery: String,
        @RequestHeader("X-GitHub-Event") githubEvent: String,
        @RequestHeader("X-GitHub-Hook-ID") githubHookId: Long,
        @RequestHeader("X-GitHub-Hook-Installation-Target-ID") githubTargetId: Long,
        @RequestHeader("X-GitHub-Hook-Installation-Target-Type") githubTargetType: String,
        @RequestBody gitHubPushEvent: GitHubPushEvent
    ) {
        webhookService.saveCommit(githubDelivery, githubEvent, githubHookId, githubTargetId, githubTargetType, gitHubPushEvent)
        log.suspendInfo("Webhook saved successfully")
    }
}