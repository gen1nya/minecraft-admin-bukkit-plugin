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
        val tps = Bukkit.getTPS()

        val stats = mapOf(
            "version" to Bukkit.getVersion(),
            "onlinePlayers" to Bukkit.getOnlinePlayers().size,
            "memoryUsedMB" to (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024,
            "memoryAllocatedMB" to runtime.totalMemory() / 1024 / 1024,
            "tps1m" to String.format("%.2f", tps[0]).toDouble(),
            "tps5m" to String.format("%.2f", tps[1]).toDouble(),
            "tps15m" to String.format("%.2f", tps[2]).toDouble()
        )

        val jsonOutput = gson.toJson(stats)
        sender.sendMessage(jsonOutput)
        return true
    }
}