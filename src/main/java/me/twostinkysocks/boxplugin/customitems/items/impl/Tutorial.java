package me.twostinkysocks.boxplugin.customitems.items.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.customitems.CustomItemsMain;
import me.twostinkysocks.boxplugin.customitems.items.CustomItem;
import me.twostinkysocks.boxplugin.manager.PerksManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class Tutorial extends CustomItem {

    public Tutorial(CustomItemsMain plugin) {
        super(
                "§d§lTutorial Quest",
                "TUTORIAL",
                Material.KNOWLEDGE_BOOK,
                plugin,
                "§bRight click to start the §ltutorial§b quest. You",
                "§bwill receive a reward at the end of the quest, and",
                "§bget a better understanding of the game."
        );
        setClick((e, a) -> {
            if(a == Action.RIGHT_CLICK_BLOCK || a == Action.RIGHT_CLICK_AIR) {
                Player p = e.getPlayer();
                e.setCancelled(true);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "quest start " + p.getName() + " 0 -overrideRequirements");
                e.getPlayer().getInventory().remove(e.getItem());
            }
        });
    }
    @Override
    public ItemStack getItemStack() {
        ItemStack stack = super.getItemStack();
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        stack.setItemMeta(meta);
        return stack;
    }
}
