package me.twostinkysocks.boxplugin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.event.PlayerBoxXpUpdateEvent;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.Name;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class TerrainRegeneratorMain implements Listener, CommandExecutor, TabCompleter {

    private ArrayList<BukkitTask> tasks;

    private HashMap<String, ArrayList<UUID>> spawnedEntities;

    private YamlConfiguration config;

    public void onEnable() {
        tasks = new ArrayList<>();
        spawnedEntities = new HashMap<String, ArrayList<UUID>>();
        BoxPlugin.instance.getCommand("terrainregenerator").setExecutor(this);
        BoxPlugin.instance.getCommand("terrainregenerator").setTabCompleter(this);
        BoxPlugin.instance.getServer().getPluginManager().registerEvents(this, BoxPlugin.instance);
        this.load();
    }


    public int load() {
        for(BukkitTask task : tasks) {
            task.cancel();
        }
        tasks.clear();
        if(!BoxPlugin.instance.getDataFolder().exists()) {
            BoxPlugin.instance.getDataFolder().mkdir();
        }
        File schematicsFolder = new File(BoxPlugin.instance.getDataFolder(), "schematics");
        if(!schematicsFolder.exists()) {
            schematicsFolder.mkdir();
        }
        File config = new File(BoxPlugin.instance.getDataFolder(), "terrainregenerator.yml");
        if(!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = new YamlConfiguration();
        try {
            this.config.load(config);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, () -> {
            for(World world : Bukkit.getWorlds()) {
                for(Entity e : world.getEntities()) {
                    if(e.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "respawningmob"), PersistentDataType.INTEGER) && e.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "respawningmob"), PersistentDataType.INTEGER) == 1) {
                        e.remove();
                    }
                }
            }
        }, 20*60L);
        // schematics
        for(Object schem : this.config.getConfigurationSection("schematics").getKeys(false).toArray()) {
            int timerseconds = this.config.getInt("schematics." + schem + ".timer-seconds");
            File schemFile = new File(BoxPlugin.instance.getDataFolder(), "schematics/" + schem + ".schem");
            tasks.add(Bukkit.getScheduler()
                    .runTaskTimer(
                            BoxPlugin.instance,
                            () -> this.regenerateNew(schemFile),
                            timerseconds * 20,
                            timerseconds * 20
                    ));
            BoxPlugin.instance.getLogger().info("Started timer for schematic " + schem);
        }
        // entities
        for(Object entity : this.config.getConfigurationSection("entities").getKeys(false).toArray()) {
            int timerseconds = this.config.getInt("entities." + entity + ".timer-seconds");
            try {
                NBTTagCompound nbt = MojangsonParser.a(this.config.getString("entities." + entity + ".nbt"));
                EntityType entityType = EntityType.fromName(this.config.getString("entities." + entity + ".type"));
                int x = this.config.getInt("entities." + entity + ".x");
                int y = this.config.getInt("entities." + entity + ".y");
                int z = this.config.getInt("entities." + entity + ".z");
//                System.out.println("Entity " + entity + " located at " + x + " " + y + " " + z);
                tasks.add(Bukkit.getScheduler()
                        .runTaskTimer(
                                BoxPlugin.instance,
                                () -> this.spawnEntity((String) entity, entityType, nbt, x, y, z),
                                timerseconds * 20,
                                timerseconds * 20
                        ));
                BoxPlugin.instance.getLogger().info("Started timer for entity " + entity + " with type " + entityType);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
        tasks.add(Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, () -> {
            for(World world: Bukkit.getWorlds()) {
                for(Entity entity : world.getEntities()) {
                    if(entity.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "respawningmob"), PersistentDataType.INTEGER) && entity.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "respawningmob"), PersistentDataType.INTEGER) == 1) {
                        String name = entity.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "respawningmobid"), PersistentDataType.STRING);
                        if(this.config.isSet("entities." + name + ".maxradius")) {
                            int x = this.config.getInt("entities." + name + ".x");
                            int y = this.config.getInt("entities." + name + ".y");
                            int z = this.config.getInt("entities." + name + ".z");
                            int radius = this.config.getInt("entities." + name + ".maxradius");
                            if(entity.getLocation().distance(new Location(Bukkit.getWorld(this.config.getString("entities." + name + ".world")), x, y, z)) > radius) {
                                entity.teleport(new Location(Bukkit.getWorld(this.config.getString("entities." + name + ".world")), x, y, z));
                            }
                        }
                    }
                }
            }
        }, 200L, 200L));

        return this.config.getConfigurationSection("schematics").getKeys(false).toArray().length + this.config.getConfigurationSection("entities").getKeys(false).toArray().length;
    }

    private void spawnEntity(String name, EntityType entityType, NBTTagCompound nbt, int x, int y, int z) {
        if(spawnedEntities.containsKey(name) && this.config.getBoolean("entities." + name + ".kill-existing")) {
            for(UUID uuid : new ArrayList<>(spawnedEntities.get(name))) {
                if(Bukkit.getEntity(uuid) != null) {
//                    System.out.println("Removing " + uuid);
                    Bukkit.getEntity(uuid).remove();
                    spawnedEntities.get(name).remove(uuid);
                }
            }
        }
        Location loc = new Location(Bukkit.getWorld(this.config.getString("entities." + name + ".world")), x, y, z);
        for(int i = 0; i < this.config.getInt("entities." + name + ".count"); i++) {
            CraftEntity e = (CraftEntity) Bukkit.getWorld(this.config.getString("entities." + name + ".world")).spawnEntity(loc, entityType);
            if(!spawnedEntities.containsKey(name) && this.config.getBoolean("entities." + name + ".kill-existing")) {
                spawnedEntities.put(name, new ArrayList<>());
            }
            spawnedEntities.get(name).add(e.getUniqueId());
            if(this.config.getInt("entities." + name + ".xp") != 0) {
                e.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER, this.config.getInt("entities." + name + ".xp"));
            }
            e.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "respawningmob"), PersistentDataType.INTEGER, 1);
            e.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "respawningmobid"), PersistentDataType.STRING, name);
            e.getHandle().g(nbt); // Entity#load()
            e.teleport(loc); // loading nbt resets loc to 0
        }
        if(this.config.getString("entities." + name + ".respawn-broadcast") != null) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', this.config.getString("entities." + name + ".respawn-broadcast")));
        }
    }

    private void regenerateNew(File schematic) {
        if(schematic.exists()) {
            String filename = schematic.getName().replace(".schem", "");
            Clipboard clipboard;
            ClipboardFormat format = ClipboardFormats.findByFile(schematic);
            try(ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
                clipboard = reader.read();
                EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld(this.config.getString("schematics." + filename + ".world"))));
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(
                                this.config.getInt("schematics." + filename + ".x"),
                                this.config.getInt("schematics." + filename + ".y"),
                                this.config.getInt("schematics." + filename + ".z")
                        ))
                        .copyEntities(this.config.getBoolean("schematics." + filename + ".includeentities"))
                        .ignoreAirBlocks(this.config.getBoolean("schematics." + filename + ".ignoreairblocks"))
                        .build();
                Operations.complete(operation);
                editSession.flushSession();
                if(this.config.getString("schematics." + filename + ".respawn-broadcast") != null) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', this.config.getString("schematics." + filename + ".respawn-broadcast")));
                }
                if(this.config.getString("schematics." + filename + ".command") != null) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), this.config.getString("schematics." + filename + ".command"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            BoxPlugin.instance.getLogger().severe("Schematic: " + schematic + " was not found");
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if(spawnedEntities.containsValue(e.getEntity().getUniqueId())) {
            spawnedEntities.values().remove(e.getEntity().getUniqueId());
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(label.equals("terrainregenerator")) {
                if(args.length == 0 || !args[0].equals("reload")) {
                    p.sendMessage(ChatColor.GRAY + "Use /terrainregenerator reload to reload config files");
                } else {
                    int num = load();
                    p.sendMessage("Reloaded " + num + " schematics!");
                }
                return true;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return List.of("reload");
    }
}
