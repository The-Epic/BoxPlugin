package me.twostinkysocks.boxplugin.customitems.items.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.customitems.CustomItemsMain;
import me.twostinkysocks.boxplugin.customitems.items.CustomItem;
import me.twostinkysocks.boxplugin.manager.PerksManager;
import me.twostinkysocks.boxplugin.util.Laser;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class TalismanOfEnergy extends CustomItem {


    private HashMap<UUID, Long> cooldown;
    private HashMap<UUID, Integer> boltTimers;
    // uuid: map of hits (Damage, Timestamp)
    private HashMap<UUID, ArrayList<Hit>> hits;

    class Hit {
        private Double damage;
        private Long timestamp;

        public Hit(Double damage, Long timestamp) {
            this.damage = damage;
            this.timestamp = timestamp;
        }

        public Double getDamage() {return damage;}

        public Long getTimestamp() {return timestamp;}
    }

    public TalismanOfEnergy(CustomItemsMain plugin) {
        super(
                "§bArtifact of Energy",
                "TALISMAN_OF_ENERGY",
                Material.NETHER_STAR,
                plugin,
                "",
                "§6Item Ability: Electrocute",
                "§7Hitting an enemy 3 times within 5 seconds electrocutes them,",
                "§7striking them again with 1/2 of your average damage per hit",
                "§7over the duration",
                "§8Cooldown: §a10s"
        );
        cooldown = new HashMap<>();
        hits = new HashMap<>();
        boltTimers = new HashMap<>();
        setEntityDamageByEntity(e -> {
            Player p = (Player) e.getDamager();
            if(p.hasPermission("customitems.cooldownbypass") || !cooldown.containsKey(p.getUniqueId()) || cooldown.get(p.getUniqueId()) < System.currentTimeMillis()) {
                cooldown.put(p.getUniqueId(), System.currentTimeMillis() + (long)(10000 * (BoxPlugin.instance.getPerksManager().getSelectedMegaPerks(p).contains(PerksManager.MegaPerk.MEGA_COOLDOWN_REDUCTION) ? 0.5 : 1))); // 10 seconds
                logic(e, p);
            }
        });
    }

    private void electrocute(long avg, EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Damageable) {
            Damageable d = (Damageable) e.getEntity();
            d.damage(avg/2.0, e.getDamager());
            playEffects(e);
        }
    }

    private void playEffects(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        UUID instanceUUID = UUID.randomUUID();
        //particle minecraft:dust 1.0 0.1 0.2 1.5 ~ ~2 ~ 0 1 0 0 5 force
        damaged.getWorld().playSound(damaged.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1f, 0.5f);
        damaged.getWorld().playSound(damaged.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.2f, 1.49f);
        damaged.getWorld().playSound(damaged.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, .32f, 1.14f);
        damaged.getWorld().playSound(damaged.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, .36f, 1.08f);
        damaged.getWorld().playSound(damaged.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, .46f, 1f);
        damaged.getWorld().playSound(damaged.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 0.62f, 2f);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 25, 51), 1.5F);
        Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, task -> {
            if(task.isCancelled()) return; // just in case
            damaged.getWorld().spawnParticle(Particle.REDSTONE, damaged.getLocation().clone().add(0, 2, 0), 5, 0, 3, 0, dustOptions);
            if(boltTimers.containsKey(instanceUUID)) {
                boltTimers.put(instanceUUID, boltTimers.get(instanceUUID)+1);
            } else {
                boltTimers.put(instanceUUID, 0);
            }
            if(boltTimers.get(instanceUUID) > 3) {
                boltTimers.remove(instanceUUID);
                task.cancel();
                damaged.getWorld().spawnParticle(Particle.REDSTONE, damaged.getLocation().clone().add(0, 0.5, 0), 15, 0.5, 0.5, 0.5, dustOptions);
                damaged.getWorld().spawnParticle(Particle.REDSTONE, damaged.getLocation(), 15, 0.8, 0.8, 0.8, dustOptions);
            }
        }, 0, 1);
    }

    private void logic(EntityDamageByEntityEvent e, Player p) {
        if(!hits.containsKey(p.getUniqueId())) {
            ArrayList<Hit> list = new ArrayList<>();
            list.add(new Hit(e.getDamage(), System.currentTimeMillis()));
            hits.put(p.getUniqueId(), list);
        } else {
            ArrayList<Hit> pHits = hits.get(p.getUniqueId());
            if(pHits.size() >= 3) {
                long min = Long.MAX_VALUE;
                long max = Long.MIN_VALUE;
                long avg = 0;
                for(Hit hit : pHits) {
                    if(hit.getTimestamp() < min) min = hit.getTimestamp();
                    if(hit.getTimestamp() > max) max = hit.getTimestamp();
                    avg += hit.getDamage();
                }
                avg /= ((double) pHits.size());
                long diff = max - min;
                if(diff < 5000) { // should electrocute
                    electrocute(avg, e);
                    hits.get(p.getUniqueId()).clear();
                } else {
                    //              first element
                    pHits.remove(0);
                }
            }
            pHits.add(new Hit(e.getDamage(), System.currentTimeMillis()));
        }
    }
}
