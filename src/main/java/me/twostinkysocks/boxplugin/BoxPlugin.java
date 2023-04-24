package me.twostinkysocks.boxplugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import me.twostinkysocks.boxplugin.compressor.Compressor;
import me.twostinkysocks.boxplugin.customitems.CustomItemsMain;
import me.twostinkysocks.boxplugin.event.Listeners;
import me.twostinkysocks.boxplugin.event.PacketListeners;
import me.twostinkysocks.boxplugin.event.PlayerBoxXpUpdateEvent;
import me.twostinkysocks.boxplugin.manager.PVPManager;
import me.twostinkysocks.boxplugin.manager.PerksManager;
import me.twostinkysocks.boxplugin.manager.ScoreboardManager;
import me.twostinkysocks.boxplugin.manager.XPManager;
import me.twostinkysocks.boxplugin.util.PlaceholderAPIExpansion;
import me.twostinkysocks.boxplugin.util.Util;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import su.nexmedia.engine.api.config.JYML;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.key.CrateKey;
import su.nightexpress.excellentcrates.key.KeyManager;

import javax.naming.Name;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
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
    public HashMap<EntityType, Integer> entityExperience;
    public ArrayList<Location> placedBlocks;

    private ScoreboardManager scoreboardManager;

    private XPManager xpManager;

    private PVPManager pvpManager;

    private PerksManager perksManager;

    private KeyManager keyManager;

    private ExcellentCrates excellentCrates;

    private Compressor compressor;


    private YamlConfiguration offlineXPFile;

//    private StateFlag entityInteract;

    public void load() {
        blockExperience = new HashMap<>();
        entityExperience = new HashMap<>();
        if(!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        File config = new File(this.getDataFolder(), "config.yml");
        if(!config.exists()) {
            saveDefaultConfig();
        }
        reloadConfig();
        File data = new File(getDataFolder(), "offlinexp.yml");
        if(!data.exists()){
            data.getParentFile().mkdirs();
            try {
                data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        offlineXPFile = new YamlConfiguration();
        try {
            offlineXPFile.load(data);
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        for(Object key : getConfig().getConfigurationSection("experience.blocks").getKeys(false).toArray()) {
            blockExperience.put(Material.getMaterial(((String) key).toUpperCase(Locale.ROOT)), getConfig().getInt("experience.blocks." + key + ".amount"));
        }
        for(Object key : getConfig().getConfigurationSection("experience.entities").getKeys(false).toArray()) {
            entityExperience.put(EntityType.fromName((String) key), getConfig().getInt("experience.entities." + key + ".amount"));
        }
        for(Player p : Bukkit.getOnlinePlayers()) {
            getScoreboardManager().updatePlayerScoreboard(p);
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        placedBlocks = new ArrayList<Location>();
        scoreboardManager = new ScoreboardManager();
        pvpManager = new PVPManager();
        xpManager = new XPManager();
        perksManager = new PerksManager();
        compressor = new Compressor();

        excellentCrates = (ExcellentCrates) getServer().getPluginManager().getPlugin("ExcellentCrates");
        keyManager = excellentCrates.getKeyManager();

        getCommand("boxgivecommonkey").setExecutor(this);
        getCommand("sus").setExecutor(this);
        getCommand("openperkgui").setExecutor(this);
        getCommand("getselectedperks").setExecutor(this);
        getCommand("boxgivecommonkey").setExecutor(this);
        getCommand("getownedperks").setExecutor(this);
        getCommand("boxplugin").setExecutor(this);
        getCommand("boxxp").setExecutor(this);
        getCommand("boxxp").setTabCompleter(this);
        getCommand("boxplugin").setTabCompleter(this);
        getCommand("resetplacedblocks").setExecutor(this);
        getCommand("key").setExecutor(this);
        getCommand("key").setTabCompleter(this);
        getCommand("compress").setExecutor(this);
        getCommand("clearstreak").setExecutor(this);
        getCommand("clearstreak").setTabCompleter(this);
        getCommand("tree").setExecutor(this);
        getCommand("claimlegacyrewards").setExecutor(this);
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        load();
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIExpansion().register();
        }
        new PacketListeners();
        new TerrainRegeneratorMain().onEnable();
        new CustomItemsMain().onEnable();
    }
//
//    @Override
//    public void onLoad() {
//        FlagRegistry fr = WorldGuard.getInstance().getFlagRegistry();
//        try{
//            StateFlag flag = new StateFlag("entityInteract", true);
//            fr.register(flag);
//            entityInteract = flag;
//        } catch(FlagConflictException e) {
//            Flag<?> existing = fr.get("entityInteract");
//            if(existing instanceof StateFlag) {
//                entityInteract = (StateFlag) existing;
//            }
//        }
//    }

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

    public PerksManager getPerksManager() {
        return perksManager;
    }

    public KeyManager getKeyManager() {return keyManager;}

    public ExcellentCrates getExcellentCrates() {return excellentCrates;}

    public YamlConfiguration getOfflineXPFile() {
        return offlineXPFile;
    }

    public Compressor getCompressor() {
        return compressor;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

//    public StateFlag getEntityInteractFlag() {
//        return entityInteract;
//    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            if(label.equals("boxxp") || label.equals("bxp")) {
                if(args.length == 0 || (!args[0].equals("get") && !args[0].equals("set") && !args[0].equals("add"))) {
                    sender.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                    return true;
                }
                if(args.length == 1 || Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if(args[0].equals("get")) {
                    sender.sendMessage(target.getName() + " has " + getXpManager().getXP(target) + " xp");
                } else if(args[0].equals("set")){
                    if(args.length == 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                        return true;
                    }
                    try {
                        int xp = Integer.parseInt(args[2]);
                        int existingxp = getXpManager().getXP(target);
                        getXpManager().setXP(target, xp);
                        Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(target, existingxp, xp));
                        sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s xp to " + xp);
                    } catch(NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                        return true;
                    }
                } else { // add
                    if(args.length == 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                        return true;
                    }
                    try {
                        int xp = Integer.parseInt(args[2]);
                        int existingxp = getXpManager().getXP(target);
                        getXpManager().setXP(target, existingxp+xp);
                        Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(target, existingxp, existingxp+xp));
                        sender.sendMessage(ChatColor.GREEN + "Added " + xp + " xp to " + target.getName());
                    } catch(NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                        return true;
                    }
                }
            } else if(label.equals("resetplacedblocks")) {
                if(!sender.hasPermission("boxplugin.resetplacedblocks")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
                placedBlocks = new ArrayList<>();
                sender.sendMessage(ChatColor.GREEN + "Reset placed blocks!");
            }
        } else {
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
                if(args.length == 0 || (!args[0].equals("get") && !args[0].equals("set") && !args[0].equals("add"))) {
                    p.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                    return true;
                }
                if(args.length == 1 || Bukkit.getPlayer(args[1]) == null) {
                    p.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if(args[0].equals("get")) {
                    p.sendMessage(target.getName() + " has " + getXpManager().getXP(target) + " xp");
                } else if(args[0].equals("set")){
                    if(args.length == 2) {
                        p.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                        return true;
                    }
                    try {
                        int xp = Integer.parseInt(args[2]);
                        int existingxp = getXpManager().getXP(target);
                        getXpManager().setXP(target, xp);
                        Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(target, existingxp, xp, true));
                        p.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s xp to " + xp);
                    } catch(NumberFormatException e) {
                        p.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                        return true;
                    }
                } else { // add
                    if(args.length == 2) {
                        p.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                        return true;
                    }
                    try {
                        int xp = Integer.parseInt(args[2]);
                        int existingxp = getXpManager().getXP(target);
                        getXpManager().setXP(target, existingxp+xp);
                        Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(target, existingxp, existingxp+xp, true));
                        p.sendMessage(ChatColor.GREEN + "Added " + xp + " xp to " + target.getName());
                    } catch(NumberFormatException e) {
                        p.sendMessage(ChatColor.RED + "Usage: /boxxp <get|set|add> [player] [amount]");
                        return true;
                    }
                }
            } else if(label.equals("openperkgui")) {
                ChestGui gui = new ChestGui(6, "Perks");
                OutlinePane pane = new OutlinePane(0, 0, 9, 6);
                pane.addItem(new GuiItem(new ItemStack(Material.SKELETON_SKULL)));
                gui.addPane(pane);
                getPerksManager().openMainGui(p);
            } else if(label.equals("getownedperks")) {
                p.sendMessage(String.join("\n", getPerksManager().getPurchasedPerks(p).stream().map(pe -> pe.instance.getKey()).collect(Collectors.toList())));
            } else if(label.equals("getselectedperks")) {
                p.sendMessage(String.join("\n", getPerksManager().getSelectedPerks(p).stream().map(pe -> pe.instance.getKey()).collect(Collectors.toList())));            } else if(label.equals("boxgivecommonkey")) {
                if(!p.hasPermission("boxplugin.givekey")) {
                    p.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
                File commonConfig = new File(BoxPlugin.instance.getExcellentCrates().getDataFolder().getPath(), "/keys/common.yml");
                BoxPlugin.instance.getKeyManager().giveKey(p, new CrateKey(BoxPlugin.instance.getExcellentCrates(), new JYML(commonConfig)), 1);
            } else if(label.equals("resetperks")) {
                if(!p.hasPermission("boxplugin.resetperks")) {
                    p.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
                if(args.length == 0) {
                    getPerksManager().resetPerks(p);
                    p.sendMessage(ChatColor.GREEN + "Reset your perks to default!");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
                } else if(args.length >= 1) {
                    if(Bukkit.getPlayer(args[0]) == null) {
                        p.sendMessage(ChatColor.RED + "Invalid command! Usage: /resetperks <player> ");
                        return true;
                    }
                    getPerksManager().resetPerks(Bukkit.getPlayer(args[0]));
                    Bukkit.getPlayer(args[0]).getOpenInventory().close();
                    p.sendMessage(ChatColor.GREEN + "Reset " + args[0] + "'s perks to default!");
                    Bukkit.getPlayer(args[0]).sendMessage(ChatColor.GREEN + "Your perks were reset by an admin.");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
                    Bukkit.getPlayer(args[0]).playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
                }
            } else if(label.equals("resetplacedblocks")) {
                if(!p.hasPermission("boxplugin.resetplacedblocks")) {
                    p.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
                placedBlocks = new ArrayList<>();
                p.sendMessage(ChatColor.GREEN + "Reset placed blocks!");
            } else if(label.equals("key")) {
                if(args.length == 0) {
                    p.sendMessage(ChatColor.RED + "Usage: /key <tier>");
                    return true;
                }
                if(args[0].equals("common")) {
                    if(!p.hasPermission("boxplugin.key.common")) {
                        p.sendMessage(ChatColor.RED + "You don't have permission to get that key type!");
                        return true;
                    }
                    File commonConfig = new File(BoxPlugin.instance.getExcellentCrates().getDataFolder().getPath(), "/keys/common.yml");
                    BoxPlugin.instance.getKeyManager().giveKey(p, new CrateKey(BoxPlugin.instance.getExcellentCrates(), new JYML(commonConfig)), 1);
                    p.sendMessage(ChatColor.GREEN + "Gave 1x Common Key!");
                } else if(args[0].equals("rare")) {
                    if(!p.hasPermission("boxplugin.key.rare")) {
                        p.sendMessage(ChatColor.RED + "You don't have permission to get that key type!");
                        return true;
                    }
                    File rareConfig = new File(BoxPlugin.instance.getExcellentCrates().getDataFolder().getPath(), "/keys/rare.yml");
                    BoxPlugin.instance.getKeyManager().giveKey(p, new CrateKey(BoxPlugin.instance.getExcellentCrates(), new JYML(rareConfig)), 1);
                    p.sendMessage(ChatColor.GREEN + "Gave 1x Rare Key!");
                } else if(args[0].equals("epic")) {
                    if(!p.hasPermission("boxplugin.key.epic")) {
                        p.sendMessage(ChatColor.RED + "You don't have permission to get that key type!");
                        return true;
                    }
                    File epicConfig = new File(BoxPlugin.instance.getExcellentCrates().getDataFolder().getPath(), "/keys/epic.yml");
                    BoxPlugin.instance.getKeyManager().giveKey(p, new CrateKey(BoxPlugin.instance.getExcellentCrates(), new JYML(epicConfig)), 1);
                    p.sendMessage(ChatColor.GREEN + "Gave 1x Epic Key!");
                } else if(args[0].equals("legendary")) {
                    if(!p.hasPermission("boxplugin.key.legendary")) {
                        p.sendMessage(ChatColor.RED + "You don't have permission to get that key type!");
                        return true;
                    }
                    File legendaryConfig = new File(BoxPlugin.instance.getExcellentCrates().getDataFolder().getPath(), "/keys/legendary_crate.yml");
                    BoxPlugin.instance.getKeyManager().giveKey(p, new CrateKey(BoxPlugin.instance.getExcellentCrates(), new JYML(legendaryConfig)), 1);
                    p.sendMessage(ChatColor.GREEN + "Gave 1x Legendary Key!");
                } else {
                    p.sendMessage(ChatColor.RED + "Usage: /key <tier>");
                    return true;
                }
            } else if(label.equals("compress")) {
                if(!p.hasPermission("boxplugin.compress")) {
                    p.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
                getCompressor().compressAll(p);
                getCompressor().compressAll(p);
                p.sendMessage(ChatColor.AQUA + "Compressed your inventory!");
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 2f);
            } else if(label.equals("clearstreak")) {
                if(!p.hasPermission("boxplugin.clearstreak")) {
                    p.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
                if(args.length == 0 || Bukkit.getPlayer(args[0]) == null) {
                    p.sendMessage(ChatColor.RED + "Usage: /clearstreak <player>");
                    return true;
                }
                BoxPlugin.instance.getPvpManager().resetStreak(Bukkit.getPlayer(args[0]));
                p.sendMessage(ChatColor.GREEN + "Cleared streak!");
            } else if(label.equals("tree")) {
                if(!p.hasPermission("boxplugin.tree")) {
                    p.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
                int x = p.getLocation().getBlockX();
                int y = p.getLocation().getBlockY() + 1;
                int z = p.getLocation().getBlockZ();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "place feature oak " + x + " " + y + " " + z);
                p.getWorld().getBlockAt(x, y-1, z).setType(Material.OAK_LOG);
                p.sendMessage(ChatColor.GREEN + "Placed tree!");
            } else if(label.equals("claimlegacyrewards")) {
                if(p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "legacylevelscompensated"), PersistentDataType.INTEGER)) {
                    p.sendMessage(ChatColor.RED + "You have no rewards to claim!");
                } else {
                    int total = BoxPlugin.instance.getXpManager().getCumulativeLevelUpReward(BoxPlugin.instance.getXpManager().getLevel(p));
                    p.getInventory().addItem(Util.gigaCoin(total));
                    p.sendMessage(ChatColor.GREEN + "Claimed " + total + " giga coins!");
                    p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "legacylevelscompensated"), PersistentDataType.INTEGER, 1);
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
                StringUtil.copyPartialMatches(args[0], List.of("get", "set", "add"), completions);
                return completions;
            } else if(args.length == 2) {
                StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList()), completions);
                return completions;
            }
        } else if(alias.equals("sus")) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList()), completions);
            return completions;
        } else if(alias.equals("resetperks")) {
            if(args.length == 1) {
                StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList()), completions);
                return completions;
            }
        } else if(alias.equals("key")) {
            if(args.length == 1) {
                ArrayList<String> keys = new ArrayList<>();
                if(sender.hasPermission("boxplugin.key.common")) {
                    keys.add("common");
                }
                if(sender.hasPermission("boxplugin.key.rare")) {
                    keys.add("rare");
                }
                if(sender.hasPermission("boxplugin.key.epic")) {
                    keys.add("epic");
                }
                if(sender.hasPermission("boxplugin.key.legendary")) {
                    keys.add("legendary");
                }
                StringUtil.copyPartialMatches(args[0], keys, completions);
                return completions;
            }
        }
        return List.of();
    }
}
