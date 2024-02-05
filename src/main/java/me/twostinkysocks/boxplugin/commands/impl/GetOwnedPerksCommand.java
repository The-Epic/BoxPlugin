package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class GetOwnedPerksCommand extends SimpleCommandHandler {
    public GetOwnedPerksCommand() {
        super("aetherconquest.command.getownedperks");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageConstants.PLAYER_REQUIRED);
            return true;
        }
        player.sendMessage(String.join("\n",
                getPlugin().getPerksManager().getPurchasedPerks(player).stream().map(pe -> pe.instance.getKey())
                        .collect(Collectors.toList())
        ));
        return true;
    }
}
