package me.twostinkysocks.boxplugin.event;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.manager.PerksManager;
import me.twostinkysocks.boxplugin.perks.PerkXPBoost;
import me.twostinkysocks.boxplugin.util.Util;
import me.twostinkysocks.boxplugin.manager.PerksManager.Perk;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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
        if(e.getEntityType() == EntityType.GUARDIAN) {
            Item drop = (Item) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.DROPPED_ITEM);
            drop.setItemStack(new ItemStack(Material.PRISMARINE_CRYSTALS));
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
        if(e.getDamager() instanceof Player && (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
            System.out.println("correct dmg type");
            Player p = (Player) e.getDamager();
            if(BoxPlugin.instance.getPerksManager().getSelectedMegaPerks(p).contains(PerksManager.MegaPerk.MEGA_LIFESTEAL)) {
                System.out.println("healing");
                p.setHealth(Math.min(p.getHealth() + (e.getFinalDamage() * 0.1), p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
            }
        }
        if(e.getDamager() instanceof WitherSkull && !(e.getEntity() instanceof HumanEntity) && !(e.getEntity() instanceof Mob)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void hangingDamage(HangingBreakByEntityEvent e) {
        if((e.getRemover() instanceof WitherSkull || e.getRemover() instanceof Boat)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void hangingDamage(HangingBreakEvent e) {
        if(e.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        for(Perk perk : BoxPlugin.instance.getPerksManager().getSelectedPerks(p)) {
            if(perk != null) {
                perk.instance.onEquip(p);
            }
        }
        if(p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.DOUBLE)) {
            p.getPersistentDataContainer().remove(new NamespacedKey(BoxPlugin.instance, "xp"));
        }
        if(!p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "xp"), PersistentDataType.INTEGER, 0);
        }

        // legacy xp

        if(BoxPlugin.instance.getXpManager().getXP(p) == 0) { // add flag by default if new player
            p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "legacylevelscompensated"), PersistentDataType.INTEGER, 1);
        }
        if(BoxPlugin.instance.getXpManager().getXP(p) > 0 && !p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "legacylevelscompensated"), PersistentDataType.INTEGER)) {
            p.sendTitle(ChatColor.RED + "Make sure to read chat!", null, 10, 100, 10);
            Bukkit.getScheduler().runTaskLater(BoxPlugin.instance, () -> {
                p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Unrewarded Levels!");
                p.sendMessage(ChatColor.RED + "Leveling up now rewards coins over time, which you haven't claimed!");
                p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You cannot gain xp until you claim your levelup rewards with " + ChatColor.GREEN + ChatColor.BOLD + "/claimlegacyrewards");
                p.sendMessage(ChatColor.RED + "Make sure to clear your inventory before claiming rewards!");
            },5);
        }

        //


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
                for(Perk perk : BoxPlugin.instance.getPerksManager().getSelectedPerks(p)) {
                    if(perk != null) perk.instance.onRespawn(e);
                }
                for(PerksManager.MegaPerk perk : BoxPlugin.instance.getPerksManager().getSelectedMegaPerks(p)) {
                    if(perk != null) perk.instance.onRespawn(e);
                }
            }, 1L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        // more than 10 below - 0xp
        // up to 10 below - 10xp
        // your level or higher - 100xp
        Player cause = e.getEntity().getKiller();
        Player target = e.getEntity();

        for(Perk perk : BoxPlugin.instance.getPerksManager().getSelectedPerks(target)) {
            if(perk != null) perk.instance.onDeath(e);
        }
        for(PerksManager.MegaPerk perk : BoxPlugin.instance.getPerksManager().getSelectedMegaPerks(target)) {
            if(perk != null) perk.instance.onDeath(e);
        }

        if(cause == null) {
            BoxPlugin.instance.getPvpManager().resetStreak(target);
            BoxPlugin.instance.getScoreboardManager().queueUpdate(target);
            return;
        }

        int causelevel = BoxPlugin.instance.getXpManager().getLevel(cause);
        int causexp = BoxPlugin.instance.getXpManager().getXP(cause);
        int targetlevel = BoxPlugin.instance.getXpManager().getLevel(target);
        if(targetlevel >= 400) {
            HashMap<Integer, ItemStack> toDrop = cause.getInventory().addItem(new ItemStack(Material.SKELETON_SKULL, BoxPlugin.instance.getPvpManager().getBounty(target)));
            toDrop.forEach((i, item) -> {
                Item droppedItem = (Item) cause.getWorld().spawnEntity(cause.getLocation(), EntityType.DROPPED_ITEM);
                droppedItem.setItemStack(item);
            });
            cause.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSkulls Claimed! &7You claimed " + BoxPlugin.instance.getPvpManager().getBounty(target) + " skulls from " + target.getName()));
            BoxPlugin.instance.getXpManager().addXP(cause, 50000);
            Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(cause, causexp, causexp + 50000));
        } else if(targetlevel >= 150 && causelevel >= 150) {
            if(causelevel - targetlevel >= 40) { // difference is greater than 40
                e.setKeepInventory(true);
                e.getDrops().clear();
                for(int i = 0; i < e.getEntity().getInventory().getSize(); i++) {
                    int rand = (int)(Math.random() * (4) + 1);
                    if(rand == 1) {
                        if(e.getEntity().getInventory().getItem(i) != null) {
                            Item itemDrop = (Item) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.DROPPED_ITEM);
                            itemDrop.setItemStack(e.getEntity().getInventory().getItem(i));
                            e.getEntity().getInventory().setItem(i, null);
                        }
                    }
                }
                ArrayList<ItemStack> armor = new ArrayList<>(List.of(e.getEntity().getInventory().getArmorContents()));
                for(int i = 0; i < e.getEntity().getInventory().getArmorContents().length; i++) {
                    int rand = (int)(Math.random() * (4) + 1);
                    if(rand == 1) {
                        if(e.getEntity().getInventory().getArmorContents()[i] != null) {
                            Item itemDrop = (Item) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.DROPPED_ITEM);
                            itemDrop.setItemStack(e.getEntity().getInventory().getArmorContents()[i]);
                            armor.set(i, null);
                        }
                    }
                }
                e.getEntity().getInventory().setArmorContents(armor.toArray(new ItemStack[4]));
                BoxPlugin.instance.getXpManager().addXP(cause, 1000);
                Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(cause, causexp, causexp + 1000));
                target.sendMessage(ChatColor.GRAY + "You kept most of your items, because the player who killed you was a significantly higher level.");
            } else {
                BoxPlugin.instance.getXpManager().addXP(cause, 15000);
                Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(cause, causexp, causexp + 15000));
            }
            HashMap<Integer, ItemStack> toDrop = cause.getInventory().addItem(new ItemStack(Material.SKELETON_SKULL, BoxPlugin.instance.getPvpManager().getBounty(target)));
            toDrop.forEach((i, item) -> {
                Item droppedItem = (Item) cause.getWorld().spawnEntity(cause.getLocation(), EntityType.DROPPED_ITEM);
                droppedItem.setItemStack(item);
            });
            cause.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSkulls Claimed! &7You claimed " + BoxPlugin.instance.getPvpManager().getBounty(target) + " skulls from " + target.getName()));
        } else if(targetlevel >= causelevel) {
            HashMap<Integer, ItemStack> toDrop = cause.getInventory().addItem(new ItemStack(Material.SKELETON_SKULL, BoxPlugin.instance.getPvpManager().getBounty(target)));
            toDrop.forEach((i, item) -> {
                Item droppedItem = (Item) cause.getWorld().spawnEntity(cause.getLocation(), EntityType.DROPPED_ITEM);
                droppedItem.setItemStack(item);
            });
            cause.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSkulls Claimed! &7You claimed " + BoxPlugin.instance.getPvpManager().getBounty(target) + " skulls from " + target.getName()));
            BoxPlugin.instance.getXpManager().addXP(cause, 3000);
            Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(cause, causexp, causexp + 3000));
        } else if(targetlevel - causelevel >= -20) { // difference is less than 20
            HashMap<Integer, ItemStack> toDrop = cause.getInventory().addItem(new ItemStack(Material.SKELETON_SKULL, BoxPlugin.instance.getPvpManager().getBounty(target)));
            toDrop.forEach((i, item) -> {
                Item droppedItem = (Item) cause.getWorld().spawnEntity(cause.getLocation(), EntityType.DROPPED_ITEM);
                droppedItem.setItemStack(item);
            });
            cause.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSkulls Claimed! &7You claimed " + BoxPlugin.instance.getPvpManager().getBounty(target) + " skulls from " + target.getName()));
            BoxPlugin.instance.getXpManager().addXP(cause, 500);
            Bukkit.getPluginManager().callEvent(new PlayerBoxXpUpdateEvent(cause, causexp, causexp + 500));
        } else {
            if(causelevel - targetlevel >= 20) { // difference is greater than 20
                e.setKeepInventory(true);
                e.getDrops().clear();
                for(int i = 0; i < e.getEntity().getInventory().getSize(); i++) {
                    int rand = (int)(Math.random() * (4) + 1);
                    if(rand == 1) {
                        if(e.getEntity().getInventory().getItem(i) != null) {
                            Item itemDrop = (Item) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.DROPPED_ITEM);
                            itemDrop.setItemStack(e.getEntity().getInventory().getItem(i));
                            e.getEntity().getInventory().setItem(i, null);
                        }
                    }
                }
                ArrayList<ItemStack> armor = new ArrayList<>(List.of(e.getEntity().getInventory().getArmorContents()));
                for(int i = 0; i < e.getEntity().getInventory().getArmorContents().length; i++) {
                    int rand = (int)(Math.random() * (4) + 1);
                    if(rand == 1) {
                        if(e.getEntity().getInventory().getArmorContents()[i] != null) {
                            Item itemDrop = (Item) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.DROPPED_ITEM);
                            itemDrop.setItemStack(e.getEntity().getInventory().getArmorContents()[i]);
                            armor.set(i, null);
                        }
                    }
                }
                e.getEntity().getInventory().setArmorContents(armor.toArray(new ItemStack[4]));
                target.sendMessage(ChatColor.GRAY + "You kept most of your items, because the player who killed you was a significantly higher level.");
            }
            if(BoxPlugin.instance.getPvpManager().getBounty(target) > 1) {
                HashMap<Integer, ItemStack> toDrop = cause.getInventory().addItem(new ItemStack(Material.SKELETON_SKULL, BoxPlugin.instance.getPvpManager().getBounty(target)));
                toDrop.forEach((i, item) -> {
                    Item droppedItem = (Item) cause.getWorld().spawnEntity(cause.getLocation(), EntityType.DROPPED_ITEM);
                    droppedItem.setItemStack(item);
                });
                cause.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSkulls Claimed! &7You claimed " + BoxPlugin.instance.getPvpManager().getBounty(target) + " skulls from " + target.getName()));
            }
        }

        BoxPlugin.instance.getPvpManager().registerKill(cause, target); // resets streak here
        BoxPlugin.instance.getScoreboardManager().queueUpdate(cause);
        BoxPlugin.instance.getScoreboardManager().queueUpdate(target);
    }

    @EventHandler
    public void onUpdateXp(PlayerBoxXpUpdateEvent e) {
        Player p = e.getPlayer();
        double multiplier = ((PerkXPBoost)Perk.XPBOOST.instance).calculateXPMultiplier(p).doubleValue();
        int beforelevel = BoxPlugin.instance.getXpManager().convertXPToLevel(e.getBeforeXP());
        int difference = e.getAfterXP() - e.getBeforeXP();
        if(difference > 0 && !e.isMultiplierBypassed() && BoxPlugin.instance.getPerksManager().getSelectedPerks(p).contains(Perk.XPBOOST)) { // xp gain
            int toAdd = (int) (difference * multiplier) - difference;
            BoxPlugin.instance.getXpManager().addXP(p, toAdd);
        }
        int afterlevel = BoxPlugin.instance.getXpManager().convertXPToLevel(BoxPlugin.instance.getXpManager().getXP(p));

        // legacy xp

        if(!p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "legacylevelscompensated"), PersistentDataType.INTEGER)) {
            p.sendTitle(ChatColor.RED + "Make sure to read chat!", null, 10, 100, 10);
            p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Unrewarded Levels!");
            p.sendMessage(ChatColor.RED + "Leveling up now rewards coins over time, which you haven't claimed!");
            p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You cannot gain xp until you claim your levelup rewards with " + ChatColor.GREEN + ChatColor.BOLD + "/claimlegacyrewards");
            p.sendMessage(ChatColor.RED + "Make sure to clear your inventory before claiming rewards!");
            BoxPlugin.instance.getXpManager().setXP(p, e.getBeforeXP());
            return;
        }

        if(BoxPlugin.instance.getXpManager().getLevel(p)%5 == 0&& BoxPlugin.instance.getXpManager().convertXPToLevel(e.getBeforeXP())%5 != 0) {
            int toGive = BoxPlugin.instance.getXpManager().getLevelUpRewardLevelToLevel(BoxPlugin.instance.getXpManager().convertXPToLevel(e.getBeforeXP()), BoxPlugin.instance.getXpManager().getLevel(p));
            HashMap<Integer, ItemStack> toDrop = p.getInventory().addItem(Util.gigaCoin(toGive));
            toDrop.forEach((index, item) -> {
                Item entity = (Item) p.getWorld().spawnEntity(p.getLocation(), EntityType.DROPPED_ITEM);
                entity.setItemStack(item);
            });
            p.sendMessage(ChatColor.GOLD + "Earned " + ChatColor.BOLD + toGive + " Giga Coins " + ChatColor.GOLD + "from leveling up!");
        } else if(Math.abs(BoxPlugin.instance.getXpManager().getLevel(p)-BoxPlugin.instance.getXpManager().convertXPToLevel(e.getBeforeXP()))>=5) {
            int toGive = BoxPlugin.instance.getXpManager().getLevelUpRewardLevelToLevel(BoxPlugin.instance.getXpManager().convertXPToLevel(e.getBeforeXP()), BoxPlugin.instance.getXpManager().getLevel(p));
            HashMap<Integer, ItemStack> toDrop = p.getInventory().addItem(Util.gigaCoin(toGive));
            toDrop.forEach((index, item) -> {
                Item entity = (Item) p.getWorld().spawnEntity(p.getLocation(), EntityType.DROPPED_ITEM);
                entity.setItemStack(item);
            });
            p.sendMessage(ChatColor.GOLD + "Earned " + ChatColor.BOLD + toGive + " Giga Coins " + ChatColor.GOLD + "from leveling up!");
        }

        //
        BoxPlugin.instance.getXpManager().handleGroupUpdate(p, beforelevel, afterlevel);
        if(beforelevel < afterlevel) {
            p.resetTitle();
            p.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "LEVEL UP!", ChatColor.GRAY + "" + beforelevel + " → " + afterlevel, 10, 40, 10);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1f);
        }
        // remove second perk
        if(afterlevel < 50 && beforelevel >= 50) {
            List<Perk> selected = BoxPlugin.instance.getPerksManager().getSelectedPerks(p);
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

    @EventHandler
    public void onPortal(PlayerPortalEvent e) {
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            e.setCancelled(true);
        }
    }

}
