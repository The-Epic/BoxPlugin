package me.twostinkysocks.boxplugin.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractPerk {

    private ItemStack guiItem;

    private int cost;

    private String key;

    public AbstractPerk() {
        this.guiItem = null;
        this.cost = 0;
        this.key = null;
    }

    public ItemStack getGuiItem() {
        return guiItem;
    }

    public int getCost() {
        return cost;
    }

    public String getKey() {
        return key;
    }

    public void setGuiItem(ItemStack guiItem) {
        this.guiItem = guiItem;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public abstract void onRespawn(PlayerRespawnEvent e);

    public abstract void onDeath(PlayerDeathEvent e);

    public abstract void onEquip(Player p);

    public abstract void onUnequip(Player p);
}
