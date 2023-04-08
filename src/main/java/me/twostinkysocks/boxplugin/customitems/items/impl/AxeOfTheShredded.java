package me.twostinkysocks.boxplugin.customitems.items.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.customitems.CustomItemsMain;
import me.twostinkysocks.boxplugin.customitems.items.CustomItem;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class AxeOfTheShredded extends CustomItem {

    private HashMap<UUID, Long> cooldown;
    private HashMap<Player, ArrayList<Axe>> spawnedAxes;

    private class Axe {
        private ArmorStand stand;
        private Vector vector;
        public Axe(ArmorStand stand, Vector vector) {
            this.vector = vector;
            this.stand = stand;
        }
        public void setArmorStand(ArmorStand stand) {
            this.stand = stand;
        }
        public void setVelocity(Vector vector) {
            this.vector = vector;
        }
        public ArmorStand getArmorStand() {
            return this.stand;
        }
        public Vector getVelocity() {
            return this.vector;
        }
    }

    public AxeOfTheShredded(CustomItemsMain plugin) {
        super(
                ChatColor.WHITE + "Throwing Knife",
                "THROWING_KNIFE",
                Material.IRON_SWORD,
                plugin,
                "",
                ChatColor.GRAY + "Right click to throw!"
        );
        cooldown = new HashMap<>();
        spawnedAxes = new HashMap<>();
        setClick((e, a) -> {
            Player p = e.getPlayer();
            if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
                if(p.hasPermission("customitems.cooldownbypass") || !cooldown.containsKey(p.getUniqueId()) || cooldown.get(p.getUniqueId()) < System.currentTimeMillis()) {
                    cooldown.put(p.getUniqueId(), System.currentTimeMillis() + 750);
                    spawnAxe(p);
                }
            }
        });

        setLeave(p -> {
            cooldown.remove(p.getUniqueId());
            spawnedAxes.remove(p);
        });

        Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, () -> {
            for(Player p : spawnedAxes.keySet()) {
                if(p.isValid() && p.isOnline()) {
                    for(Axe axe : new ArrayList<>(spawnedAxes.get(p))) {
                        if(!axe.getArmorStand().isDead()) {
                            if(!isNextToBlock(axe.getArmorStand())) {
                                axe.getArmorStand().setVelocity(axe.getVelocity());
                                axe.getArmorStand().setRightArmPose(new EulerAngle((axe.getArmorStand().getRightArmPose().getX()+Math.toRadians(30)) > Math.PI*2 ? (axe.getArmorStand().getRightArmPose().getX()+Math.toRadians(30))%(Math.PI*2) : (axe.getArmorStand().getRightArmPose().getX()+Math.toRadians(30)), 0, 0));
                                damage(p, axe);
                            } else {
                                axe.getArmorStand().remove();
                            }
                        } else {
                            spawnedAxes.get(p).remove(axe);
                        }
                    }
                }
            }
        }, 1, 1);
    }

    private void damage(Player p, Axe axe) {
        List<Entity> nearby = axe.getArmorStand().getNearbyEntities(0.5, 0.5, 0.5);
        for(Entity entity : nearby) {
            if(entity instanceof Damageable && !entity.equals(p) && !(entity.getType() == EntityType.ARMOR_STAND)) {
                Damageable damageable = (Damageable) entity;
                damageable.damage(90, p);
                p.playSound(p.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 0.5f, 0.5f);
            }
        }
    }

    private boolean isNextToBlock(Entity e) {
        Block feet = e.getLocation().getBlock();
        Block head = e.getLocation().add(0, 1, 0).getBlock();
        Block xph = head.getRelative(1, 0 ,0);
        Block xmh = head.getRelative(-1, 0 ,0);
        Block yph = head.getRelative(0, 1 ,0);
        Block ymh = head.getRelative(0, -1 ,0);
        Block zph = head.getRelative(0, 0 ,1);
        Block zmh = head.getRelative(0, 0 ,-1);
        if(xph.getType() != Material.AIR || xmh.getType() != Material.AIR || yph.getType() != Material.AIR || ymh.getType() != Material.AIR || zph.getType() != Material.AIR || zmh.getType() != Material.AIR) {
            return true;
        }
        return false;
    }

    private CraftArmorStand createStand(Player p) {
        CraftArmorStand armorStand = (CraftArmorStand) p.getWorld().spawnEntity(new Location(p.getWorld(), 0, 0, 0), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setArms(true);
        armorStand.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        armorStand.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, getItemId()), PersistentDataType.INTEGER, 1);
        Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, () -> {
            armorStand.remove();
        }, 8*20); // kill stand timer
        armorStand.setInvulnerable(true);
        Location armorStandLocation = p.getLocation().add(p.getLocation().getDirection().setY(0).normalize().rotateAroundY(Math.PI / 2).multiply(0.5));
        armorStand.teleport(armorStandLocation);
        armorStand.setVelocity(p.getLocation().getDirection().normalize().multiply(2));
        return armorStand;
    }

    private void spawnAxe(Player p) {
        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 0.5f);
        CraftArmorStand armorStand = createStand(p);

        if(spawnedAxes.containsKey(p)) {
            if(!spawnedAxes.get(p).contains(p)) {
                spawnedAxes.get(p).add(new Axe(armorStand, p.getLocation().getDirection().normalize().multiply(0.7)));
            }
        } else {
            spawnedAxes.put(p, new ArrayList<>(List.of(new Axe(armorStand, p.getLocation().getDirection().normalize().multiply(0.7)))));
        }
    }
}
