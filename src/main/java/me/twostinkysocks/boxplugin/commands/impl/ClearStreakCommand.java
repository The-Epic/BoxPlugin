package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearStreakCommand extends SimpleCommandHandler {
    public ClearStreakCommand() {
        super("aetherconquest.command.clearstreak");
        addExtraPermission("boxplugin.clearstreak");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(MessageConstants.PLAYER_REQUIRED);
            return true;
        }

        if(!hasPermission(player)) {
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        if(args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /clearstreak <player>");
            return true;
        }

        Player toClear = Bukkit.getPlayer(args[0]);

        if(toClear == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        getPlugin().getPvpManager().resetStreak(toClear);
        player.sendMessage(ChatColor.GREEN + "Cleared streak!");
        return false;
    }
}
