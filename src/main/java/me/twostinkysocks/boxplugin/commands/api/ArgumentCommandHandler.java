package me.twostinkysocks.boxplugin.commands.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
public class ArgumentCommandHandler extends SimpleCommandHandler {
    private final Map<String, SimpleCommandHandler> subcommands = new HashMap<>();
    private final Supplier<String> noPermissionMessage;
    private final Supplier<String> usageMessage;

    private CommandExecutor defaultExecutor;

    public ArgumentCommandHandler(String permission, Supplier<String> noPermissionMessage, Supplier<String> usageMessage) {
        super(permission);
        this.noPermissionMessage = noPermissionMessage;
        this.usageMessage = usageMessage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!hasAnyPermission(sender)) {
            sender.sendMessage(noPermissionMessage.get());
            return true;
        }

        if (args.length == 0) {
            if (this.defaultExecutor != null) {
                return this.defaultExecutor.onCommand(sender, command, alias, args);
            } else {
                sendUsage(sender, "none");
                return true;
            }
        } else {
            SimpleCommandHandler executor = this.subcommands.get(args[0]);

            if (executor == null) {
                sendUsage(sender, args[0]);
                return true;
            }

            if (!hasAnySubcommandPermission(sender, executor)) {
                sender.sendMessage(noPermissionMessage.get());
                return true;
            }

            return executor.onCommand(sender, command, alias, Arrays.copyOfRange(args, 1, args.length + 1));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[0], getVisibleSubcommands(sender), suggestions);
            return suggestions;
        } else if (args.length > 1) {
            SimpleCommandHandler executor = this.subcommands.get(args[0]);
            if (executor != null)
                return executor.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length + 1));
        }
        return Collections.emptyList();
    }

    private void sendUsage(CommandSender sender, String arg) {
        String msg = this.usageMessage.get();
        msg = msg.replace("%arg%", arg);
        msg = msg.replace("%args%", String.join(", ", getVisibleSubcommands(sender)));

        sender.sendMessage(msg);
    }

    public void addArgumentExecutor(String arg, SimpleCommandHandler executor) {
        this.subcommands.put(arg, executor);
    }

    public void setDefault(SimpleCommandHandler executor) {
        this.defaultExecutor = executor;
    }

    private List<String> getVisibleSubcommands(CommandSender sender) {
        List<String> subcommands = new ArrayList<>();
        for (Entry<String, SimpleCommandHandler> entry : this.subcommands.entrySet()) {
            if (hasAnySubcommandPermission(sender, entry.getValue()))
                subcommands.add(entry.getKey());
        }
        return subcommands;
    }

    private boolean hasAnyPermission(CommandSender sender) {
        if (sender.hasPermission(getPermission())) {
            return true;
        }
        for (String permission : getExtraPermissions()) {
            if (sender.hasPermission(permission))
                return true;
        }
        return false;
    }

    private boolean hasAnySubcommandPermission(CommandSender sender, SimpleCommandHandler executor) {
        if (sender.hasPermission(executor.getPermission())) {
            return true;
        }

        for (String permission : executor.getExtraPermissions()) {
            if (sender.hasPermission(permission))
                return true;
        }
        return false;
    }
}