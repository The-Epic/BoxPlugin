package me.twostinkysocks.boxplugin.util;

import me.twostinkysocks.boxplugin.BoxPlugin;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftHumanEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static boolean isGigaCoin(ItemStack item) {
        if(item != null && item.getType() == Material.GOLD_BLOCK && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasEnchants() && item.getItemMeta().hasLore()) {
           return item.getItemMeta().getDisplayName().contains("Giga Coin");
        } else {
            return false;
        }
    }

    public static boolean isTeraCube(ItemStack item) {
        if(item != null && item.getType() == Material.DIAMOND_BLOCK && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasEnchants() && item.getItemMeta().hasLore()) {
            return ChatColor.stripColor(item.getItemMeta().getDisplayName()).contains("TeraCube");
        } else {
            return false;
        }
    }

    public static boolean isHexidium(ItemStack item) {
        if(item != null && item.getType() == Material.EMERALD_BLOCK && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasEnchants() && item.getItemMeta().hasLore()) {
            return ChatColor.stripColor(item.getItemMeta().getDisplayName()).contains("HEXIDIUM");
        } else {
            return false;
        }
    }

    public static boolean isGhastlyHerb(ItemStack item) {
        if(item != null && item.getType() == Material.WITHER_ROSE && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasEnchants() && item.getItemMeta().hasLore()) {
            return ChatColor.stripColor(item.getItemMeta().getDisplayName()).contains("Ghastly Herb");
        } else {
            return false;
        }
    }

    public static boolean isPerkItem(ItemStack item) {
        return item != null && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "perk_item"), PersistentDataType.INTEGER) && item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "perk_item"), PersistentDataType.INTEGER) == 1;
    }

    public static ItemStack gigaCoin(int amount) {
        ItemStack item = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(List.of("64x Xanatos Coins"));
        meta.setDisplayName("§6§lGiga Coin");
        item.setItemMeta(meta);
        item.setAmount(amount);
        return item;
    }

    public static ItemStack[] gigaCoinArray(int amount) {
        int stacks = amount/64;
        int remaining = amount%64;
        ItemStack[] coinStacks = new ItemStack[stacks+1];
        for(int i = 0; i < stacks; i++) {
            coinStacks[i] = gigaCoin(64);
        }
        coinStacks[coinStacks.length-1] = gigaCoin(remaining);
        return coinStacks;
    }

    public static void spawnTallCircle(World w, Location circleLocation, Vector direction, double radius, int points, Particle.DustOptions dustOptions) {
        double interval = 2*Math.PI/points;
        for(int i = 0; i < points; i++) {
            double t = i*interval;
            double x = radius * Math.cos(t);
            double y = radius * Math.sin(t);
            double z = 0;
            Vector v = new Vector(x,y,z);
            v = MathUtil.rotateFunction(v, new Location(w, 0,0,0).setDirection(direction));
            w.spawnParticle(Particle.SCULK_SOUL, new Location(w, circleLocation.getX() + v.getX(), circleLocation.getY() + v.getY(), circleLocation.getZ() + v.getZ()), 4, 0, 0.8, 0, 0);
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

    public static void spawnCircle(World w, Location circleLocation, Vector direction, double radius, int points) {
        double interval = 2*Math.PI/points;
        for(int i = 0; i < points; i++) {
            double t = i*interval;
            double x = radius * Math.cos(t);
            double y = radius * Math.sin(t);
            double z = 0;
            Vector v = new Vector(x,y,z);
            v = MathUtil.rotateFunction(v, new Location(w, 0,0,0).setDirection(direction));
            w.spawnParticle(Particle.END_ROD, new Location(w, circleLocation.getX() + v.getX(), circleLocation.getY() + v.getY(), circleLocation.getZ() + v.getZ()), 1, 0, 0.1, 0, 0);
        }
    }

    public static String colorize(String string) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        for (Matcher matcher = pattern.matcher(string); matcher.find(); matcher = pattern.matcher(string)) {
            String color = string.substring(matcher.start(), matcher.end());
            try {
                string = string.replace(color, ChatColor.of(color) + ""); // You're missing this replacing
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        string = ChatColor.translateAlternateColorCodes('&', string); // Translates any & codes too
        return string;
    }

    public static void debug(Player p, String message) {
        if(BoxPlugin.instance.getDebugEnabled().containsKey(p.getUniqueId()) && BoxPlugin.instance.getDebugEnabled().get(p.getUniqueId())) {
            p.sendMessage(ChatColor.GRAY + "[DEBUG] " + message);
        }
    }

    public static <T> T randomFromList(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()) % list.size());
    }

    public static boolean isInteger(String s) {
        int i;
        try {
            i = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void hitThroughShield(Entity source, HumanEntity target, double damage, int cooldownticks) {
        if(target.isBlocking()) {
            target.setCooldown(Material.SHIELD, cooldownticks);
            EntityLiving nms = ((CraftHumanEntity) target).getHandle();
            // EntityLiving#clearActiveItem()  -  actually disables it
            nms.eZ();
        }
        target.damage(damage, source);
    }

    /**
     *
     * @param e The event
     * @param dropChancePerSlot drop chance per slot (0.5 is 50% chance to drop that item)
     */
    public static void dropPercent(PlayerDeathEvent e, double dropChancePerSlot) {
        Player target = e.getEntity();
        int outof = (int)(Math.round(1/dropChancePerSlot));
        Util.debug(target, "Losing " + (int)(dropChancePerSlot*100) + "% of items");
        e.setKeepInventory(true);
        e.getDrops().clear();
        for(int i = 0; i < e.getEntity().getInventory().getSize(); i++) {
            int rand = (int)(Math.random() * (outof) + 1);
            if(rand == 1) {
                if(e.getEntity().getInventory().getItem(i) != null) {
                    Util.debug(target, "Lost " + e.getEntity().getInventory().getItem(i).getType());
                    e.getDrops().add(e.getEntity().getInventory().getItem(i));
                    e.getEntity().getInventory().setItem(i, null);
                }
            }
        }
        try {
            ArrayList<ItemStack> armor = new ArrayList<>(List.of(e.getEntity().getInventory().getArmorContents()));
            for(int i = 0; i < e.getEntity().getInventory().getArmorContents().length; i++) {
                int rand = (int)(Math.random() * (outof) + 1);
                if(rand == 1) {
                    if(e.getEntity().getInventory().getArmorContents()[i] != null) {
                        Util.debug(target, "Lost " + e.getEntity().getInventory().getArmorContents()[i].getType());
                        e.getDrops().add(e.getEntity().getInventory().getArmorContents()[i]);
                        armor.set(i, null);
                    }
                }
            }
            e.getEntity().getInventory().setArmorContents(armor.toArray(new ItemStack[4]));
        } catch (NullPointerException ex) {
            // don't care if armor is null
        }
    }
}
