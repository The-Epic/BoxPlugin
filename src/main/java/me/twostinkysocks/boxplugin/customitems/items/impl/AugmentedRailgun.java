package me.twostinkysocks.boxplugin.customitems.items.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.customitems.CustomItemsMain;
import me.twostinkysocks.boxplugin.customitems.items.CustomItem;
import me.twostinkysocks.boxplugin.util.Laser;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class AugmentedRailgun extends CustomItem {

    private HashMap<UUID, Long> cooldown;
    private HashMap<UUID, Integer> particleTimers;

    public AugmentedRailgun(CustomItemsMain plugin) {
        super(
                ChatColor.WHITE + "Augmented Railgun",
                "AUGMENTED_RAILGUN",
                Material.DIAMOND_HOE,
                plugin
        );
        cooldown = new HashMap<>();
        particleTimers = new HashMap<>();
        setClick((e, a) -> {
            Player p = e.getPlayer();
            if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
                e.setCancelled(true);
                if(p.hasPermission("customitems.cooldownbypass") || !cooldown.containsKey(p.getUniqueId()) || cooldown.get(p.getUniqueId()) < System.currentTimeMillis()) {
                    cooldown.put(p.getUniqueId(), System.currentTimeMillis() + 750);
                    try {
                        shoot(p);
                    } catch (ReflectiveOperationException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void spawnEffects(Player p, UUID instanceUUID) throws ReflectiveOperationException {
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, 0.5f, 2f);
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 2f);
        Location startLoc = p.getLocation().add(0,1,0);
        CraftPlayer craftPlayer = (CraftPlayer) p;
        Location endLoc = p.getTargetBlock(Set.of(Material.values()), 50).getLocation();
        Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, task -> {
            if(task.isCancelled()) return; // just in case
            p.getWorld().spawnParticle(Particle.GLOW, startLoc.add(0, -.1, 0), 10, 0.1, 0.1, 0.1, 0);
            startLoc.add(0,0.1,0);
            p.getWorld().spawnParticle(Particle.SCULK_SOUL, startLoc, 10, 0.15, 0.15, 0.15, 0);
            if(particleTimers.containsKey(instanceUUID)) {
                particleTimers.put(instanceUUID, particleTimers.get(instanceUUID)+1);
            } else {
                particleTimers.put(instanceUUID, 0);
            }
            if(particleTimers.get(instanceUUID) > 40) {
                particleTimers.remove(instanceUUID);
                task.cancel();
            }
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, task -> {
            Laser up = null;
            try {
//                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 2f);
                up = new Laser.GuardianLaser(startLoc.add(0,.1,0), endLoc.add(0,.1,0), -1, -1);
                up.start(BoxPlugin.instance);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            Laser finalUp = up;
            Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, () -> {
                finalUp.stop();
            }, 36);
        }, 4);
        Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, task -> {
            Laser down = null;
            try {
//                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 2f);
                down = new Laser.GuardianLaser(startLoc.add(0,-.1,0), endLoc.add(0,-.1,0), -1, -1);
                down.start(BoxPlugin.instance);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            Laser finalDown = down;
            Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, () -> {
                finalDown.stop();
            }, 28);
        }, 12);
        Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, task -> {
            Laser middle = null;
            try {
//                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 2f);
                middle = new Laser.GuardianLaser(startLoc, endLoc, -1, -1);
                middle.start(BoxPlugin.instance);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            Laser finalMiddle = middle;
            Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, () -> {
                finalMiddle.stop();
            }, 20);
        }, 20);

        // fire
        Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, () -> {
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WARDEN_ATTACK_IMPACT, 0.5f, 0.5f);
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.5f, 2f);
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.5f, 2f);
        }, 40);

    }

    private void shoot(Player p) throws ReflectiveOperationException {
        UUID instanceUUID = UUID.randomUUID();
        spawnEffects(p, instanceUUID);
    }
}
