package site.gitudy.webhooks.domain.webhook

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import site.gitudy.webhooks.domain.webhook.github.GithubWebhook

interface WebhookRepository : ReactiveCrudRepository<GithubWebhook, Long> {

}