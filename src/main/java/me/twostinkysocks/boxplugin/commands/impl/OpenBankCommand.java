package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OpenBankCommand extends SimpleCommandHandler {
    public OpenBankCommand() {
        super("aetherconquest.command.openbank");
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

        getPlugin().getMarketManager().openGui(player);
        return true;
    }
}
