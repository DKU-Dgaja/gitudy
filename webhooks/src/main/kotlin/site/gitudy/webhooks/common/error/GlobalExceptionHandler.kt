package site.gitudy.webhooks.common.error

import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import site.gitudy.webhooks.utils.logger

@Component
@Order(-2)
class GlobalExceptionHandler(
    globalErrorAttributes: ErrorAttributes?,
    applicationContext: ApplicationContext?,
    configurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(
    globalErrorAttributes,
    WebProperties.Resources(),
    applicationContext
) {
    private val log = logger<GlobalExceptionHandler>()
    init {
        this.setMessageWriters(configurer.writers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all()) { request: ServerRequest ->
            renderErrorResponse(request)
        }
    }

    private fun renderErrorResponse(request: ServerRequest): Mono<ServerResponse> {
        val errorPropertiesMap = when (val error = getError(request)) {
            is RuntimeException -> errorMap("400", "BAD_REQUEST", error.message!!)
            else -> errorMap("400", "ERROR", error.message!!)
        }
        log.error("Error: $errorPropertiesMap")
        return ServerResponse.status(HttpStatus.valueOf(errorPropertiesMap["error"]!!["status"]!!.toInt()))
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue<Map<String, Any>>(errorPropertiesMap))
    }

    fun errorMap(status: String, title: String, message: String) = mapOf(
        "error" to mapOf(
            "status" to status,
            "title" to title,
            "message" to message
        )
    )
}