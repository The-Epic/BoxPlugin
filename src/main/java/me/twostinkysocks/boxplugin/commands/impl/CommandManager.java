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

        // TODO add enforced permcheck for commands that bypass subcommands
        OpenBankComand openBankCommand = new OpenBankComand();
        plugin.getCommand("openbank").setExecutor(openBankCommand);
        addArgumentExecutor("openbank", openBankCommand);

        OpenPerkGuiCommand openPerkGuiCommand = new OpenPerkGuiCommand();
        plugin.getCommand("openperkgui").setExecutor(openPerkGuiCommand);
        addArgumentExecutor("openperkgui", openPerkGuiCommand);

        GetOwnedPerksCommand getOwnedPerksCommand = new GetOwnedPerksCommand();
        plugin.getCommand("getownedperks").setExecutor(getOwnedPerksCommand);
        addArgumentExecutor("getownedperks", getOwnedPerksCommand);

        plugin.getCommand("keys").setExecutor(new KeyCommand());

        plugin.getCommand("resetperks").setExecutor(new ResetPerksCommand());

        plugin.getCommand("compress").setExecutor(new CompressCommand());

        addArgumentExecutor("tree", new TreeCommand());
        plugin.getCommand("tree").setExecutor(new TreeCommand());
    }

}
