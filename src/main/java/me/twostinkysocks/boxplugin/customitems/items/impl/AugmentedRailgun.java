package me.twostinkysocks.boxplugin.customitems.items.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.customitems.CustomItemsMain;
import me.twostinkysocks.boxplugin.customitems.items.CustomItem;
import me.twostinkysocks.boxplugin.util.Laser;
import me.twostinkysocks.boxplugin.util.MathUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.stream.Collectors;

public class AugmentedRailgun extends CustomItem {

    private HashMap<UUID, Long> cooldown;
    private HashMap<UUID, Integer> particleTimers;
    private HashMap<UUID, Integer> ring1Timer;
    private HashMap<UUID, Integer> ring2Timer;
    private HashMap<UUID, Integer> ring3Timer;
    private HashMap<UUID, Integer> beamTimers;
    private HashMap<UUID, Integer> finalRingTimer;

    public AugmentedRailgun(CustomItemsMain plugin) {
        super(
                ChatColor.WHITE + "Augmented Railgun",
                "AUGMENTED_RAILGUN",
                Material.DIAMOND_HOE,
                plugin
        );
        cooldown = new HashMap<>();
        particleTimers = new HashMap<>();
        beamTimers = new HashMap<>();
        ring1Timer = new HashMap<>();
        ring2Timer = new HashMap<>();
        ring3Timer = new HashMap<>();
        finalRingTimer = new HashMap<>();
        setClick((e, a) -> {
            Player p = e.getPlayer();
            if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
                e.setCancelled(true);
                if(p.hasPermission("customitems.cooldownbypass") || !cooldown.containsKey(p.getUniqueId()) || cooldown.get(p.getUniqueId()) < System.currentTimeMillis()) {
                    cooldown.put(p.getUniqueId(), System.currentTimeMillis() + 1000*15);
                    try {
                        shoot(p);
                    } catch (ReflectiveOperationException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    BigDecimal bd = new BigDecimal(((double)(cooldown.get(p.getUniqueId()) - System.currentTimeMillis()))/1000.0);
                    bd = bd.round(new MathContext(2));
                    p.sendMessage(ChatColor.RED + "That's too fast! Wait " + bd.doubleValue() + " more seconds!");
                    p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 3.0F, 1.0F);
                }
            }
        });
    }

    private void spawnEffects(Player p, UUID instanceUUID, Vector direction) throws ReflectiveOperationException {
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, 0.5f, 2f);
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 2f);
        Location startLoc = p.getLocation().add(0,1,0);
        CraftPlayer craftPlayer = (CraftPlayer) p;
        Location endLoc = p.getTargetBlock(Set.of(Material.values()), 50).getLocation();
        Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, task -> {
            if(task.isCancelled()) return; // just in case
            p.getWorld().spawnParticle(Particle.GLOW, startLoc.clone().add(0, -.1, 0), 10, 0.1, 0.1, 0.1, 0);
            p.getWorld().spawnParticle(Particle.SCULK_SOUL, startLoc, 10, 0.15, 0.15, 0.15, 0);
            if(particleTimers.containsKey(instanceUUID)) {
                particleTimers.put(instanceUUID, particleTimers.get(instanceUUID)+1);
            } else {
                particleTimers.put(instanceUUID, 0);
            }
            if(particleTimers.get(instanceUUID) > 20) {
                particleTimers.remove(instanceUUID);
                task.cancel();
            }
        }, 0, 1);
        Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, task -> {
            if(task.isCancelled()) return; // just in case

            Vector translation = direction.clone().normalize(); // 1 block
            spawnCircle(p, startLoc.clone().add(translation), translation, 0.5, 50);

            if(ring1Timer.containsKey(instanceUUID)) {
                ring1Timer.put(instanceUUID, ring1Timer.get(instanceUUID)+1);
            } else {
                ring1Timer.put(instanceUUID, 0);
            }
            if(ring1Timer.get(instanceUUID) > 20) {
                ring1Timer.remove(instanceUUID);
                task.cancel();
            }
        }, 0, 1);
        Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, task -> {
            if(task.isCancelled()) return; // just in case

            Vector translation = direction.clone().normalize().multiply(2); // 2 blocks
            spawnCircle(p, startLoc.clone().add(translation), translation, 0.5, 50);


            if(ring2Timer.containsKey(instanceUUID)) {
                ring2Timer.put(instanceUUID, ring2Timer.get(instanceUUID)+1);
            } else {
                ring2Timer.put(instanceUUID, 0);
            }
            if(ring2Timer.get(instanceUUID) > 15) {
                ring2Timer.remove(instanceUUID);
                task.cancel();
            }
        }, 5, 1);
        Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, task -> {
            if(task.isCancelled()) return; // just in case

            Vector translation = direction.clone().normalize().multiply(3); // 3 blocks
            spawnCircle(p, startLoc.clone().add(translation), translation, 0.5, 50);

            if(ring3Timer.containsKey(instanceUUID)) {
                ring3Timer.put(instanceUUID, ring3Timer.get(instanceUUID)+1);
            } else {
                ring3Timer.put(instanceUUID, 0);
            }
            if(ring3Timer.get(instanceUUID) > 10) {
                ring3Timer.remove(instanceUUID);
                task.cancel();
            }
        }, 10, 1);
        ArrayList<Laser> lasers = new ArrayList<>();
        Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, task -> {
            if(task.isCancelled()) return; // just in case
            try {
                double y = (double) Math.random() * (0.2 + 0.2) - 0.2;
                Laser laser = new Laser.GuardianLaser(startLoc.clone().add(0,y,0), endLoc.clone().add(0,y,0), -1, -1);
                laser.start(BoxPlugin.instance);
                lasers.add(laser);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            if(beamTimers.containsKey(instanceUUID)) {
                beamTimers.put(instanceUUID, beamTimers.get(instanceUUID)+1);
            } else {
                beamTimers.put(instanceUUID, 0);
            }
            if(beamTimers.get(instanceUUID) > 15) {
                beamTimers.remove(instanceUUID);
                task.cancel();
            }
        }, 0, 1);

        Location startLocClone = startLoc.clone();
        // fire
        Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, task -> {
            if(task.isCancelled()) return; // just in case

            Vector translation = direction.clone().normalize().multiply(1);
            spawnCircle(p, startLocClone.clone().add(translation), translation, 0.5, 50, 0);
            translation = direction.clone().normalize().multiply(2);
            spawnCircle(p, startLocClone.clone().add(translation), translation, 0.5, 50, 0);
            translation = direction.clone().normalize().multiply(3);
            spawnCircle(p, startLocClone.clone().add(translation), translation, 0.5, 50, 0);

            startLocClone.add(direction.clone().normalize().multiply(3));
            if(finalRingTimer.containsKey(instanceUUID)) {
                finalRingTimer.put(instanceUUID, finalRingTimer.get(instanceUUID)+1);
            } else {
                finalRingTimer.put(instanceUUID, 0);
            }
            if(finalRingTimer.get(instanceUUID) < 8) {
                Vector direc2 = direction.clone().normalize().multiply(0.1);
                Vector direc = direc2.clone();
                for(Laser laser : lasers) {
                    if(laser.isStarted()) {
                        laser.stop();
                    }
                }
                for(int i = 0; i < 500; i++) {
                    p.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, new Location(p.getWorld(), startLoc.getX() + direc.getX(), startLoc.getY() + direc.getY(), startLoc.getZ() + direc.getZ()), 1, 0, 0, 0);
                    p.getWorld().spawnParticle(Particle.LAVA, new Location(p.getWorld(), startLoc.getX() + direc.getX(), startLoc.getY() + direc.getY(), startLoc.getZ() + direc.getZ()), 1, 0, 0, 0);
                    direc = direc2.clone().multiply(i);
                }
            }
            if(finalRingTimer.get(instanceUUID) > 15) {
                finalRingTimer.remove(instanceUUID);
                task.cancel();
            }
        }, 18, 1);
        Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, () -> {
            p.getWorld().playSound(startLoc, Sound.BLOCK_CONDUIT_DEACTIVATE, 0.5f, 2f);
            p.getWorld().playSound(startLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.4f, 1.7f);
            p.getWorld().playSound(startLoc, Sound.ENTITY_WARDEN_ATTACK_IMPACT, 0.4f, 0.5f);
            p.getWorld().playSound(startLoc, Sound.ENTITY_WITHER_DEATH, 0.2f, 2f);
            p.getWorld().playSound(startLoc, Sound.ENTITY_WARDEN_SONIC_BOOM, 0.35f, 1.6f);
            p.getWorld().playSound(startLoc, Sound.ITEM_TRIDENT_THUNDER, 0.5f, 0.85f);
            for(Laser l : lasers) {
                if(l.isStarted()) {
                    l.stop();
                }
            }
        }, 20);

    }

    private void shoot(Player p) throws ReflectiveOperationException {
        UUID instanceUUID = UUID.randomUUID();
        List<Block> lineOfSight = p.getLineOfSight(Set.of(Material.values()), 50);
        Location startLoc = p.getLocation();
        spawnEffects(p, instanceUUID, p.getLocation().getDirection());
        Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, () -> {
            List<Entity> nearbyEntities = new ArrayList<>(startLoc.getWorld().getNearbyEntities(startLoc, 50, 50, 50));
            List<Damageable> damageables = raycastEntities(lineOfSight, nearbyEntities);
            for(Damageable d : damageables) {
                if(d instanceof ArmorStand) return;
                if(d instanceof ItemFrame) return;
                if(d instanceof LivingEntity) {
                    if(d.getUniqueId().equals(p.getUniqueId())) return;
                    ((LivingEntity) d).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 2, true, false));
                }
                d.damage(75, p);
            }
        }, 20);
    }

    private List<Damageable> raycastEntities(List<Block> lineOfSight, List<Entity> nearbyEntities) {
        ArrayList<Damageable> entities = new ArrayList<>();
        for(Entity entity : nearbyEntities) {
            for(Block block : lineOfSight) {
                if(entity instanceof Damageable) {
                    if(entity.getLocation().distance(block.getLocation()) < 2) {
                        entities.add((Damageable) entity);
                    }
                }
            }
        }
        return entities;
    }

    private void spawnCircle(Player p, Location circleLocation, Vector direction, double radius, int points, double speed) {
        double interval = 2*Math.PI/points;
        for(int i = 0; i < points; i++) {
            double t = i*interval;
            double x = radius * Math.cos(t);
            double y = radius * Math.sin(t);
            double z = 0;
            Vector v = new Vector(x,y,z);
            v = MathUtil.rotateFunction(v, new Location(p.getWorld(), 0,0,0).setDirection(direction));
            p.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, new Location(p.getWorld(), circleLocation.getX() + v.getX(), circleLocation.getY() + v.getY(), circleLocation.getZ() + v.getZ()), 1, 0, 0, 0, speed);
        }


        //
//        // this works
//        int points = 50;
//        double radius = 0.5;
//        double interval = 2*Math.PI/points;
//        Location circleLocation = startLoc.clone();
//        for(int i = 0; i < points; i++) {
//            double t = i*interval;
//            double x = radius * Math.cos(t);
//            double y = radius * Math.sin(t);
//            double z = 0;
//            Vector v = new Vector(x,y,z);
//            v = MathUtil.rotateFunction(v, new Location(p.getWorld(), 0,0,0).setDirection(direction));
//            p.getWorld().spawnParticle(Particle.DRIP_LAVA, new Location(p.getWorld(), circleLocation.getX() + v.getX(), circleLocation.getY() + v.getY(), circleLocation.getZ() + v.getZ()), 0, 0, 0, 0);
//        }
        //
    }

    private void spawnCircle(Player p, Location circleLocation, Vector direction, double radius, int points) {
        double interval = 2*Math.PI/points;
        for(int i = 0; i < points; i++) {
            double t = i*interval;
            double x = radius * Math.cos(t);
            double y = radius * Math.sin(t);
            double z = 0;
            Vector v = new Vector(x,y,z);
            v = MathUtil.rotateFunction(v, new Location(p.getWorld(), 0,0,0).setDirection(direction));
            p.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, new Location(p.getWorld(), circleLocation.getX() + v.getX(), circleLocation.getY() + v.getY(), circleLocation.getZ() + v.getZ()), 1, 0, 0, 0);
        }


        //
//        // this works
//        int points = 50;
//        double radius = 0.5;
//        double interval = 2*Math.PI/points;
//        Location circleLocation = startLoc.clone();
//        for(int i = 0; i < points; i++) {
//            double t = i*interval;
//            double x = radius * Math.cos(t);
//            double y = radius * Math.sin(t);
//            double z = 0;
//            Vector v = new Vector(x,y,z);
//            v = MathUtil.rotateFunction(v, new Location(p.getWorld(), 0,0,0).setDirection(direction));
//            p.getWorld().spawnParticle(Particle.DRIP_LAVA, new Location(p.getWorld(), circleLocation.getX() + v.getX(), circleLocation.getY() + v.getY(), circleLocation.getZ() + v.getZ()), 0, 0, 0, 0);
//        }
        //
    }
}
