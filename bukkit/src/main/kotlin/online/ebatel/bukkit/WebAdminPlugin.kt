package online.ebatel.bukkit

import org.bukkit.plugin.java.JavaPlugin

class WebAdminPlugin : JavaPlugin() {
    override fun onEnable() {
        logger.info("WebAdminPlugin enabled!")
        getCommand("playerlist")?.setExecutor(PlayerListCommand())
        getCommand("serverstat")?.setExecutor(ServerStatsCommand())
    }

    override fun onDisable() {
        logger.info("WebAdminPlugin disabled!")
    }
}
