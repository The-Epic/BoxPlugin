package me.twostinkysocks.boxplugin.customitems;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.customitems.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityTargetEvent;
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

//    @EventHandler
//    public void entityDamage(EntityDamageByEntityEvent e) {
//        if(e.getDamager() instanceof Player) {
//            Player p = (Player) e.getDamager();
//            for(ItemStack item : p.getInventory().getContents()) {
//                // TODO: implement for all items
//                if(item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING) && item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING).equals("TALISMAN_OF_ENERGY")) {
//                    for(CustomItem ci : items) {
//                        if(ci.getItemId().equals("TALISMAN_OF_ENERGY")) {
//                            ci.getEntityDamageByEntity().accept(e);
//                        }
//                    }
//                }
//            }
//        }
//    }

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
