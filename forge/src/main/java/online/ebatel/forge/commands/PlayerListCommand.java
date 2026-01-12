package online.ebatel.forge.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraftforge.server.ServerLifecycleHooks;
import online.ebatel.common.JsonSerializer;
import online.ebatel.common.PlayerData;
import online.ebatel.forge.WebAdminMod;

import java.util.ArrayList;
import java.util.List;

public class PlayerListCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("playerlist")
                .requires(source -> !(source.getEntity() instanceof ServerPlayer) || source.hasPermission(2))
                .executes(PlayerListCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return 0;

        PlayerList playerList = server.getPlayerList();
        UserWhiteList whitelist = playerList.getWhiteList();

        List<PlayerData> whitelistedPlayersData = new ArrayList<>();

        // Get whitelisted player names and look them up
        String[] whitelistedNames = whitelist.getUserList();

        for (String name : whitelistedNames) {
            GameProfile profile = server.getProfileCache()
                .get(name)
                .orElse(null);

            if (profile == null) continue;

            ServerPlayer onlinePlayer = playerList.getPlayer(profile.getId());

            whitelistedPlayersData.add(new PlayerData(
                profile.getName(),
                profile.getId().toString(),
                playerList.isOp(profile),
                onlinePlayer != null,
                playerList.getBans().isBanned(profile),
                onlinePlayer != null ? onlinePlayer.gameMode.getGameModeForPlayer().getName() : "unknown"
            ));
        }

        String jsonOutput = JsonSerializer.INSTANCE.serializePlayerList(whitelistedPlayersData);
        source.sendSuccess(() -> Component.literal(jsonOutput), false);
        return 1;
    }
}
