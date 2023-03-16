package me.twostinkysocks.boxplugin.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractSelectablePerk extends AbstractPerk {

    private int cost;

    public AbstractSelectablePerk() {
        super();
        this.cost = 0;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
