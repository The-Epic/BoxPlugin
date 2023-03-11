package me.twostinkysocks.boxplugin.customitems;


import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.customitems.items.CustomItem;
import me.twostinkysocks.boxplugin.customitems.items.impl.SexShovel;
import me.twostinkysocks.boxplugin.customitems.items.impl.SuperSexShovel;
import me.twostinkysocks.boxplugin.customitems.items.impl.WarpSword;
import me.twostinkysocks.boxplugin.customitems.items.impl.WitherSkullSword;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomItemsMain implements CommandExecutor, TabCompleter {

    private ArrayList<CustomItem> items;

    public void onEnable() {
        items = new ArrayList<>();
        BoxPlugin.instance.getCommand("cgive").setExecutor(this);
        BoxPlugin.instance.getCommand("cgive").setTabCompleter(this);
        BoxPlugin.instance.getServer().getPluginManager().registerEvents(new Listeners(items), BoxPlugin.instance);

        registerItem(new WarpSword(this));
        registerItem(new SexShovel(this));
        registerItem(new SuperSexShovel(this));
        registerItem(new WitherSkullSword(this));
        BoxPlugin.instance.getLogger().info("Loaded custom items!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(command.getName().equals("cgive")) {
                if(!p.hasPermission("customitems.give")) {
                    p.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                    return true;
                }
                if(args.length == 0) {
                    p.sendMessage(ChatColor.RED + "You need to enter an item to give!");
                    return true;
                }
                boolean foundItem = false;
                for(CustomItem i : items) {
                    if(args[0].equals(i.getItemId())) {
                        foundItem = true;
                        p.getInventory().addItem(i.getItemStack());
                        p.sendMessage(ChatColor.GREEN + "Gave 1x " + i.getItemId());
                    }
                }
                if(!foundItem) {
                    p.sendMessage(ChatColor.RED + "That item doesn't exist!");
                }
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if(sender instanceof Player) {
            if(cmd.getName().equals("cgive")) {
                if(args.length == 1) {
                    StringUtil.copyPartialMatches(args[0], items.stream().map(i -> i.getItemId()).collect(Collectors.toList()), completions);
                    return completions;
                }
            }
        }
        return completions;
    }

    public void registerItem(CustomItem i) {
        items.add(i);
    }


}
