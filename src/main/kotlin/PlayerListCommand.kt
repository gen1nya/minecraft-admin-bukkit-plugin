package online.ebatel

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.*

class PlayerListCommand : CommandExecutor {
    private val gson = Gson()
    private val lastKnownGameModes = mutableMapOf<UUID, String>()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp && !sender.isConsole()) {
            sender.sendMessage("§cNo permissions to execute!")
            return true
        }

        val whitelistedPlayersData = Bukkit.getWhitelistedPlayers().map { offlinePlayer ->
            val player = offlinePlayer.player
            val gameMode = player?.let {
                lastKnownGameModes[player.uniqueId] = player.gameMode.name
                player.gameMode
            } ?: lastKnownGameModes[offlinePlayer.uniqueId] ?: "unknown"

            mapOf(
                "name" to offlinePlayer.name,
                "uuid" to offlinePlayer.uniqueId.toString(),
                "isOp" to offlinePlayer.isOp,
                "isOnline" to offlinePlayer.isOnline,
                "gameMode" to gameMode
            )
        }

        val jsonOutput = gson.toJson(whitelistedPlayersData)
        sender.sendMessage(jsonOutput)
        return true
    }
}
