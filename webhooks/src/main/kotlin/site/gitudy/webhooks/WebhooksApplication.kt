package site.gitudy.webhooks

import io.netty.util.concurrent.FastThreadLocal
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.blockhound.BlockHound
import reactor.blockhound.integration.BlockHoundIntegration
import java.security.SecureRandom
import java.util.Random
import java.util.UUID
import java.util.zip.InflaterInputStream

@SpringBootApplication
class WebhooksApplication

fun main(args: Array<String>) {
//    BlockHound.install(BlockHoundIntegration { b: BlockHound.Builder ->
//        b.allowBlockingCallsInside(InflaterInputStream::class.java.name, "read")
//        b.allowBlockingCallsInside(Random::class.java.name, "nextInt")
//        b.allowBlockingCallsInside(UUID::class.java.name, "randomUUID")
//        b.allowBlockingCallsInside(SecureRandom::class.java.name, "nextBytes")
//    })
    runApplication<WebhooksApplication>(*args)
}
