package me.twostinkysocks.boxplugin.commands.api;

import me.twostinkysocks.boxplugin.BoxPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

public abstract class SimpleCommandHandler implements TabExecutor {
    private final String permission;
    private final BoxPlugin plugin = BoxPlugin.instance;

    public SimpleCommandHandler(String permission) {
        this.permission = permission;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    public String getPermission() {
        return permission;
    }

    public BoxPlugin getPlugin() {
        return plugin;
    }

}