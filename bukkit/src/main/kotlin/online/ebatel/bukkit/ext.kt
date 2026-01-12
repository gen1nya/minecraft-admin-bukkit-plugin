package online.ebatel.bukkit

import org.bukkit.command.CommandSender

fun CommandSender.isConsole(): Boolean = this !is org.bukkit.entity.Player
