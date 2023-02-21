package me.twostinkysocks.boxplugin;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class Util {
    public static boolean isGigaCoin(ItemStack item) {
        if(item != null && item.getType() == Material.GOLD_BLOCK && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
           return item.getItemMeta().getDisplayName().contains("Giga Coin");
        } else {
            return false;
        }
    }

    public static boolean isPerkItem(ItemStack item) {
        return item != null && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "perk_item"), PersistentDataType.INTEGER) && item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "perk_item"), PersistentDataType.INTEGER) == 1;
    }
}
