package me.twostinkysocks.boxplugin.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.twostinkysocks.boxplugin.BoxPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "2stinkysocks";
    }

    @Override
    public String getIdentifier() {
        return "BoxPlugin";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        int xp = 0;
        int level = 0;
        if(player instanceof Player) {
            Player p = (Player) player;
            xp = BoxPlugin.instance.getXpManager().getXP(p);
            level = BoxPlugin.instance.getXpManager().getLevel(p);
        } else {
            xp = BoxPlugin.instance.getOfflineXPFile().getInt(player.getUniqueId().toString());
            level = BoxPlugin.instance.getXpManager().convertXPToLevel(xp);
        }
        if(params.equalsIgnoreCase("boxxp")){
            return String.valueOf(xp);
        }

        if(params.equalsIgnoreCase("boxlevel")) {
            return String.valueOf(level);
        }
        return null; // Placeholder is unknown by the Expansion
    }

}
