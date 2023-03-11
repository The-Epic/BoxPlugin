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

public class SexShovel extends CustomItem {

    private HashMap<UUID, Long> cooldown;

    public SexShovel(CustomItemsMain plugin) {
        super(
                "§3Sex Shovel",
                "SEX_SHOVEL",
                Material.GOLDEN_SHOVEL,
                plugin,
                "",
                "§6Item Ability: Warp §e§lRIGHT CLICK",
                "§7Teleport forward 10 blocks on cast",
                "§8Cooldown: §a10s",
                "",
                "§8Can teleport through some ceilings"
        );
        cooldown = new HashMap<>();
        setRightClick(p -> {
            if(p.hasPermission("customitems.cooldownbypass") || !cooldown.containsKey(p.getUniqueId()) || cooldown.get(p.getUniqueId()) < System.currentTimeMillis()) {
                cooldown.put(p.getUniqueId(), System.currentTimeMillis() + 10000); // 10 seconds
                Block b = p.getTargetBlock(null, 10);
                Location l = b.getLocation();
                float pitch = p.getLocation().getPitch();
                float yaw = p.getLocation().getYaw();
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
