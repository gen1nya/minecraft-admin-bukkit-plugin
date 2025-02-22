package online.ebatel

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ServerStatsCommand : CommandExecutor {
    private val gson = Gson()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp && !sender.isConsole()) {
            sender.sendMessage("§cNo permissions to execute!")
            return true
        }

        val runtime = Runtime.getRuntime()
        val stats = mapOf(
            "version" to Bukkit.getVersion(),
            "onlinePlayers" to Bukkit.getOnlinePlayers().size,
            "memoryUsedMB" to (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024,
            "memoryAllocatedMB" to runtime.totalMemory() / 1024 / 1024
        )

        val jsonOutput = gson.toJson(stats)
        sender.sendMessage(jsonOutput)
        return true
    }
}