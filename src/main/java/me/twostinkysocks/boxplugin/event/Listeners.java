package me.twostinkysocks.boxplugin.event;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.perks.AbstractSelectablePerk;
import me.twostinkysocks.boxplugin.perks.AbstractUpgradablePerk;
import me.twostinkysocks.boxplugin.perks.impl.PerkXPBoost;
import me.twostinkysocks.boxplugin.util.Util;
import me.twostinkysocks.boxplugin.manager.PerksManager.Perk;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class Listeners implements Listener {

    @EventHandler
    public void entityDeath(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof Player) && e.getEntity().getKiller() != null) {
            if(BoxPlugin.instance.entityExperience.containsKey(e.getEntityType()) && !e.getEntity().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER)) {
                int before = BoxPlugin.instance.getXpManager().getXP(e.getEntity().getKiller());
                BoxPlugin.instance.getXpManager().addXP(e.getEntity().getKiller(), BoxPlugin.instance.entityExperience.get(e.getEntityType()));
                int after = BoxPlugin.instance.getXpManager().getXP(e.getEntity().getKiller());
                Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(e.getEntity().getKiller(), before, after));
            }
        }
        if(!(e.getEntity() instanceof Player) && e.getEntity().getKiller() != null &&e.getEntity().getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER)) {
            int before = BoxPlugin.instance.getXpManager().getXP(e.getEntity().getKiller());
            BoxPlugin.instance.getXpManager().addXP(e.getEntity().getKiller(), e.getEntity().getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER));
            int after = BoxPlugin.instance.getXpManager().getXP(e.getEntity().getKiller());
            Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(e.getEntity().getKiller(), before, after));
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if(e.getMessage().startsWith("/spawn")) {
            if(BoxPlugin.instance.getPvpManager().getStreak(e.getPlayer()) >= 20) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "You can't /spawn with a high streak!");
            }
        }
    }

    @EventHandler
    public void entityInteract(PlayerInteractEntityEvent e) {
        Entity interacted = e.getRightClicked();
        Player p = e.getPlayer();

//        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
//        RegionQuery query = container.createQuery();
//        ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(interacted.getLocation().getWorld())).getApplicableRegions(BlockVector3.at(interacted.getLocation().getX(),interacted.getLocation().getY(),interacted.getLocation().getZ()));
//        if(!set.testState(WorldGuardPlugin.inst().wrapPlayer(p), BoxPlugin.instance.getEntityInteractFlag())) {
//            e.setCancelled(true);
//            p.sendMessage(ChatColor.RED + "You don't meet the level requirement to access this!");
//        }

        if(interacted.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "perk_npc"), PersistentDataType.INTEGER) && interacted.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "perk_npc"), PersistentDataType.INTEGER) == 1) {
            e.setCancelled(true);
            BoxPlugin.instance.getPerksManager().openMainGui(p);
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            Entity interacted = e.getEntity();
            if(interacted.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "perk_npc"), PersistentDataType.INTEGER) && interacted.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "perk_npc"), PersistentDataType.INTEGER) == 1) {
                e.setCancelled(true);
                BoxPlugin.instance.getPerksManager().openMainGui(p);
            }
        }
        if(e.getDamager() instanceof WitherSkull && !(e.getEntity() instanceof HumanEntity) && !(e.getEntity() instanceof Mob)) {
            e.setCancelled(true);
        }
        if((e.getDamager() instanceof WitherSkull || e.getDamager() instanceof Boat) && (e.getEntity() instanceof ItemFrame)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        for(AbstractSelectablePerk perk : BoxPlugin.instance.getPerksManager().getSelectedPerks(p)) {
            perk.onEquip(p);
        }
        for(AbstractUpgradablePerk perk : BoxPlugin.instance.getPerksManager().getUpgradablePerks()) {
            perk.onEquip(p);
        }
        if(p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.DOUBLE)) {
            p.getPersistentDataContainer().remove(new NamespacedKey(BoxPlugin.instance, "xp"));
        }
        if(!p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER, 0);
        }
//        BoxPlugin.instance.getLeaderboardManager().updateLeaderboard(p);
        BoxPlugin.instance.getScoreboardManager().updatePlayerScoreboard(p);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        BoxPlugin.instance.getOfflineXPFile().set(e.getPlayer().getUniqueId().toString(), BoxPlugin.instance.getXpManager().getXP(e.getPlayer()));
        try {
            BoxPlugin.instance.getOfflineXPFile().save(new File(BoxPlugin.instance.getDataFolder(), "offlinexp.yml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        BoxPlugin.instance.getScoreboardManager().getQueuedUpdates().remove(e.getPlayer());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(!BoxPlugin.instance.placedBlocks.contains(e.getBlock().getLocation())) {
            BoxPlugin.instance.placedBlocks.add(e.getBlock().getLocation());
        }
    }
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        BoxPlugin.instance.placedBlocks.remove(e.getBlock().getLocation());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        ItemStack item = event.getCursor();
        ItemStack itemselected = event.getCurrentItem();
        ItemStack hotkeyItem = event.getClick() == ClickType.NUMBER_KEY ? event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) : event.getCurrentItem();
        ItemStack offHandItem = event.getClick() == ClickType.SWAP_OFFHAND ? event.getWhoClicked().getInventory().getItem(45) : event.getCurrentItem();
//        Bukkit.broadcastMessage("CLICK EVENT:");
//        Bukkit.broadcastMessage("Slot " + event.getRawSlot());
//        Bukkit.broadcastMessage("Bottom Size " + player.getInventory().getSize());
//        Bukkit.broadcastMessage("Top Size " + player.getOpenInventory().getTopInventory().getSize());
//        Bukkit.broadcastMessage("Cursor item " + (item == null ? null : item.getType()));
//        Bukkit.broadcastMessage("Current item " + (itemselected == null ? null : itemselected.getType()));
//        Bukkit.broadcastMessage("Clicked inv " + event.getClickedInventory());
//        Bukkit.broadcastMessage("Top inv " + player.getOpenInventory().getTopInventory());
//        Bukkit.broadcastMessage("Action " + event.getAction() + "\n");


        // TODO: fix hotkey
        boolean shouldCancel = false;

        // shift click into another inventory
        if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && !(player.getOpenInventory().getTopInventory() instanceof CraftInventoryPlayer)) {
            shouldCancel = true;
        }

        // hotkey from hotbar into inventory
        else if((event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) && !(player.getOpenInventory().getTopInventory() instanceof CraftInventoryCrafting)) {
            shouldCancel = true;
        }

        // click item into inventory
        else if((event.getAction() == InventoryAction.SWAP_WITH_CURSOR || event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.PLACE_SOME) && !(event.getClickedInventory() instanceof CraftInventoryPlayer)) {
            shouldCancel = true;
        }

        if(event.getClick() == ClickType.SWAP_OFFHAND && !(player.getOpenInventory().getTopInventory() instanceof CraftInventoryCrafting)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't swap hands while in an inventory!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.25f, 0.5f);
            return;
        }


        if(shouldCancel) {
            if(Util.isPerkItem(item) || Util.isPerkItem(itemselected) || Util.isPerkItem(hotkeyItem)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can't remove perk items from your inventory!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.25f, 0.5f);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        ItemStack dragged = event.getOldCursor(); // This is the item that is being dragged

        if (Util.isPerkItem(dragged)) {
            int inventorySize = event.getInventory().getSize(); // The size of the inventory, for reference

            // Now we go through all of the slots and check if the slot is inside our inventory (using the inventory size as reference)
            for (int i : event.getRawSlots()) {
                if (i < inventorySize) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if(!e.isCancelled()) {
            if(BoxPlugin.instance.blockExperience.containsKey(e.getBlock().getType()) && !BoxPlugin.instance.placedBlocks.contains(e.getBlock().getLocation())) {
                int newxp = BoxPlugin.instance.blockExperience.get(e.getBlock().getType());
                int existingxp = p.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER);
                p.getPersistentDataContainer()
                        .set(
                                new NamespacedKey(BoxPlugin.instance, "xp"),
                                PersistentDataType.INTEGER,
                                existingxp + newxp
                        );
                Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(p, existingxp, existingxp + newxp));
            }
            BoxPlugin.instance.placedBlocks.remove(e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItemDrop().getItemStack();
        if(Util.isPerkItem(item)) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You can't drop perk items!");
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.25f, 0.5f);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, () -> {
            for(AbstractSelectablePerk perk : BoxPlugin.instance.getPerksManager().getSelectedPerks(p)) {
                perk.onRespawn(e);
            }
            for(AbstractUpgradablePerk perk : BoxPlugin.instance.getPerksManager().getUpgradablePerks()) {
                perk.onRespawn(e);
            }
            if(BoxPlugin.instance.getPerksManager().getSelectedMegaPerk(p) != null) BoxPlugin.instance.getPerksManager().getSelectedMegaPerk(p).onRespawn(e);
        }, 1L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        // more than 10 below - 0xp
        // up to 10 below - 10xp
        // your level or higher - 100xp
        Player cause = e.getEntity().getKiller();
        Player target = e.getEntity();

        for(AbstractSelectablePerk perk : BoxPlugin.instance.getPerksManager().getSelectedPerks(target)) {
            perk.onDeath(e);
        }
        for(AbstractUpgradablePerk perk : BoxPlugin.instance.getPerksManager().getUpgradablePerks()) {
            perk.onDeath(e);
        }
        if(BoxPlugin.instance.getPerksManager().getSelectedMegaPerk(target) != null) BoxPlugin.instance.getPerksManager().getSelectedMegaPerk(target).onDeath(e);

        if(cause == null) {
            BoxPlugin.instance.getPvpManager().resetStreak(target);
            BoxPlugin.instance.getScoreboardManager().queueUpdate(target);
            return;
        }

        int causelevel = BoxPlugin.instance.getXpManager().getLevel(cause);
        int causexp = BoxPlugin.instance.getXpManager().getXP(cause);
        int targetlevel = BoxPlugin.instance.getXpManager().getLevel(target);
        BoxPlugin.instance.getPvpManager().registerKill(cause, target); // resets streak here
        if(targetlevel >= 100 && causelevel >= 100) {
            HashMap<Integer, ItemStack> toDrop = cause.getInventory().addItem(new ItemStack(Material.SKELETON_SKULL, BoxPlugin.instance.getPvpManager().getBounty(target)));
            toDrop.forEach((i, item) -> {
                Item droppedItem = (Item) cause.getWorld().spawnEntity(cause.getLocation(), EntityType.DROPPED_ITEM);
                droppedItem.setItemStack(item);
            });
            cause.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSkulls Claimed! &7You claimed " + BoxPlugin.instance.getPvpManager().getBounty(target) + " skulls from " + target.getName()));
            BoxPlugin.instance.getXpManager().addXP(cause, 5000);
            Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(cause, causexp, causexp + 100));
        } else if(targetlevel >= causelevel) {
            HashMap<Integer, ItemStack> toDrop = cause.getInventory().addItem(new ItemStack(Material.SKELETON_SKULL, BoxPlugin.instance.getPvpManager().getBounty(target)));
            toDrop.forEach((i, item) -> {
                Item droppedItem = (Item) cause.getWorld().spawnEntity(cause.getLocation(), EntityType.DROPPED_ITEM);
                droppedItem.setItemStack(item);
            });
            cause.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSkulls Claimed! &7You claimed " + BoxPlugin.instance.getPvpManager().getBounty(target) + " skulls from " + target.getName()));
            BoxPlugin.instance.getXpManager().addXP(cause, 1000);
            Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(cause, causexp, causexp + 100));
        } else if(targetlevel - causelevel >= -10) {
            HashMap<Integer, ItemStack> toDrop = cause.getInventory().addItem(new ItemStack(Material.SKELETON_SKULL, BoxPlugin.instance.getPvpManager().getBounty(target)));
            toDrop.forEach((i, item) -> {
                Item droppedItem = (Item) cause.getWorld().spawnEntity(cause.getLocation(), EntityType.DROPPED_ITEM);
                droppedItem.setItemStack(item);
            });
            cause.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSkulls Claimed! &7You claimed " + BoxPlugin.instance.getPvpManager().getBounty(target) + " skulls from " + target.getName()));
            BoxPlugin.instance.getXpManager().addXP(cause, 200);
            Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(cause, causexp, causexp + 10));
        } // else nothing
        BoxPlugin.instance.getScoreboardManager().queueUpdate(cause);
        BoxPlugin.instance.getScoreboardManager().queueUpdate(target);
    }

    @EventHandler
    public void onUpdateXp(PlayerBoxXpUpdateEvent e) {
        Player p = e.getPlayer();
        double multiplier = ((PerkXPBoost) Perk.XPBOOST.instance).calculateXPMultiplier(p);
        int beforelevel = BoxPlugin.instance.getXpManager().convertXPToLevel(e.getBeforeXP());
        int difference = e.getAfterXP() - e.getBeforeXP();
        if(difference > 0 && !e.isMultiplierBypassed()) { // xp gain
            int toAdd = (int) (difference * multiplier) - difference;
            BoxPlugin.instance.getXpManager().addXP(p, toAdd);
        }
        int afterlevel = BoxPlugin.instance.getXpManager().convertXPToLevel(e.getAfterXP());

        BoxPlugin.instance.getXpManager().handleGroupUpdate(p, beforelevel, afterlevel);
        if(beforelevel < afterlevel) {
            p.resetTitle();
            p.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "LEVEL UP!", ChatColor.GRAY + "" + beforelevel + " → " + afterlevel, 10, 40, 10);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1f);
        }

        // remove second perk
        if(afterlevel < 50 && beforelevel >= 50) {
            List<AbstractSelectablePerk> selected = BoxPlugin.instance.getPerksManager().getSelectedPerks(p);
            if(selected.size() >= 1) {
                BoxPlugin.instance.getPerksManager().setSelectedPerks(p, List.of(selected.get(0)));
            }
        }

        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 2f);
        if(e.getAfterXP() < 0) {
            p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER, Math.abs(e.getAfterXP()));
        }
        BoxPlugin.instance.getScoreboardManager().queueUpdate(p);
    }

    @EventHandler
    public void onSplit(SlimeSplitEvent e) {
        if(e.getEntity() instanceof MagmaCube) {
            if(e.getEntity().getSize() == 2) {
                e.setCancelled(true);
            }
        }
    }

}
