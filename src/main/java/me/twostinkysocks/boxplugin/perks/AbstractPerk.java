package me.twostinkysocks.boxplugin.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractPerk {

    protected ItemStack guiItem;

    protected String key;

    public AbstractPerk() {
        this.guiItem = null;
        this.key = null;
    }

    // player argument can be used by overrides
    public ItemStack getGuiItem(Player p) {
        return guiItem;
    }

    public String getKey() {
        return key;
    }

    public void setGuiItem(ItemStack guiItem) {
        this.guiItem = guiItem;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void onRespawn(PlayerRespawnEvent e) {
    }

    public void onDeath(PlayerDeathEvent e) {
    }

    public void onEquip(Player p) {
    }

    public void onUnequip(Player p) {
    }
}
