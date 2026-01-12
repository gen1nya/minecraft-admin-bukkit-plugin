package online.ebatel.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import online.ebatel.common.JsonSerializer;
import online.ebatel.common.MemoryUtils;
import online.ebatel.common.ServerStats;
import online.ebatel.forge.TpsTracker;

public class ServerStatsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("serverstat")
                .requires(source -> !(source.getEntity() instanceof ServerPlayer) || source.hasPermission(2))
                .executes(ServerStatsCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return 0;

        ServerStats stats = new ServerStats(
            server.getServerVersion(),
            server.getPlayerCount(),
            MemoryUtils.INSTANCE.getUsedMemoryMB(),
            MemoryUtils.INSTANCE.getAllocatedMemoryMB(),
            Math.round(TpsTracker.getTps1m() * 100.0) / 100.0,
            Math.round(TpsTracker.getTps5m() * 100.0) / 100.0,
            Math.round(TpsTracker.getTps15m() * 100.0) / 100.0
        );

        String jsonOutput = JsonSerializer.INSTANCE.serializeServerStats(stats);
        source.sendSuccess(() -> Component.literal(jsonOutput), false);
        return 1;
    }
}
