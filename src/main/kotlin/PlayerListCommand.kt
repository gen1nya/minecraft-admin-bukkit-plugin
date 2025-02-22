package online.ebatel

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class PlayerListCommand : CommandExecutor {
    private val gson = Gson()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp && !sender.isConsole()) {
            sender.sendMessage("§cNo permissions to execute!")
            return true
        }

        val whitelistedPlayers = Bukkit.getWhitelistedPlayers().map {
            mapOf(
                "name" to it.name,
                "uuid" to it.uniqueId.toString(),
                "isOp" to it.isOp,
                "isOnline" to it.isOnline
            )
        }

        val jsonOutput = gson.toJson(whitelistedPlayers)
        sender.sendMessage(jsonOutput)
        return true
    }
}
