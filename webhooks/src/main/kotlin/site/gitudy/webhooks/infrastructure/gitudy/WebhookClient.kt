package site.gitudy.webhooks.infrastructure.gitudy

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.awaitExchange
import site.gitudy.webhooks.infrastructure.gitudy.dto.WebhookCommitRequest
import site.gitudy.webhooks.utils.logger
import site.gitudy.webhooks.utils.suspendError

@Component
class WebhookClient(
    @Value("\${gitudy.server.token}") val serverToken: String
) {
    private val log = logger<WebhookClient>()
    suspend fun saveWebhookCommit(request: WebhookCommitRequest) {
        val response = WebClient.create("http://localhost:8080")
            .post()
            .uri("/webhook/commit")
            .header("Authorization", "Bearer $serverToken")
            .bodyValue(request)
            .awaitExchange { it.awaitBodilessEntity() }

        if (!response.statusCode.is2xxSuccessful) {
            log.suspendError("Failed to save gitudy server = $response")
        }
    }
}