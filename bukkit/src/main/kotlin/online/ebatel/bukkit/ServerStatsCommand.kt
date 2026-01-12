package online.ebatel.bukkit

import online.ebatel.common.JsonSerializer
import online.ebatel.common.MemoryUtils
import online.ebatel.common.ServerStats
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ServerStatsCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp && !sender.isConsole()) {
            sender.sendMessage("§cNo permissions to execute!")
            return true
        }

        val tps = Bukkit.getTPS()

        val stats = ServerStats(
            version = Bukkit.getVersion(),
            onlinePlayers = Bukkit.getOnlinePlayers().size,
            memoryUsedMB = MemoryUtils.getUsedMemoryMB(),
            memoryAllocatedMB = MemoryUtils.getAllocatedMemoryMB(),
            tps1m = String.format("%.2f", tps[0]).toDouble(),
            tps5m = String.format("%.2f", tps[1]).toDouble(),
            tps15m = String.format("%.2f", tps[2]).toDouble()
        )

        val jsonOutput = JsonSerializer.serializeServerStats(stats)
        sender.sendMessage(jsonOutput)
        return true
    }
}
