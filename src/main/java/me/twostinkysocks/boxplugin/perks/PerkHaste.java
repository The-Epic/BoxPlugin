package me.twostinkysocks.boxplugin.perks;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
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

public class PerkHaste extends AbstractPerk {
    public PerkHaste() {
        ItemStack guiItem = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) guiItem.getItemMeta();
        meta.setColor(Color.YELLOW);
        meta.setDisplayName(ChatColor.YELLOW + "Haste");
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        meta.setLore(List.of(
                "",
                ChatColor.GRAY + "Gain permanent Haste II"
        ));
        guiItem.setItemMeta(meta);

        setGuiItem(guiItem);

        setCost(4);

        setKey("perk_haste");
    }

    @Override
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1, true, false));
    }

    @Override
    public void onDeath(PlayerDeathEvent e) {

    }

    @Override
    public void onEquip(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1, true, false));
    }

    @Override
    public void onUnequip(Player p) {
        p.removePotionEffect(PotionEffectType.FAST_DIGGING);
    }
}
