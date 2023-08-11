package me.twostinkysocks.boxplugin.customitems;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.customitems.items.CustomItem;
import me.twostinkysocks.boxplugin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.naming.Name;
import java.util.ArrayList;

public class Listeners implements Listener {

    private ArrayList<CustomItem> items;

    public Listeners(ArrayList<CustomItem> items) {
        this.items = items;
    }


    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if(item != null && item.getItemMeta() != null) {
            if(item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING)) {
                String itemId = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING);
                if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    for(CustomItem i : items) {
                        if(itemId.equals(i.getItemId())) {
                            i.getClick().accept(e, e.getAction());
                        }
                    }
                } else if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if(e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
                        for(CustomItem i : items) {
                            if(itemId.equals(i.getItemId())) {
                                i.getClick().accept(e, e.getAction());
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void entityInteract(EntityInteractEvent e) {
        for(CustomItem i : items) {
            i.getEntityInteract().accept(e);
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            for(ItemStack item : p.getInventory().getContents()) {
                // TODO: implement for all items
                if(item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING) && item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING).equals("TALISMAN_OF_ENERGY")) {
                    for(CustomItem ci : items) {
                        if(ci.getItemId().equals("TALISMAN_OF_ENERGY")) {
                            ci.getEntityDamageByEntity().accept(e);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void entityDamageForGrenade(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) e.getDamager();
            if(tnt.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "CLUSTER_GRENADE_ENTITY"), PersistentDataType.STRING)) {
                if(e.getEntity() instanceof Player) {
                    Player p = (Player) e.getEntity();
                    Util.debug(p, "Pre-calcualtion raw tnt damage: " + e.getDamage());
                }
                e.setDamage(e.getDamage()*3);
                if(e.getEntity() instanceof Player) {
                    Player p = (Player) e.getEntity();
                    Util.debug(p, "Post-calculation raw tnt damage: " + e.getDamage());
                    Util.debug(p, "Post-calculation final tnt damage: " + e.getFinalDamage());
                }
            }
        }
    }

    @EventHandler
    public void armorStandInteract(PlayerArmorStandManipulateEvent e) {
        if(e.getRightClicked().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "THROWING_KNIFE"), PersistentDataType.INTEGER)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        if(e.getTarget() instanceof Player) {
            Player p = (Player) e.getTarget();
            ItemStack item = p.getInventory().getItemInMainHand();
            if(item != null && item.getItemMeta() != null) {
                if(item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING)) {
                    String itemId = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING);
                    for(CustomItem i : items) {
                        if(itemId.equals(i.getItemId())) {
                            i.getEntityTarget().accept(e);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBow(EntityShootBowEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if((p.getInventory().getItemInMainHand().getType() == Material.BOW && p.getInventory().getItemInMainHand().hasItemMeta() && p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING) && p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING).equals("PULSE_BOW")) || (p.getInventory().getItemInOffHand().getType() == Material.BOW && p.getInventory().getItemInOffHand().hasItemMeta() && p.getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING) && p.getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING).equals("PULSE_BOW"))) {
                e.getProjectile().getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "PULSE_ARROW"), PersistentDataType.INTEGER, 1);
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1f, 2f);
                e.getProjectile().setVelocity(e.getProjectile().getVelocity().multiply(2));
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        for(CustomItem i : items) {
            i.getLeave().accept(e.getPlayer());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        for(CustomItem i : items) {
            i.getJoin().accept(e.getPlayer());
        }
    }
}
