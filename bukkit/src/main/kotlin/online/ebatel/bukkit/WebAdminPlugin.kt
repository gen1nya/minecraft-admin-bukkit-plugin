package online.ebatel.bukkit

import org.bukkit.plugin.java.JavaPlugin

class WebAdminPlugin : JavaPlugin() {
    private var webhookClient: WebhookClient? = null

    override fun onEnable() {
        logger.info("WebAdminPlugin enabled!")

        // Save default config
        saveDefaultConfig()

        // Register commands
        getCommand("playerlist")?.setExecutor(PlayerListCommand())
        getCommand("serverstat")?.setExecutor(ServerStatsCommand())

        // Setup chat webhook if enabled
        if (config.getBoolean("webhook.enabled", false)) {
            setupChatWebhook()
        }
    }

    private fun setupChatWebhook() {
        val serverId = config.getString("webhook.serverId", "default") ?: "default"
        val baseUrl = config.getString("webhook.url", "") ?: ""

        if (baseUrl.isBlank()) {
            logger.warning("Webhook URL is not configured. Chat forwarding disabled.")
            return
        }

        val webhookUrl = if (baseUrl.contains("/chat/webhook")) {
            baseUrl
        } else {
            "${baseUrl.trimEnd('/')}/api/servers/$serverId/chat/webhook"
        }

        webhookClient = WebhookClient(logger)
        val chatListener = ChatListener(webhookClient!!) { webhookUrl }

        server.pluginManager.registerEvents(chatListener, this)
        logger.info("Chat webhook enabled: $webhookUrl")
    }

    override fun onDisable() {
        webhookClient?.shutdown()
        logger.info("WebAdminPlugin disabled!")
    }
}
