package me.twostinkysocks.boxplugin.perks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class MegaPerkLifeSteal extends AbstractPerk {
    public MegaPerkLifeSteal() {
        ItemStack guiItem = new ItemStack(Material.REDSTONE);
        ItemMeta meta = guiItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Lifesteal");
        meta.setLore(List.of(
                "",
                ChatColor.GRAY + "Heal for 15% of the final melee damage you deal to enemies"
        ));
        guiItem.setItemMeta(meta);

        setGuiItem(guiItem);

        setCost(1);

        setKey("mega_perk_lifesteal");
    }
}
