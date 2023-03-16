package me.twostinkysocks.boxplugin.perks;

import me.twostinkysocks.boxplugin.BoxPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public abstract class AbstractUpgradablePerk extends AbstractPerk {

    public abstract int getUpgradeCost(Player p);

    public int getLevel(Player p) {
        return p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, getKey() + "_level"), PersistentDataType.INTEGER) ? p.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, getKey() + "_level"), PersistentDataType.INTEGER) : 0;
    }

    public abstract void upgrade(Player p);

    public void incrementLevel(Player p) {
        if(p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, getKey() + "_level"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, getKey() + "_level"), PersistentDataType.INTEGER, p.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, getKey() + "_level"), PersistentDataType.INTEGER)+1);
        } else {
            p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, getKey() + "_level"), PersistentDataType.INTEGER, 1);
        }
    }

    public void setLevel(Player p, int level) {
        p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, getKey() + "_level"), PersistentDataType.INTEGER, level);
    }

}
