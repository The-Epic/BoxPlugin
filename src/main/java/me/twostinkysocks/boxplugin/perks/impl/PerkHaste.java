package me.twostinkysocks.boxplugin.perks.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.perks.AbstractUpgradablePerk;
import me.twostinkysocks.boxplugin.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PerkHaste extends AbstractUpgradablePerk {
    public PerkHaste() {
        setKey("perk_haste");
    }

    @Override
    public ItemStack getGuiItem(Player p) {
        ItemStack guiItem = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) guiItem.getItemMeta();
        meta.setColor(Color.YELLOW);
        meta.setDisplayName(ChatColor.YELLOW + "Haste");
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        meta.setLore(List.of(
                "",
                ChatColor.AQUA + "Current level: " + (getLevel(p)),
                "",
                ChatColor.GRAY + "Upgrade to " + ChatColor.YELLOW + "Haste " + (getLevel(p)+1) + " " + ChatColor.GRAY + "for " + ChatColor.GOLD + ChatColor.BOLD + getUpgradeCost(p) + "" + " Giga Coins"
        ));
        guiItem.setItemMeta(meta);
        return guiItem;
    }

    @Override
    public int getUpgradeCost(Player p) {
        return (int) Math.pow(4, getLevel(p));
    }

    @Override
    public void upgrade(Player p) {
        boolean canBuy = buyPermaHaste(p);
        if(canBuy) {
            incrementLevel(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
            p.sendMessage(ChatColor.GREEN + "Upgraded Haste!");
            p.closeInventory();
            onEquip(p);
            BoxPlugin.instance.getPerksManager().openUpgradableGui(p);
        } else {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.25f, 0.5f);
            p.sendMessage(ChatColor.RED + "You can't afford this!");
        }
    }

    @Override
    public void onRespawn(PlayerRespawnEvent e) {
        if(getLevel(e.getPlayer()) > 0) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, getLevel(e.getPlayer())-1, true, false));
        }
    }

    @Override
    public void onEquip(Player p) {
        if(getLevel(p) > 0) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, getLevel(p) - 1, true, false));
        }
    }

    @Override
    public void onUnequip(Player p) {
        if(getLevel(p) > 0) {
            p.removePotionEffect(PotionEffectType.FAST_DIGGING);
        }
    }

    private boolean buyPermaHaste(Player p) {
        int gigaCoinsHeld = 0;
        int cost = getUpgradeCost(p);
        for(ItemStack item : p.getInventory().getContents()) {
            if(Util.isGigaCoin(item)) {
                gigaCoinsHeld += item.getAmount();
            }
        }
        if(gigaCoinsHeld >= cost) {
            for(ItemStack item : p.getInventory().getContents()) {
                if(cost == 0) return true;
                if(Util.isGigaCoin(item)) {
                    int amount = item.getAmount();
                    for(int i = 0; i < amount; i++) {
                        cost--;
                        item.setAmount(item.getAmount() - 1);
                        if(cost == 0) return true;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
