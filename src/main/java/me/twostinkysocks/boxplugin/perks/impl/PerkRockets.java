package me.twostinkysocks.boxplugin.perks.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.perks.AbstractSelectablePerk;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

public class PerkRockets extends AbstractSelectablePerk {
    public PerkRockets() {
        ItemStack guiItem = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) guiItem.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Rockets");
        meta.setPower(2);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        meta.setLore(List.of(
                "",
                ChatColor.GRAY + "Gain 32 rockets on respawn"
        ));
        guiItem.setItemMeta(meta);

        setGuiItem(guiItem);

        setCost(5);

        setKey("perk_rockets");
    }

    @Override
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        removeOldRocketsFromInventory(p);
        addRocketsToInventory(p);
    }

    @Override
    public void onDeath(PlayerDeathEvent e) {
        for(ItemStack item : e.getDrops()) {
            if(item.getType() == Material.FIREWORK_ROCKET && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "perk_item"), PersistentDataType.INTEGER) && item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "perk_item"), PersistentDataType.INTEGER) == 1) {
                e.getDrops().remove(item);
            }
        }
    }

    @Override
    public void onEquip(Player p) {
        removeOldRocketsFromInventory(p);
        addRocketsToInventory(p);
    }

    @Override
    public void onUnequip(Player p) {
        removeOldRocketsFromInventory(p);
    }

    private void removeOldRocketsFromInventory(Player p) {
        for(ItemStack item : p.getInventory().getContents()) {
            if(item != null && item.getType() == Material.FIREWORK_ROCKET && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "perk_item"), PersistentDataType.INTEGER) && item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "perk_item"), PersistentDataType.INTEGER) == 1) {
                p.getInventory().remove(item);
            }
        }
    }

    private void addRocketsToInventory(Player p) {
        ItemStack stack = new ItemStack(Material.FIREWORK_ROCKET, 32);
        FireworkMeta meta = (FireworkMeta) stack.getItemMeta();
        meta.setPower(3);
        meta.setLore(List.of(
                "",
                ChatColor.GRAY + "Perk item"
        ));
        meta.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "perk_item"), PersistentDataType.INTEGER, 1);
        stack.setItemMeta(meta);
        p.getInventory().addItem(stack);
    }
}
