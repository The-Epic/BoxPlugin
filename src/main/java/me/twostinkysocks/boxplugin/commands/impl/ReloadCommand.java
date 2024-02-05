package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends SimpleCommandHandler {
    public ReloadCommand() {
        super("aetherconquest.command.reload");
        addExtraPermission("boxplugin.reload");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        getPlugin().load();
        sender.sendMessage(ChatColor.GREEN + "Reloaded");
        return false;
    }
}
