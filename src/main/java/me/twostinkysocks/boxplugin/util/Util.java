package me.twostinkysocks.boxplugin.util;

import me.twostinkysocks.boxplugin.BoxPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Util {
    public static boolean isGigaCoin(ItemStack item) {
        if(item != null && item.getType() == Material.GOLD_BLOCK && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
           return item.getItemMeta().getDisplayName().contains("Giga Coin");
        } else {
            return false;
        }
    }

    public static boolean isTeraCube(ItemStack item) {
        if(item != null && item.getType() == Material.DIAMOND_BLOCK && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return ChatColor.stripColor(item.getItemMeta().getDisplayName()).contains("TeraCube");
        } else {
            return false;
        }
    }

    public static boolean isHexidium(ItemStack item) {
        if(item != null && item.getType() == Material.EMERALD_BLOCK && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return ChatColor.stripColor(item.getItemMeta().getDisplayName()).contains("HEXIDIUM");
        } else {
            return false;
        }
    }

    public static boolean isGhastlyHerb(ItemStack item) {
        if(item != null && item.getType() == Material.WITHER_ROSE && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
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

}
