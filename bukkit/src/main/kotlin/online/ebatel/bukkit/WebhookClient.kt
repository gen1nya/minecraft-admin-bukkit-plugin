package online.ebatel.bukkit

import online.ebatel.common.JsonSerializer
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger

data class ChatPayload(
    val player: String,
    val playerUuid: String,
    val message: String
)

class WebhookClient(
    private val logger: Logger
) {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    fun sendChatMessage(webhookUrl: String, player: String, playerUuid: String, message: String) {
        executor.submit {
            try {
                val payload = ChatPayload(player, playerUuid, message)
                val json = JsonSerializer.toJson(payload)

                val url = URL(webhookUrl)
                val connection = url.openConnection() as HttpURLConnection

                connection.apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 5000
                    readTimeout = 5000
                }

                connection.outputStream.use { os ->
                    os.write(json.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    logger.warning("Webhook request failed with status: $responseCode")
                }

                connection.disconnect()
            } catch (e: Exception) {
                logger.warning("Failed to send webhook: ${e.message}")
            }
        }
    }

    fun shutdown() {
        executor.shutdown()
    }
}
