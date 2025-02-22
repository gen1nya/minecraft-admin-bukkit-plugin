package online.ebatel

import org.bukkit.command.CommandSender

fun CommandSender.isConsole(): Boolean = this !is org.bukkit.entity.Player