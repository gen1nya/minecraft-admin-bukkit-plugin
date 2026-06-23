package online.ebatel.fabric

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import online.ebatel.fabric.commands.PlayerListCommand
import online.ebatel.fabric.commands.ServerStatsCommand
import org.slf4j.LoggerFactory

/**
 * Fabric entry point. Mirrors the behaviour of the Bukkit/Forge variants:
 *  - registers the /playerlist and /serverstat commands (consumed by the admin panel over RCON)
 *  - tracks TPS every server tick
 *  - optionally forwards in-game chat to the admin panel webhook
 *
 * Registered as a Kotlin entry point, so the server needs fabric-language-kotlin installed.
 */
object WebAdminMod : ModInitializer {
    const val MOD_ID = "webadminplugin"

    @JvmField
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    private var webhookClient: WebhookClient? = null

    override fun onInitialize() {
        FabricModConfig.load()

        // Keep TPS samples flowing regardless of webhook config (serverstat needs them).
        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick { TpsTracker.onServerTick() })

        CommandRegistrationCallback.EVENT.register(
            CommandRegistrationCallback { dispatcher, _, _ ->
                PlayerListCommand.register(dispatcher)
                ServerStatsCommand.register(dispatcher)
            }
        )
        LOGGER.info("WebAdminPlugin commands registered!")

        val webhookUrl = FabricModConfig.getWebhookUrl()
        if (webhookUrl != null) {
            val client = WebhookClient(LOGGER)
            webhookClient = client
            ServerMessageEvents.CHAT_MESSAGE.register(
                ServerMessageEvents.ChatMessage { message, sender, _ ->
                    // Re-read each time: config may have been reloaded; cheap.
                    val url = FabricModConfig.getWebhookUrl() ?: return@ChatMessage
                    client.sendChatMessage(
                        url,
                        sender.name.string,
                        sender.uuid.toString(),
                        message.signedContent()
                    )
                }
            )
            LOGGER.info("Chat webhook enabled: {}", webhookUrl)
        } else {
            LOGGER.info("Chat webhook disabled (see config/{}.json).", MOD_ID)
        }

        ServerLifecycleEvents.SERVER_STOPPING.register(
            ServerLifecycleEvents.ServerStopping { webhookClient?.shutdown() }
        )

        LOGGER.info("WebAdminPlugin (Fabric) initialized!")
    }
}
