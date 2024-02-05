package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DebugCommand extends SimpleCommandHandler {
    public DebugCommand() {
        super("aetherconquest.command.debug");
        addExtraPermission("boxplugin.debug");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageConstants.PLAYER_REQUIRED);
            return true;
        }

        if(!hasPermission(player)) {
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        if (getPlugin().isDebugEnabled(player)) {
            getPlugin().disableDebug(player);
            player.sendMessage(ChatColor.RED + "Debug mode disabled");
        } else {
            getPlugin().enableDebug(player);
            player.sendMessage(ChatColor.GREEN + "Debug mode enabled");
        }
        return true;
    }
}
