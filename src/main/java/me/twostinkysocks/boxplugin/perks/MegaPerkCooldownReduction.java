package me.twostinkysocks.boxplugin.perks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

public class MegaPerkCooldownReduction extends AbstractPerk {
    public MegaPerkCooldownReduction() {
        ItemStack guiItem = new ItemStack(Material.CLOCK);
        ItemMeta meta = guiItem.getItemMeta();
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.setDisplayName(ChatColor.AQUA + "Cooldown Reduction");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        meta.setLore(List.of(
                "",
                ChatColor.GRAY + "Reduce the cooldowns of custom items by 50%"
        ));
        guiItem.setItemMeta(meta);

        setGuiItem(guiItem);

        setCost(1);

        setKey("mega_perk_cooldown_reduction");
    }
}
