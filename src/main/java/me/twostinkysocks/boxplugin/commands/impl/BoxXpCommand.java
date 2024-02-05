package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import me.twostinkysocks.boxplugin.event.PlayerBoxXpUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoxXpCommand extends SimpleCommandHandler {

    public BoxXpCommand() {
        super("aetherconquest.command.box-xp");
        addExtraPermission("boxplugin.xpcommands");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /aetherconquest boxxp <get|set|add> [player] [amount]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[1] + " not found");
            return true;
        }

        switch (args[0]) {
            case "get" -> {
                sender.sendMessage(target.getName() + " has " + this.getPlugin().getXpManager().getXP(target) + " xp");
            }
            case "set", "add" -> {
                try {
                    int amount = Integer.parseInt(args[2]);
                    addOrSet(target, amount, args[0].equals("add"));
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount, please use a valid number.");
                    return true;
                }
            }
            default -> {
                sender.sendMessage(ChatColor.RED + "Usage: /aetherconquest boxxp <get|set|add> [player] [amount]");
            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("get", "set", "add"), new ArrayList<>());
        } else if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1],
                    Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(
                            Collectors.toList()), new ArrayList<>()
            );
        }
        return super.onTabComplete(sender, command, alias, args);
    }

    private void addOrSet(Player player, int amount, boolean add) {
        int existingXp = this.getPlugin().getXpManager().getXP(player);
        int newTotal;

        if (add) {
            newTotal = existingXp + amount;
            player.sendMessage(ChatColor.GREEN + "Added " + amount + " xp to " + player.getName());
        } else {
            newTotal = amount;
            player.sendMessage(ChatColor.GREEN + "Set " + player.getName() + "'s xp to " + amount);
        }

        this.getPlugin().getXpManager().setXP(player, newTotal);

        Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(player, existingXp, newTotal));
    }
}
