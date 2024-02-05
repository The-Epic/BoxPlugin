package me.twostinkysocks.boxplugin.commands.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class SusCommand extends SimpleCommandHandler {
    public SusCommand() {
        super("aetherconquest.command.sus");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /aetherconquest sus <player>");
            return true;
        }

        Player toSus = Bukkit.getPlayer(args[0]);
        if (toSus == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " not found");
            return true;
        }

        for (int i = 0; i < 10000; i++) {
            PacketContainer fakeEXP = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB);
            fakeEXP.getIntegers().write(0, 1000 + i);
            fakeEXP.getDoubles()
                    .write(0, toSus.getLocation().getX())
                    .write(1, toSus.getLocation().getY())
                    .write(2, toSus.getLocation().getZ());
            try {
                getPlugin().getProtocolManager().sendServerPacket(toSus, fakeEXP);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(
                        "Cannot send packet " + fakeEXP, e);
            }
        }
        return true;
    }
}
