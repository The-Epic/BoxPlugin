package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import me.twostinkysocks.boxplugin.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetMarketMultiplierCommand extends SimpleCommandHandler {
    public SetMarketMultiplierCommand() {
        super("aetherconquest.command.setmarketmultiplier");
        addExtraPermission("boxplugin.setmarketmultiplier");
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
            player.sendMessage(ChatColor.RED + "Usage: /setmarketmultiplier <multiplier>");
            return true;
        }

        if(!Util.isDouble(args[0])) {
            player.sendMessage(ChatColor.RED + "Bad input!");
            return true;
        }
        double d = Double.parseDouble(args[0]);
        getPlugin().getMarketManager().setMarketMultiplier(d);
        player.sendMessage(ChatColor.GREEN + "Set multiplier to " + d);
        return true;
    }
}
