package me.twostinkysocks.boxplugin.manager;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.event.PlayerBoxXpUpdateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class XPManager {

    public XPManager() {
        Bukkit.getScheduler().runTaskTimer(BoxPlugin.instance, () -> {
            Bukkit.getOnlinePlayers().forEach(p -> {
                int beforexp = BoxPlugin.instance.getXpManager().getXP(p);
                BoxPlugin.instance.getXpManager().addXP(p, 200);
                int afterxp = BoxPlugin.instance.getXpManager().getXP(p);
                Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(p, beforexp, afterxp));
            });
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a&lFREE XP! &7You earned 200 xp!"));
        }, 36000, 36000);
    }

    public int getXP(Player p) {
        return p.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER);
    }

    public void setXP(Player p, int xp) {
        p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER, xp);
    }

    public void addXP(Player p, int xp) {
        p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER, getXP(p) + xp);
    }

    public int getLevel(Player p) {
        int xp = getXP(p);
        return convertXPToLevel(xp);
    }

    public int convertXPToLevel(int xp) {
        return (int) (Math.sqrt(Math.abs(((double)xp)/20))+1);
    }

    public int convertLevelToXP(int level) {
        return (int) (20 * Math.pow(level - 1, 2));
    }

    public int getNeededXp(Player p) {
        int xp = getXP(p);
        int level = convertXPToLevel(xp);
        int xpDiff = convertLevelToXP(level+1) - convertLevelToXP(level);
        int xpRemainder = xp - convertLevelToXP(level);
        return xpDiff - xpRemainder;
    }

    public void handleGroupUpdate(Player p, int beforelevel, int afterlevel) {
        User user = BoxPlugin.instance.getLuckPerms().getUserManager().getUser(p.getUniqueId());
        // add xp
        if(beforelevel < 20 && afterlevel >= 20) {
            InheritanceNode node = InheritanceNode.builder("lvl20").value(true).build();
            user.data().add(node);
        }
        if(beforelevel < 35 && afterlevel >= 35) {
            InheritanceNode node = InheritanceNode.builder("lvl35").value(true).build();
            user.data().add(node);
        }
        if(beforelevel < 50 && afterlevel >= 50) {
            InheritanceNode node = InheritanceNode.builder("lvl50").value(true).build();
            user.data().add(node);
        }
        if(beforelevel < 70 && afterlevel >= 70) {
            InheritanceNode node = InheritanceNode.builder("lvl70").value(true).build();
            user.data().add(node);
        }
        if(beforelevel < 100 && afterlevel >= 100) {
            InheritanceNode node = InheritanceNode.builder("lvl100").value(true).build();
            user.data().add(node);
        }

        // remove xp
        if(afterlevel < 100 && beforelevel >= 100) {
            InheritanceNode node = InheritanceNode.builder("lvl100").value(false).build();
            user.data().add(node);
        }
        if(afterlevel < 70 && beforelevel >= 70) {
            InheritanceNode node = InheritanceNode.builder("lvl70").value(false).build();
            user.data().add(node);
        }
        if(afterlevel < 50 && beforelevel >= 50) {
            InheritanceNode node = InheritanceNode.builder("lvl50").value(false).build();
            user.data().add(node);
        }
        if(afterlevel < 35 && beforelevel >= 35) {
            InheritanceNode node = InheritanceNode.builder("lvl35").value(false).build();
            user.data().add(node);
        }
        if(afterlevel < 20 && beforelevel >= 20) {
            InheritanceNode node = InheritanceNode.builder("lvl20").value(false).build();
            user.data().add(node);
        }
        BoxPlugin.instance.getLuckPerms().getUserManager().saveUser(user);
    }

}
