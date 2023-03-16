package me.twostinkysocks.boxplugin.perks.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.perks.AbstractUpgradablePerk;
import me.twostinkysocks.boxplugin.util.Util;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class PerkXPBoost extends AbstractUpgradablePerk {
    public PerkXPBoost() {
        setKey("perk_xp_boost");
    }

    @Override
    public ItemStack getGuiItem(Player p) {
        ItemStack guiItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = guiItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Permanent XP Boost");
        BigDecimal bd = new BigDecimal((calculateXPMultiplier(p)+0.1));
        bd = bd.round(new MathContext(4));
        double upgradedValue = bd.doubleValue();
        meta.setLore(List.of(
                "",
                ChatColor.AQUA + "Current boost: " + (calculateXPMultiplier(p)) + "x",
                "",
                ChatColor.GRAY + "Upgrade to " + ChatColor.AQUA + "" + upgradedValue + "x " + ChatColor.GRAY + "for " + ChatColor.GOLD + ChatColor.BOLD + getUpgradeCost(p) + "" + " Giga Coins"
        ));
        guiItem.setItemMeta(meta);
        return guiItem;
    }

    public double calculateXPMultiplier(Player p) {
        return 1.0 + (0.1*getLevel(p));
    }

    @Override
    public int getUpgradeCost(Player p) {
        return (int) Math.pow(2, getLevel(p));
    }

    @Override
    public void upgrade(Player p) {
        boolean canBuy = buyPermaXP(p);
        if(canBuy) {
            incrementLevel(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
            p.sendMessage(ChatColor.GREEN + "Upgraded XP boost!");
            p.closeInventory();
            BoxPlugin.instance.getPerksManager().openUpgradableGui(p);
        } else {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.25f, 0.5f);
            p.sendMessage(ChatColor.RED + "You can't afford this!");
        }
    }

    private boolean buyPermaXP(Player p) {
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
