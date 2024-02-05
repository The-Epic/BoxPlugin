package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResetPerksCommand extends SimpleCommandHandler {
    public ResetPerksCommand() {
        super("aetherconquest.command.resetperks");
        addExtraPermission("boxplugin.resetperks");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You need to be a player for this command.");
            return true;
        }

        if(args.length == 0) {
            getPlugin().getPerksManager().resetPerks(player);
            player.sendMessage(ChatColor.GREEN + "Reset your perks to default!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            player.sendMessage(ChatColor.RED + "Invalid command! Usage: /resetperks <player> ");
            return true;
        }

        getPlugin().getPerksManager().resetPerks(Bukkit.getPlayer(args[0]));
        target.getOpenInventory().close();

        player.sendMessage(ChatColor.GREEN + "Reset " + args[0] + "'s perks to default!");
        target.sendMessage(ChatColor.GREEN + "Your perks were reset by an admin.");

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
        target.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
        return true;
    }
}
