package online.ebatel.fabric

import com.google.gson.Gson
import org.slf4j.Logger
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

/**
 * Fire-and-forget POST of chat messages to the admin panel, on a dedicated single thread so the
 * server tick is never blocked on network I/O. Payload matches the Bukkit/Forge ChatPayload.
 */
class WebhookClient(private val logger: Logger) {
    private val executor = Executors.newSingleThreadExecutor()

    private data class ChatPayload(
        val player: String,
        val playerUuid: String,
        val message: String
    )

    fun sendChatMessage(webhookUrl: String, player: String, playerUuid: String, message: String) {
        executor.submit {
            try {
                val json = GSON.toJson(ChatPayload(player, playerUuid, message))

                val connection = URL(webhookUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                connection.outputStream.use { os -> os.write(json.toByteArray(StandardCharsets.UTF_8)) }

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    logger.warn("Webhook request failed with status: {}", responseCode)
                }
                connection.disconnect()
            } catch (e: Exception) {
                logger.warn("Failed to send webhook: {}", e.message)
            }
        }
    }

    fun shutdown() {
        executor.shutdown()
    }

    private companion object {
        val GSON = Gson()
    }
}
