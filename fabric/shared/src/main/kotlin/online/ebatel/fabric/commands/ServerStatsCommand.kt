package online.ebatel.fabric.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import online.ebatel.common.JsonSerializer
import online.ebatel.common.MemoryUtils
import online.ebatel.common.ServerStats
import online.ebatel.fabric.TpsTracker

/**
 * /serverstat — emits server version, online count, memory and TPS as JSON.
 * Consumed by the admin panel over RCON.
 */
object ServerStatsCommand {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("serverstat")
                .requires { source -> source.entity !is ServerPlayer || source.hasPermission(2) }
                .executes(::execute)
        )
    }

    private fun execute(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source
        val server = source.server

        val stats = ServerStats(
            server.serverVersion,
            server.playerCount,
            MemoryUtils.getUsedMemoryMB(),
            MemoryUtils.getAllocatedMemoryMB(),
            round2(TpsTracker.getTps1m()),
            round2(TpsTracker.getTps5m()),
            round2(TpsTracker.getTps15m())
        )

        val jsonOutput = JsonSerializer.serializeServerStats(stats)
        source.sendSuccess({ Component.literal(jsonOutput) }, false)
        return 1
    }

    private fun round2(value: Double): Double = Math.round(value * 100.0) / 100.0
}
