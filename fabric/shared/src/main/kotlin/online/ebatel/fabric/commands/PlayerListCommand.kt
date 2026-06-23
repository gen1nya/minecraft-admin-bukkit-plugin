package online.ebatel.fabric.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import online.ebatel.common.JsonSerializer
import online.ebatel.common.PlayerData

/**
 * /playerlist — emits whitelisted players (with online/op/ban/gamemode state) as JSON.
 * The admin panel runs this over RCON and JSON.parse()s the output.
 */
object PlayerListCommand {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("playerlist")
                .requires { source -> source.entity !is ServerPlayer || source.hasPermission(2) }
                .executes(::execute)
        )
    }

    private fun execute(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source
        val server = source.server
        val playerList = server.playerList
        val whitelist = playerList.whiteList

        val players = ArrayList<PlayerData>()
        for (name in whitelist.userList) {
            val profile = server.profileCache?.get(name)?.orElse(null) ?: continue
            val online: ServerPlayer? = playerList.getPlayer(profile.id)

            // Enum constant name is UPPERCASE (SURVIVAL/CREATIVE/ADVENTURE/SPECTATOR), matching the
            // admin panel's expected values and the Bukkit variant. (Forge used the lowercase id.)
            val gameMode = online?.gameMode?.gameModeForPlayer?.name ?: "unknown"

            players.add(
                PlayerData(
                    profile.name,
                    profile.id.toString(),
                    playerList.isOp(profile),
                    online != null,
                    playerList.bans.isBanned(profile),
                    gameMode
                )
            )
        }

        val jsonOutput = JsonSerializer.serializePlayerList(players)
        source.sendSuccess({ Component.literal(jsonOutput) }, false)
        return 1
    }
}
