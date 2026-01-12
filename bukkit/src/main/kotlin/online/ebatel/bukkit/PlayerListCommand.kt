package online.ebatel.bukkit

import online.ebatel.common.JsonSerializer
import online.ebatel.common.PlayerData
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.*

class PlayerListCommand : CommandExecutor {
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
                player.gameMode.name
            } ?: lastKnownGameModes[offlinePlayer.uniqueId] ?: "unknown"

            PlayerData(
                name = offlinePlayer.name,
                uuid = offlinePlayer.uniqueId.toString(),
                isOp = offlinePlayer.isOp,
                isOnline = offlinePlayer.isOnline,
                isBanned = offlinePlayer.isBanned,
                gameMode = gameMode
            )
        }

        val jsonOutput = JsonSerializer.serializePlayerList(whitelistedPlayersData)
        sender.sendMessage(jsonOutput)
        return true
    }
}
