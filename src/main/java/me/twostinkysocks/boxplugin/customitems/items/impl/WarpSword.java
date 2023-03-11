package me.twostinkysocks.boxplugin.customitems.items.impl;

import me.twostinkysocks.boxplugin.customitems.CustomItemsMain;
import me.twostinkysocks.boxplugin.customitems.items.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.UUID;

public class WarpSword extends CustomItem {

    private HashMap<UUID, Long> cooldown;

    public WarpSword(CustomItemsMain plugin) {
        super(
                "§9Warp Sword",
                "WARP_SWORD",
                Material.DIAMOND_SWORD,
                plugin,
                "",
                "§6Item Ability: Warp §e§lRIGHT CLICK",
                "§7Teleport forward 12 blocks on cast",
                "",
                "§9§lRARE"
        );
        cooldown = new HashMap<>();
        setRightClick(p -> {
            if(p.hasPermission("customitems.cooldownbypass") || !cooldown.containsKey(p.getUniqueId()) || cooldown.get(p.getUniqueId()) < System.currentTimeMillis()) {
                cooldown.put(p.getUniqueId(), System.currentTimeMillis() + 4000); // 4 seconds
                Block b = p.getTargetBlock(null, 12);
                Location l = b.getLocation();
                float pitch = p.getEyeLocation().getPitch();
                float yaw = p.getEyeLocation().getYaw();
                l.add(0,1,0);
                l.setYaw(yaw);
                l.setPitch(pitch);
                p.teleport(l);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            } else {
                BigDecimal bd = new BigDecimal(((double)(cooldown.get(p.getUniqueId()) - System.currentTimeMillis()))/1000.0);
                bd = bd.round(new MathContext(2));
                p.sendMessage(ChatColor.RED + "That's too fast! Wait " + bd.doubleValue() + " more seconds!");
                p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 3.0F, 1.0F);
            }
        });
    }
}
