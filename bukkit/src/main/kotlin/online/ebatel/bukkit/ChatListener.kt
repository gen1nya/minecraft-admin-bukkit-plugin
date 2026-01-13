package online.ebatel.bukkit

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatListener(
    private val webhookClient: WebhookClient,
    private val webhookUrlProvider: () -> String?
) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val webhookUrl = webhookUrlProvider() ?: return

        val player = event.player
        webhookClient.sendChatMessage(
            webhookUrl = webhookUrl,
            player = player.name,
            playerUuid = player.uniqueId.toString(),
            message = event.message
        )
    }
}
