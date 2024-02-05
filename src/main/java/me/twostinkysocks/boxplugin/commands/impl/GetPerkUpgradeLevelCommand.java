package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import me.twostinkysocks.boxplugin.manager.PerksManager;
import me.twostinkysocks.boxplugin.perks.Upgradable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GetPerkUpgradeLevelCommand extends SimpleCommandHandler {
    public GetPerkUpgradeLevelCommand() {
        super("aetherconquest.command.getperkupgradelvl");
        addExtraPermission("manageperks");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageConstants.PLAYER_REQUIRED);
            return true;
        }

        if (!hasPermission(sender)) {
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /getperkupgradelevel <player> <upgradeableperk>");
            return true;
        }

        Player toSearch = Bukkit.getPlayer(args[0]);
        if (toSearch == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        PerksManager.Perk perk = PerksManager.Perk.getByName(args[1]);
        if (perk == null) {
            player.sendMessage(ChatColor.RED + "Perk not found!");
            return true;
        }

        if (!(perk.instance instanceof Upgradable up)) {
            player.sendMessage(ChatColor.RED + "Perk is not upgradeable!");
            return true;
        }

        player.sendMessage(
                toSearch.getName() + "'s level for " + perk.instance.getKey() + ": " + up.getLevel(toSearch));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0],
                    Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(
                            Collectors.toList()), new ArrayList<>()
            );
        } else if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1],
                    Arrays.stream(PerksManager.Perk.values()).filter(per -> per.instance instanceof Upgradable)
                            .map(per -> per.instance.getKey()).collect(Collectors.toList()), new ArrayList<>()
            );
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
