package site.gitudy.webhooks.common.filter

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

const val CONTEXT_MAP = "context-map"
const val LOGGING_ID = "logging-id"

@Component
class RequestFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val contextMap = ConcurrentHashMap(
            mapOf(
                LOGGING_ID to UUID.randomUUID().toString()
            )
        )
        return chain
            .filter(exchange)
            .contextWrite { it.put(CONTEXT_MAP, contextMap) }
    }
}