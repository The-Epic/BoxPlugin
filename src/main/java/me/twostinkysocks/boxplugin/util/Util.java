package me.twostinkysocks.boxplugin.util;

import com.google.common.base.Preconditions;
import io.lumine.mythic.bukkit.utils.adventure.text.serializer.legacy.LegacyComponentSerializer;
import me.twostinkysocks.boxplugin.BoxPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
}
