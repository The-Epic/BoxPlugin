package me.twostinkysocks.boxplugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import me.twostinkysocks.boxplugin.event.Listeners;
import me.twostinkysocks.boxplugin.event.PlayerBoxXpUpdateEvent;
import me.twostinkysocks.boxplugin.manager.PVPManager;
import me.twostinkysocks.boxplugin.manager.ScoreboardManager;
import me.twostinkysocks.boxplugin.manager.XPManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class BoxPlugin extends JavaPlugin implements CommandExecutor, TabCompleter {
    private ProtocolManager protocolManager;

    public static BoxPlugin instance;

    public HashMap<Material, Integer> blockExperience;
    public ArrayList<Location> placedBlocks;

    private ScoreboardManager scoreboardManager;

    private XPManager xpManager;

    private PVPManager pvpManager;


    @Override
    public void onEnable() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        placedBlocks = new ArrayList<Location>();
        scoreboardManager = new ScoreboardManager();
        pvpManager = new PVPManager();
        xpManager = new XPManager();
        getCommand("sus").setExecutor(this);
        getCommand("boxplugin").setExecutor(this);
        getCommand("boxxp").setExecutor(this);
        getCommand("boxxp").setTabCompleter(this);
        getCommand("boxplugin").setTabCompleter(this);
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        load();
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public XPManager getXpManager() {
        return xpManager;
    }

    public LuckPerms getLuckPerms() {return LuckPermsProvider.get();}

    public PVPManager getPvpManager() {
        return pvpManager;
    }

    public void load() {
        blockExperience = new HashMap<Material, Integer>();
        if(!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        File config = new File(this.getDataFolder(), "config.yml");
        if(!config.exists()) {
            saveDefaultConfig();
        }
        reloadConfig();
        for(Object key : getConfig().getConfigurationSection("experience.blocks").getKeys(false).toArray()) {
            blockExperience.put(Material.getMaterial(((String) key).toUpperCase(Locale.ROOT)), getConfig().getInt("experience.blocks." + key + ".amount"));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if(label.equals("sus")) {
            if(!p.hasPermission("boxplugin.crash")) {
                p.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }
            if(args.length == 0 || Bukkit.getPlayer(args[0]) == null) {
                p.sendMessage(ChatColor.RED + "Usage: /sus <player>");
                return true;
            }
            Player to = Bukkit.getPlayer(args[0]);
            for(int i = 0; i < 10000; i++) {
                PacketContainer fakeEXP = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB);
                fakeEXP.getIntegers().write(0, 1000+i);
                fakeEXP.getDoubles()
                        .write(0, to.getLocation().getX())
                        .write(1, to.getLocation().getY())
                        .write(2, to.getLocation().getZ());
                try {
                    protocolManager.sendServerPacket(to, fakeEXP);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(
                            "Cannot send packet " + fakeEXP, e);
                }
            }
        } else if(label.equals("boxplugin")) {
            if(!p.hasPermission("boxplugin.reload")) {
                p.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }
            if(args.length == 0) {
                p.sendMessage(ChatColor.RED + "Usage: /boxplugin reload");
                return true;
            }
            load();
            p.sendMessage(ChatColor.AQUA + "Reloaded config!");
        } else if(label.equals("boxxp") || label.equals("bxp")) {
            if(!p.hasPermission("boxplugin.xpcommands")) {
                p.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }
            if(args.length == 0 || (!args[0].equals("get") && !args[0].equals("set"))) {
                p.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set> [player] [amount]");
                return true;
            }
            if(args.length == 1 || Bukkit.getPlayer(args[1]) == null) {
                p.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set> [player] [amount]");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if(args[0].equals("get")) {
                p.sendMessage(target.getName() + " has " + getXpManager().getXP(target) + " xp");
            } else { // set
                if(args.length == 2) {
                    p.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set> [player] [amount]");
                    return true;
                }
                try {
                    int xp = Integer.parseInt(args[2]);
                    int existingxp = getXpManager().getXP(target);
                    getXpManager().setXP(target, xp);
                    Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(target, existingxp, xp));
                    p.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s xp to " + xp);
                } catch(NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set> [player] [amount]");
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if(alias.equals("boxplugin")) {
            StringUtil.copyPartialMatches(args[0], List.of("reload"), completions);
            return completions;
        } else if(alias.equals("boxxp") || alias.equals("bxp")) {
            if(args.length == 1) {
                StringUtil.copyPartialMatches(args[0], List.of("get", "set"), completions);
                return completions;
            } else if(args.length == 2) {
                StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList()), completions);
                return completions;
            }
        } else if(alias.equals("sus")) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList()), completions);
            return completions;
        }
        return List.of();
    }
}
