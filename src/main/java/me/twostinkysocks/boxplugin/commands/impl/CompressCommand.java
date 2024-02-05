package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CompressCommand extends SimpleCommandHandler {
    public CompressCommand() {
        super("aetherconquest.command.compress");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageConstants.PLAYER_REQUIRED);
            return true;
        }

        if(!player.hasPermission("boxplugin.compress")) {
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        getPlugin().getCompressor().compressAll(player);
        getPlugin().getCompressor().compressAll(player);
        player.sendMessage(ChatColor.AQUA + "Compressed your inventory!");
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 2f);
        return true;
    }
}
