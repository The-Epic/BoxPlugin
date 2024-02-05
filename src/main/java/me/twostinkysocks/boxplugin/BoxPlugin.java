package me.twostinkysocks.boxplugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.twostinkysocks.boxplugin.commands.impl.CommandManager;
import me.twostinkysocks.boxplugin.compressor.Compressor;
import me.twostinkysocks.boxplugin.customitems.CustomItemsMain;
import me.twostinkysocks.boxplugin.event.Listeners;
import me.twostinkysocks.boxplugin.event.PacketListeners;
import me.twostinkysocks.boxplugin.manager.MarketManager;
import me.twostinkysocks.boxplugin.manager.PVPManager;
import me.twostinkysocks.boxplugin.manager.PerksManager;
import me.twostinkysocks.boxplugin.manager.ScoreboardManager;
import me.twostinkysocks.boxplugin.manager.XPManager;
import me.twostinkysocks.boxplugin.util.MarketJob;
import me.twostinkysocks.boxplugin.util.PlaceholderAPIExpansion;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.key.KeyManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class BoxPlugin extends JavaPlugin {
    private ProtocolManager protocolManager;

    public static BoxPlugin instance;

    public HashMap<Material, Integer> blockExperience;
    public HashMap<EntityType, Integer> entityExperience;
    public ArrayList<Location> placedBlocks;
    private final List<UUID> debugEnabled = new ArrayList<>();

    private ScoreboardManager scoreboardManager;

    private XPManager xpManager;

    private PVPManager pvpManager;

    private PerksManager perksManager;

    private KeyManager keyManager;

    private ExcellentCrates excellentCrates;

    private Compressor compressor;

    private MarketManager marketManager;

    private Economy econ = null;

    // player who killed, <player who was killed, times>
    private HashMap<UUID, HashMap<UUID, Integer>> killsInHour;


    private YamlConfiguration offlineXPFile;

//    private StateFlag entityInteract;

    @Override
    public void onDisable() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "PREVIOUS_HEALTH"), PersistentDataType.DOUBLE, p.getHealth());
        }
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

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
        killsInHour = new HashMap<>();
        placedBlocks = new ArrayList<>();
        scoreboardManager = new ScoreboardManager();
        pvpManager = new PVPManager();
        xpManager = new XPManager();
        perksManager = new PerksManager();
        compressor = new Compressor();
        marketManager = new MarketManager();

        excellentCrates = (ExcellentCrates) getServer().getPluginManager().getPlugin("ExcellentCrates");
        keyManager = excellentCrates.getKeyManager();

        getMarketManager().initializeMarketMultiplier();

        getCommand("aetherconquest").setExecutor(new CommandManager());

        getServer().getPluginManager().registerEvents(new Listeners(), this);
        load();
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIExpansion().register();
        }
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            killsInHour.clear();
        }, 20 * 60 * 60 * 2, 20 * 60 * 60 * 2);
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for(World world : Bukkit.getWorlds()) {
                for(Entity e : world.getEntities()) {
                    if(e instanceof Zombie) {
                        ((Zombie) e).setConversionTime(-1);
                    }
                }
            }
        }, 0, 100);
        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(entity instanceof Slime) { // slime and magma
                    entity.remove();
                }
            }
        }
        new PacketListeners();
        new TerrainRegeneratorMain().onEnable();
        new CustomItemsMain().onEnable();


        JobKey jobKeyA = new JobKey("market", "group1");
        JobDetail jobA = JobBuilder.newJob(MarketJob.class).withIdentity(jobKeyA)
                .build();

        Trigger trigger1 = TriggerBuilder
                .newTrigger()
                .withIdentity("dummyTriggerName1", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("43 27 */4 * * ?")) // every 4 hours offset by random time
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?")) // every minute
                .build();

        Scheduler scheduler;
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(jobA, trigger1);
            System.out.println("Started market task");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
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

    public HashMap<UUID, HashMap<UUID, Integer>> getKillsInHour() {
        return killsInHour;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public XPManager getXpManager() {
        return xpManager;
    }

    public LuckPerms getLuckPerms() { return LuckPermsProvider.get(); }

    public PVPManager getPvpManager() {
        return pvpManager;
    }

    public PerksManager getPerksManager() {
        return perksManager;
    }

    public Economy getEconomy() {
        return econ;
    }

    public MarketManager getMarketManager() {
        return marketManager;
    }

    public KeyManager getKeyManager() { return keyManager; }

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

    public void resetPlacedBlocks() {
        placedBlocks.clear();
    }

    public void enableDebug(Player player) {
        this.debugEnabled.add(player.getUniqueId());
    }

    public void disableDebug(Player player) {
        this.debugEnabled.remove(player.getUniqueId());
    }

    public boolean isDebugEnabled(Player player) {
        UUID uuid = this.debugEnabled.stream().filter(id -> id.equals(player.getUniqueId())).findFirst().orElse(null);
        return uuid != null;
    }
}
