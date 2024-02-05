package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AddTagCommand extends SimpleCommandHandler {
    public AddTagCommand() {
        super("aetherconquest.command.addtag");
        addExtraPermission("boxplugin.addtag");
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

        if(player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You need to hold an item!");
            return true;
        }
        // /addtag string eggcommand mm spawn Zamm 1 %world%,%x%,%y%,%z%
        if(args.length == 0 || (!args[0].equals("string") && !args[0].equals("int"))) {
            player.sendMessage(ChatColor.RED + "Invalid data type! /addtag <string|int> <tag> <value>");
            return true;
        }

        if(args.length == 1 || args.length == 2) {
            player.sendMessage(ChatColor.RED + "Missing args! /addtag <string|int> <tag> <value>");
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        try {
            if(args[0].equals("int")) {
                int value = Integer.parseInt(args[2]);
                meta.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, args[1]), PersistentDataType.INTEGER, value);
            } else {
                String value = String.join(" ", Arrays.stream(args).skip(2).collect(Collectors.toList()));
                meta.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, args[1]), PersistentDataType.STRING, value);
            }
            item.setItemMeta(meta);
            player.getInventory().setItemInMainHand(item);
            player.sendMessage(ChatColor.AQUA + "Applied tag!");
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Value was not an integer, but integer type was supplied! /addtag <string|int> <tag> <value>");
            return true;
        }
        return false;
    }
}
