package site.gitudy.webhooks.utils

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.reactor.ReactorContext
import site.gitudy.webhooks.common.filter.CONTEXT_MAP
import site.gitudy.webhooks.common.filter.LOGGING_ID

object RequestContext {
    suspend fun getContextMap(): MutableMap<String, String?>? =
        currentCoroutineContext()[ReactorContext]
            ?.context
            ?.get<MutableMap<String, String?>>(CONTEXT_MAP)

    suspend fun putToContext(key: String, value: String?) = getContextMap()?.put(key, value)

    suspend fun putToContexts(map: Map<String, String?>) = getContextMap()?.putAll(map)

    suspend inline fun getFromContext(key: String): String? = getContextMap()?.get(key)

    suspend fun getLoggerId(): String? = getFromContext(LOGGING_ID)

    suspend fun setLoggerId(value: String): String? = putToContext(LOGGING_ID, value)
}