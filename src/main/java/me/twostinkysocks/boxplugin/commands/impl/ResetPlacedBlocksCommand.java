package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ResetPlacedBlocksCommand extends SimpleCommandHandler {

    public ResetPlacedBlocksCommand() {
        super("aetherconquest.command.resetplacedblocks");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.getPlugin().resetPlacedBlocks();
        sender.sendMessage(ChatColor.GREEN + "Reset placed blocks");
        return true;
    }
}
