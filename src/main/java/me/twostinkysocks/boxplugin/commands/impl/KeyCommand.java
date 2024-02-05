package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.key.KeyManager;

import java.util.ArrayList;
import java.util.List;

public class KeyCommand extends SimpleCommandHandler {

    public KeyCommand() {
        super("aetherconquest.command.key");
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

        if(args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /key <tier>");
            return true;
        }

        KeyManager keyManager = getPlugin().getKeyManager();
        switch (args[0]) {
            case "common" -> {
                if (!player.hasPermission("boxplugin.key.common")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to get that key type!");
                    return true;
                }

                keyManager.giveKey(player, keyManager.getKeyById("common"), 1);
            }
            case "rare" -> {
                if (!player.hasPermission("boxplugin.key.rare")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to get that key type!");
                    return true;
                }

                keyManager.giveKey(player, keyManager.getKeyById("rare"), 1);
            }
            case "epic" -> {
                if (!player.hasPermission("boxplugin.key.epic")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to get that key type!");
                    return true;
                }

                keyManager.giveKey(player, keyManager.getKeyById("epic"), 1);
            }
            case "legendary" -> {
                if (!player.hasPermission("boxplugin.key.legendary")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to get that key type!");
                    return true;
                }

                keyManager.giveKey(player, keyManager.getKeyById("legendary"), 1);
            }
            default -> player.sendMessage(ChatColor.RED + "Usage: /key <tier>");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {
            ArrayList<String> keys = new ArrayList<>();
            if(sender.hasPermission("boxplugin.key.common")) {
                keys.add("common");
            }
            if(sender.hasPermission("boxplugin.key.rare")) {
                keys.add("rare");
            }
            if(sender.hasPermission("boxplugin.key.epic")) {
                keys.add("epic");
            }
            if(sender.hasPermission("boxplugin.key.legendary")) {
                keys.add("legendary");
            }
            return StringUtil.copyPartialMatches(args[0], keys, new ArrayList<>());
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
