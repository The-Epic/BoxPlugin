package me.twostinkysocks.boxplugin.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class PVPManager {

    private HashMap<UUID, Integer> streaks;


    public PVPManager() {
        streaks = new HashMap<UUID, Integer>();
    }

    public void registerKill(Player cause, Player target) {
        // add to streak
        if(streaks.containsKey(cause.getUniqueId())) {
            streaks.put(cause.getUniqueId(), streaks.get(cause.getUniqueId()) + 1);
        } else {
            streaks.put(cause.getUniqueId(), 1);
        }
        if(getStreak(cause) % 10 == 0) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',"&c&lBounty! &7" + cause.getName() + " is on a " + getStreak(cause) + " kill streak!"));
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.2f, 2f);
            }
        }
        // claim and reset streak
        HashMap<Integer, ItemStack> toDrop = cause.getInventory().addItem(new ItemStack(Material.SKELETON_SKULL, getBounty(target)));
        toDrop.forEach((i, item) -> {
            Item droppedItem = (Item) cause.getWorld().spawnEntity(cause.getLocation(), EntityType.DROPPED_ITEM);
            droppedItem.setItemStack(item);
        });
        cause.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSkulls Claimed! &7You claimed " + getBounty(target) + " skulls from " + target.getName()));
        resetStreak(target);
    }

    public int getStreak(Player p) {
        return streaks.containsKey(p.getUniqueId()) ? streaks.get(p.getUniqueId()) : 0;
    }

    public void resetStreak(Player p) {
        streaks.put(p.getUniqueId(), 0);
    }

    public boolean hasStreak(Player p) {
        return streaks.containsKey(p.getUniqueId()) && streaks.get(p.getUniqueId()) > 0;
    }

    // num of skulls
    public int getBounty(Player p) {
        return calculateBounty(getStreak(p));
    }

    public int calculateBounty(int streak) {
        return streak >= 10 ? streak : 1;
    }

}
