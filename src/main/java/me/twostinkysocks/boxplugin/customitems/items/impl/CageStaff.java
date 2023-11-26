package me.twostinkysocks.boxplugin.customitems.items.impl;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.customitems.CustomItemsMain;
import me.twostinkysocks.boxplugin.customitems.items.CustomItem;
import me.twostinkysocks.boxplugin.manager.PerksManager;
import me.twostinkysocks.boxplugin.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class CageStaff extends CustomItem {

    private HashMap<UUID, Long> cooldown;

    public static HashMap<UUID, HashSet<Location>> cageBlocks = new HashMap<>();

    public CageStaff(CustomItemsMain plugin) {
        super(
                "§6Cage Staff",
                "CAGE_STAFF",
                Material.BLAZE_ROD,
                plugin,
                ""
        );
        cooldown = new HashMap<>();
        setClick((e, a) -> {
            Player p = e.getPlayer();
            if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
                if(p.hasPermission("customitems.cooldownbypass") || !cooldown.containsKey(p.getUniqueId()) || cooldown.get(p.getUniqueId()) < System.currentTimeMillis()) {
                    cooldown.put(p.getUniqueId(), System.currentTimeMillis() + (long)(1000 * 60 * 5 * (BoxPlugin.instance.getPerksManager().getSelectedMegaPerks(p).contains(PerksManager.MegaPerk.MEGA_COOLDOWN_REDUCTION) ? 0.5 : 1))); // 10 seconds
                    Set<Location> sphereLocations = Util.sphere(p.getLocation(), 20, true);
                    UUID cageID = UUID.randomUUID();
                    HashSet<Location> placedBlocks = new HashSet<>();
                    RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
                    for(Location l : sphereLocations) {
                        if(l.getBlock().getType() != Material.AIR) continue;
                        ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(l));
                        StateFlag.State flag = regions.queryValue(WorldGuardPlugin.inst().wrapPlayer(p), Flags.BUILD);
                        if(flag == StateFlag.State.ALLOW) {
                            p.getWorld().getBlockAt(l).setType(Material.GLASS);
                            placedBlocks.add(l);
                        }
                    }
                    if(!placedBlocks.isEmpty()) {
                        cageBlocks.put(cageID, placedBlocks);
                        Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, () -> {
                            for(Location l : cageBlocks.get(cageID)) {
                                l.getWorld().getBlockAt(l).setType(Material.AIR);
                            }
                            cageBlocks.remove(cageID);
                        }, 20*10);
                    }
                } else {
                    BigDecimal bd = new BigDecimal(((double)(cooldown.get(p.getUniqueId()) - System.currentTimeMillis()))/1000.0);
                    bd = bd.round(new MathContext(4));
                    p.sendMessage(ChatColor.RED + "That's too fast! Wait " + bd.doubleValue() + " more seconds!");
                    p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 3.0F, 1.0F);
                }
            }
        });
    }
}
