package online.ebatel.fabric

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path

/**
 * Fabric has no built-in config system, so we keep a small hand-rolled JSON file at
 * config/webadminplugin.json. The webhook URL is built exactly like the Forge ModConfig:
 *   {base}/api/servers/{serverId}/chat/webhook
 */
object FabricModConfig {
    private val GSON: Gson = GsonBuilder().setPrettyPrinting().create()

    data class Data(
        var enabled: Boolean = false,
        var url: String = "",
        var serverId: String = "default"
    )

    @Volatile
    private var data = Data()

    private fun configPath(): Path =
        FabricLoader.getInstance().configDir.resolve("${WebAdminMod.MOD_ID}.json")

    fun load() {
        val path = configPath()
        try {
            if (Files.exists(path)) {
                Files.newBufferedReader(path).use { reader ->
                    data = GSON.fromJson(reader, Data::class.java) ?: Data()
                }
            } else {
                save() // materialize defaults so the admin can edit them
            }
        } catch (e: Exception) {
            WebAdminMod.LOGGER.warn("Failed to load config, using defaults: {}", e.message)
            data = Data()
        }
    }

    fun save() {
        val path = configPath()
        try {
            Files.createDirectories(path.parent)
            Files.newBufferedWriter(path).use { writer -> GSON.toJson(data, writer) }
        } catch (e: Exception) {
            WebAdminMod.LOGGER.warn("Failed to save config: {}", e.message)
        }
    }

    /** Returns the fully-qualified webhook URL, or null when disabled/unconfigured. */
    fun getWebhookUrl(): String? {
        if (!data.enabled) return null
        val baseUrl = data.url
        if (baseUrl.isBlank()) return null
        if (baseUrl.contains("/chat/webhook")) return baseUrl

        val serverId = data.serverId.ifBlank { "default" }
        return baseUrl.trimEnd('/') + "/api/servers/" + serverId + "/chat/webhook"
    }
}
