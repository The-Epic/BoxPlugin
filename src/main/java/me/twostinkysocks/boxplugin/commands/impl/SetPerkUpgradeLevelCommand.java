package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import me.twostinkysocks.boxplugin.manager.PerksManager;
import me.twostinkysocks.boxplugin.perks.Upgradable;
import me.twostinkysocks.boxplugin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetPerkUpgradeLevelCommand extends SimpleCommandHandler {
    public SetPerkUpgradeLevelCommand() {
        super("aetherconquest.command.setperkupgradelvl");
        addExtraPermission("boxplugin.manageperks");
        addExtraPermission("manageperks");
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

        if(args.length < 3 || Bukkit.getPlayer(args[0]) == null || PerksManager.Perk.getByName(args[1]) == null || !(PerksManager.Perk.getByName(args[1]).instance instanceof Upgradable) || !Util.isInteger(args[2])) {
            player.sendMessage(ChatColor.RED + "Usage: /setperkupgradelevel <player> <upgradeableperk> <level>");
            return true;
        }

        Upgradable up = (Upgradable) PerksManager.Perk.getByName(args[1]).instance;
        Player toSearch = Bukkit.getPlayer(args[0]);
        int num = Integer.parseInt(args[2]);
        up.setLevel(toSearch, num);
        player.sendMessage("Set " + toSearch.getName() + "'s level for " + PerksManager.Perk.getByName(args[1]).instance.getKey() + " to " + up.getLevel(toSearch));
        toSearch.sendMessage(ChatColor.AQUA + "Your perk level for " + PerksManager.Perk.getByName(args[1]).instance.getKey() + " was set to " + up.getLevel(toSearch) + " by an admin.");
        return true;
    }
}
