package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TreeCommand extends SimpleCommandHandler {
    public TreeCommand() {
        super("aetherconquest.command.tree");
        addExtraPermission("boxplugin.tree");
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
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY() + 1;
        int z = player.getLocation().getBlockZ();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "place feature oak " + x + " " + y + " " + z);
        player.getWorld().getBlockAt(x, y-1, z).setType(Material.OAK_LOG);
        player.sendMessage(ChatColor.GREEN + "Placed tree!");
        return true;
    }
}
