package site.gitudy.webhooks.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

/**
 * 주의할 점은 코루틴의 구조화된 동시성이 깨지면, contextMap을 잃어버릴 수 있다.
 */
suspend fun Logger.suspendInfo(msg: String) {
    info("[${RequestContext.getLoggerId()}] ${msg}")
}

suspend fun Logger.suspendWarn(msg: String) {
    info("[${RequestContext.getLoggerId()}] ${msg}")
}

suspend fun Logger.suspendError(msg: String) {
    info("[${RequestContext.getLoggerId()}] ${msg}")
}

suspend fun Logger.suspendDebug(msg: String) {
    info("[${RequestContext.getLoggerId()}] ${msg}")
}