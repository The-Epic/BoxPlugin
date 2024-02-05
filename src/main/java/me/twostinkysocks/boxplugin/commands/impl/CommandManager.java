package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.commands.api.ArgumentCommandHandler;

public class CommandManager extends ArgumentCommandHandler {

    public CommandManager() {
        super("aetherconquest.command", () -> "You do not have permission to use this command",
                () -> "Usage: /aetherconquest <%args%>"
        );

        addArgumentExecutor("box-xp", new BoxXpCommand());
        addArgumentExecutor("boxxp", new BoxXpCommand());
        addArgumentExecutor("bxp", new BoxXpCommand());

        addArgumentExecutor("reload", new ReloadCommand());

        addArgumentExecutor("resetplacedblocks", new ResetPlacedBlocksCommand());
        addArgumentExecutor("reset-placed-blocks", new ResetPlacedBlocksCommand());

        addArgumentExecutor("sus", new SusCommand());

        BoxPlugin plugin = getPlugin();

        plugin.getCommand("openbank").setExecutor(new OpenBankCommand());

        plugin.getCommand("openperkgui").setExecutor(new OpenPerkGuiCommand());

        plugin.getCommand("getownedperks").setExecutor(new GetOwnedPerksCommand());

        plugin.getCommand("keys").setExecutor(new KeyCommand());

        plugin.getCommand("resetperks").setExecutor(new ResetPerksCommand());

        plugin.getCommand("compress").setExecutor(new CompressCommand());

        plugin.getCommand("tree").setExecutor(new TreeCommand());

        plugin.getCommand("clearstreak").setExecutor(new ClearStreakCommand());

        plugin.getCommand("debug").setExecutor(new DebugCommand());

        plugin.getCommand("setmarketmultiplier").setExecutor(new SetMarketMultiplierCommand());

        plugin.getCommand("setperkupgradelevel").setExecutor(new SetPerkUpgradeLevelCommand());

        plugin.getCommand("addtag").setExecutor(new AddTagCommand());

        plugin.getCommand("claimlegacyrewards").setExecutor(new ClaimLegacyRewardsCommand());
    }

}
