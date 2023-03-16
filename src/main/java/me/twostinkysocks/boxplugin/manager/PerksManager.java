package me.twostinkysocks.boxplugin.manager;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.google.common.collect.Maps;
import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.perks.*;
import me.twostinkysocks.boxplugin.perks.impl.*;
import me.twostinkysocks.boxplugin.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PerksManager {

    // TODO: perk obsidian
    // TODO: megaperks: speed 5, strength 6, resistance 2, regen 1


    // perk_speed, etc, are all integers, 0 or 1

    // selected_perks is a string of all perk keys, separated by \n

    public enum Perk {
        SPEED(new PerkSpeed()),
        STRENGTH(new PerkStrength()),
        WATER_BREATHING(new PerkWaterBreathing()),
        HASTE(new PerkHaste()),
        ROCKETS(new PerkRockets()),
        OBSIDIAN(new PerkObsidian()),
        XPBOOST(new PerkXPBoost());

        public final AbstractPerk instance;

        private static final Map<String, Perk> BY_NAME = Maps.newHashMap();

        private Perk(AbstractPerk instance) {
            this.instance = instance;
        }

        public static Perk getByName(String name) {
            return BY_NAME.get(name);
        }

        public static List<String> getKeys() {
            return new ArrayList<String>(BY_NAME.keySet());
        }

        static {
            for (Perk perk : values()) {
                BY_NAME.put(perk.instance.getKey(), perk);
            }
        }
    }

    public enum MegaPerk {
        MEGA_SPEED(new MegaPerkSpeed()),
        MEGA_STRENGTH(new MegaPerkStrength()),
        MEGA_RESISTANCE(new MegaPerkResistance()),
        MEGA_REGENERATION(new MegaPerkRegeneration());

        public final AbstractPerk instance;

        private static final Map<String, MegaPerk> BY_NAME = Maps.newHashMap();

        private MegaPerk(AbstractPerk instance) {
            this.instance = instance;
        }

        public static MegaPerk getByName(String name) {
            return BY_NAME.get(name);
        }

        public static List<String> getKeys() {
            return new ArrayList<String>(BY_NAME.keySet());
        }

        static {
            for (MegaPerk perk : values()) {
                BY_NAME.put(perk.instance.getKey(), perk);
            }
        }
    }

    public void openMainGui(Player p) {
        ChestGui gui = new ChestGui(6, "Equipped Perks");
        StaticPane pane = new StaticPane(0, 0, 9, 6);
        List<AbstractSelectablePerk> selectedPerks = getSelectedPerks(p);
        GuiItem perkOne = null;
        GuiItem perkTwo = null;
        // note to self, never write code like this ever again
        if(BoxPlugin.instance.getXpManager().getLevel(p) < 50) {
            if(selectedPerks.size() == 0) {
                ItemStack itemOne = new ItemStack(Material.STONE);
                ItemMeta itemOneMeta = itemOne.getItemMeta();
                itemOneMeta.setDisplayName(ChatColor.GRAY + "No perk selected");
                List<String> newItemOneLore = itemOneMeta.getLore() == null ? new ArrayList<>() : itemOneMeta.getLore();
                newItemOneLore.add("");
                newItemOneLore.add(ChatColor.GRAY + "Click to change perk");
                itemOneMeta.setLore(newItemOneLore);
                itemOne.setItemMeta(itemOneMeta);
                perkOne = new GuiItem(itemOne, e -> {
                    clickPerkSlot(e, 1);
                });
                ItemStack itemTwo = new ItemStack(Material.BEDROCK);
                ItemMeta itemTwoMeta = itemTwo.getItemMeta();
                itemTwoMeta.setDisplayName(ChatColor.RED + "Unlocks at level 50");
                itemTwo.setItemMeta(itemTwoMeta);
                perkTwo = new GuiItem(itemTwo, e -> {
                    e.setCancelled(true);
                });
            } else { // 1 or 2 has the same behavior
                AbstractSelectablePerk selected = selectedPerks.get(0);
                ItemStack itemOne = selected.getGuiItem(p).clone();
                ItemMeta itemOneMeta = itemOne.getItemMeta();
                List<String> newItemOneLore = itemOneMeta.getLore() == null ? new ArrayList<>() : itemOneMeta.getLore();
                newItemOneLore.add("");
                newItemOneLore.add(ChatColor.GRAY + "Click to change perk");
                itemOneMeta.setLore(newItemOneLore);
                itemOne.setItemMeta(itemOneMeta);
                perkOne = new GuiItem(itemOne, e -> {
                    clickPerkSlot(e, 1);
                });
                ItemStack itemTwo = new ItemStack(Material.BEDROCK);
                ItemMeta itemTwoMeta = itemTwo.getItemMeta();
                itemTwoMeta.setDisplayName(ChatColor.RED + "Unlocks at level 50");
                itemTwo.setItemMeta(itemTwoMeta);
                perkTwo = new GuiItem(itemTwo, e -> {
                    e.setCancelled(true);
                });
            }
        } else {
            if(selectedPerks.size() == 0) {
                ItemStack itemOne = new ItemStack(Material.STONE);
                ItemMeta itemOneMeta = itemOne.getItemMeta();
                itemOneMeta.setDisplayName(ChatColor.GRAY + "No perk selected");
                List<String> newItemOneLore = itemOneMeta.getLore() == null ? new ArrayList<>() : itemOneMeta.getLore();
                newItemOneLore.add("");
                newItemOneLore.add(ChatColor.GRAY + "Click to change perk");
                itemOneMeta.setLore(newItemOneLore);
                itemOne.setItemMeta(itemOneMeta);
                perkOne = new GuiItem(itemOne, e -> {
                    clickPerkSlot(e, 1);
                });
                ItemStack itemTwo = new ItemStack(Material.STONE);
                ItemMeta itemTwoMeta = itemTwo.getItemMeta();
                itemTwoMeta.setDisplayName(ChatColor.GRAY + "No perk selected");
                List<String> newItemTwoLore = itemTwoMeta.getLore() == null ? new ArrayList<>() : itemTwoMeta.getLore();
                newItemTwoLore.add("");
                newItemTwoLore.add(ChatColor.GRAY + "Click to change perk");
                itemTwoMeta.setLore(newItemTwoLore);
                itemTwo.setItemMeta(itemTwoMeta);
                perkTwo = new GuiItem(itemTwo, e -> {
                    clickPerkSlot(e, 2);
                });
            } else if(selectedPerks.size() == 1) {
                AbstractSelectablePerk selected = selectedPerks.get(0);
                ItemStack itemOne = selected.getGuiItem(p).clone();
                ItemMeta itemOneMeta = itemOne.getItemMeta();
                List<String> newItemOneLore = itemOneMeta.getLore() == null ? new ArrayList<>() : itemOneMeta.getLore();
                newItemOneLore.add("");
                newItemOneLore.add(ChatColor.GRAY + "Click to change perk");
                itemOneMeta.setLore(newItemOneLore);
                itemOne.setItemMeta(itemOneMeta);
                perkOne = new GuiItem(itemOne, e -> {
                    clickPerkSlot(e, 1);
                });
                ItemStack itemTwo = new ItemStack(Material.STONE);
                ItemMeta itemTwoMeta = itemTwo.getItemMeta();
                itemTwoMeta.setDisplayName(ChatColor.GRAY + "No perk selected");
                List<String> newItemTwoLore = itemTwoMeta.getLore() == null ? new ArrayList<>() : itemTwoMeta.getLore();
                newItemTwoLore.add("");
                newItemTwoLore.add(ChatColor.GRAY + "Click to change perk");
                itemTwoMeta.setLore(newItemTwoLore);
                itemTwo.setItemMeta(itemTwoMeta);
                perkTwo = new GuiItem(itemTwo, e -> {
                    clickPerkSlot(e, 2);
                });
            } else if(selectedPerks.size() == 2) {
                AbstractSelectablePerk selected = selectedPerks.get(0);
                ItemStack itemOne = selected.getGuiItem(p).clone();
                ItemMeta itemOneMeta = itemOne.getItemMeta();
                List<String> newItemOneLore = itemOneMeta.getLore() == null ? new ArrayList<>() : itemOneMeta.getLore();
                newItemOneLore.add("");
                newItemOneLore.add(ChatColor.GRAY + "Click to change perk");
                itemOneMeta.setLore(newItemOneLore);
                itemOne.setItemMeta(itemOneMeta);
                perkOne = new GuiItem(itemOne, e -> {
                    clickPerkSlot(e, 1);
                });
                selected = selectedPerks.get(1);
                ItemStack itemTwo = selected.getGuiItem(p).clone();
                ItemMeta itemTwoMeta = itemTwo.getItemMeta();
                List<String> newItemTwoLore = itemTwoMeta.getLore() == null ? new ArrayList<>() : itemTwoMeta.getLore();
                newItemTwoLore.add("");
                newItemTwoLore.add(ChatColor.GRAY + "Click to change perk");
                itemTwoMeta.setLore(newItemTwoLore);
                itemTwo.setItemMeta(itemTwoMeta);
                perkTwo = new GuiItem(itemTwo, e -> {
                    clickPerkSlot(e, 2);
                });
            }
        }

        assert perkOne != null;
        assert perkTwo != null;

        pane.addItem(perkOne, 3,2);
        pane.addItem(perkTwo, 5,2);

        GuiItem megaperk = null;

        if(BoxPlugin.instance.getXpManager().getLevel(p) < 100) {
            ItemStack item = new ItemStack(Material.BEDROCK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Unlocks at level 100");
            item.setItemMeta(meta);
            megaperk = new GuiItem(item, e -> {
                e.setCancelled(true);
            });
        } else {
            if(getSelectedMegaPerk(p) == null) { // unlocked but nothing equipped
                ItemStack item = new ItemStack(Material.STONE);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GRAY + "No Megaperk selected");
                List<String> newLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
                newLore.add("");
                newLore.add(ChatColor.GRAY + "Click to change perk");
                meta.setLore(newLore);
                item.setItemMeta(meta);
                megaperk = new GuiItem(item, e -> {
                    clickMegaPerk(e);
                });
            } else { // unlocked and equipped
                AbstractSelectablePerk selected = getSelectedMegaPerk(p);
                ItemStack item = selected.getGuiItem(p).clone();
                ItemMeta meta = item.getItemMeta();
                List<String> newItemTwoLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
                newItemTwoLore.add("");
                newItemTwoLore.add(ChatColor.GRAY + "Click to change perk");
                meta.setLore(newItemTwoLore);
                item.setItemMeta(meta);
                megaperk = new GuiItem(item, e -> {
                    clickMegaPerk(e);
                });
            }
        }

        // permanent upgrade
        ItemStack upgradeStack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta upgradeMeta = upgradeStack.getItemMeta();
        upgradeMeta.setDisplayName(ChatColor.GREEN + "Permanent Upgrades");
        upgradeMeta.setLore(List.of(
                "",
                ChatColor.GRAY + "Click to edit"
        ));
        upgradeStack.setItemMeta(upgradeMeta);
        GuiItem xpUpgradeItem = new GuiItem(upgradeStack, e -> {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
            this.openUpgradableGui(p);
        });
        //

        pane.addItem(xpUpgradeItem, 4, 5);

        pane.addItem(megaperk, 4, 4);

        gui.addPane(pane);

        gui.copy().show(p);
    }

//    public int getXpBoostLevel(Player p) {
//        return p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "xp_boost_level"), PersistentDataType.INTEGER) ? p.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "xp_boost_level"), PersistentDataType.INTEGER) : 0;
//    }
//
//    public void setXpBoostLevel(Player p, int level) {
//        p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "xp_boost_level"), PersistentDataType.INTEGER, level);
//    }
//
//    public double calculateXPMultiplier(Player p) {
//        return 1.0 + (0.1*getXpBoostLevel(p));
//    }
//
//    public int calculateXPGigaCoinCost(Player p) {
//        return (int) Math.pow(2, getXpBoostLevel(p));
//    }
//
//    public void clickXPUpgrade(InventoryClickEvent e) {
//        e.setCancelled(true);
//        Player p = (Player) e.getWhoClicked();
//        boolean canBuy = buyPermaXP(p);
//        if(canBuy) {
//            setXpBoostLevel(p, getXpBoostLevel(p) + 1);
//            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
//            p.sendMessage(ChatColor.GREEN + "Upgraded XP boost!");
//            p.closeInventory();
//            openMainGui(p);
//        } else {
//            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.25f, 0.5f);
//            p.sendMessage(ChatColor.RED + "You can't afford this!");
//            return;
//        }
//    }

    public void openUpgradableGui(Player p) {
        ChestGui gui = new ChestGui(6, "Upgradable Perks");
        StaticPane pane = new StaticPane(0, 0, 9, 6);
        int j = 0;
        for(int i = 0; i < Perk.values().length; i++) {
            Perk perk = Perk.getByName(Perk.getKeys().get(i));
            ItemStack visualItem = perk.instance.getGuiItem(p).clone();
            ItemMeta meta = visualItem.getItemMeta();
            assert meta != null;
            List<String> newLore = meta.getLore();
            assert newLore != null;
            if(perk.instance instanceof AbstractUpgradablePerk) {
                AbstractUpgradablePerk perkInstance = (AbstractUpgradablePerk) perk.instance;
                GuiItem item = new GuiItem(visualItem, e -> {
                    e.setCancelled(true);
                    perkInstance.upgrade(p);
                });
                if(j >= 3) {
                    pane.addItem(item, j+2, 2);
                } else {
                    pane.addItem(item, j+1, 2);
                }
                j++;
            }
        }
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta meta = cancel.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Go back");
        cancel.setItemMeta(meta);
        pane.addItem(new GuiItem(cancel, e -> {
            e.setCancelled(true);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
            openMainGui(p);
        }), 4, 5);

        gui.addPane(pane);
        gui.copy().show(p);
    }

    public void resetPerks(Player p) {
        for(AbstractSelectablePerk perk : getSelectedPerks(p)) {
            perk.onUnequip(p);
        }
        for(AbstractUpgradablePerk perk : getUpgradablePerks()) {
            perk.onUnequip(p);
            perk.setLevel(p, 0);
        }
        if(getSelectedMegaPerk(p) != null) getSelectedMegaPerk(p).onUnequip(p);
        p.getPersistentDataContainer().remove(new NamespacedKey(BoxPlugin.instance, "selected_megaperk"));
        p.getPersistentDataContainer().remove(new NamespacedKey(BoxPlugin.instance, "selected_perks"));
        for(MegaPerk perk: MegaPerk.values()) {
            if(p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, perk.instance.getKey()), PersistentDataType.INTEGER)) {
                p.getPersistentDataContainer().remove(new NamespacedKey(BoxPlugin.instance, perk.instance.getKey()));
            }
        }
        for(Perk perk : Perk.values()) {
            if(p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, perk.instance.getKey()), PersistentDataType.INTEGER)) {
                p.getPersistentDataContainer().remove(new NamespacedKey(BoxPlugin.instance, perk.instance.getKey()));
            }
        }
//        setXpBoostLevel(p, 0);
    }

    public void openMegaPerkBuyGui(Player p) {
        ChestGui gui = new ChestGui(6, "Available Megaperks");
        StaticPane pane = new StaticPane(0, 0, 9, 6);
        for(int i = 0; i < MegaPerk.getKeys().size(); i++) {
            final int j = i;
            MegaPerk perk = MegaPerk.getByName(MegaPerk.getKeys().get(i));
            ItemStack visualItem = perk.instance.getGuiItem(p).clone();
            ItemMeta meta = visualItem.getItemMeta();
            assert meta != null;
            List<String> newLore = meta.getLore();
            assert newLore != null;
            if(perk.instance instanceof AbstractSelectablePerk) {
                AbstractSelectablePerk perkInstance = (AbstractSelectablePerk) perk.instance;
                if(ownsMegaPerk(p, perk)) { // show "owned"
                    newLore.add("");
                    newLore.add(ChatColor.GREEN + "Owned");
                    newLore.add(ChatColor.GRAY + "Click to equip");
                } else { // show cost
                    newLore.add("");
                    newLore.add(ChatColor.GOLD + "Costs " + perkInstance.getCost() + ChatColor.translateAlternateColorCodes('&', " &x&4&4&4&4&4&4&lG&x&4&9&3&D&3&D&lh&x&4&E&3&6&3&6&la&x&5&2&3&0&3&0&ls&x&5&7&2&9&2&9&lt&x&5&C&2&2&2&2&ll&x&6&1&1&B&1&B&ly &x&6&6&1&4&1&4&lH&x&6&A&0&E&0&E&le&x&6&F&0&7&0&7&lr&x&7&4&0&0&0&0&lb"));
                    newLore.add(ChatColor.GRAY + "Click to buy and equip");
                }
                meta.setLore(newLore);
                visualItem.setItemMeta(meta);
                GuiItem item = new GuiItem(visualItem, e -> {
                    e.setCancelled(true);
                    AbstractSelectablePerk selectedPerk = getSelectedMegaPerk(p);
                    if(selectedPerk != null && selectedPerk.getKey().equals(perk.instance.getKey())) {
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.25f, 0.5f);
                        p.sendMessage(ChatColor.RED + "You already have this megaperk equipped!");
                        return;
                    }
                    if(ownsMegaPerk(p, perk)) {
                        selectedPerk = perkInstance;
                        setSelectedMegaPerk(p, selectedPerk);
                        p.sendMessage(ChatColor.GREEN + "Equipped megaperk!");
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
                        openMainGui(p);
                    } else {
                        // this deducts coins but does not add the perk
                        boolean canBuy = buyMegaPerk(perk, p);
                        if(canBuy) {
                            addMegaPerk(p, perk);
                            selectedPerk = perkInstance;
                            setSelectedMegaPerk(p, selectedPerk);
                            p.sendMessage(ChatColor.GREEN + "Bought megaperk!");
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
                            openMainGui(p);
                        } else {
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.25f, 0.5f);
                            p.sendMessage(ChatColor.RED + "You don't have enough " + ChatColor.translateAlternateColorCodes('&', "&x&4&4&4&4&4&4&lG&x&4&9&3&D&3&D&lh&x&4&E&3&6&3&6&la&x&5&2&3&0&3&0&ls&x&5&7&2&9&2&9&lt&x&5&C&2&2&2&2&ll&x&6&1&1&B&1&B&ly &x&6&6&1&4&1&4&lH&x&6&A&0&E&0&E&le&x&6&F&0&7&0&7&lr&x&7&4&0&0&0&0&lbs"));
                        }
                    }
                });
                if(j >= 2) {
                    pane.addItem(item, j+3, 2);
                } else {
                    pane.addItem(item, j+2, 2);
                }
            }
        }
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "Go back");
        cancel.setItemMeta(cancelMeta);
        pane.addItem(new GuiItem(cancel, e -> {
            e.setCancelled(true);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
            openMainGui(p);
        }), 4, 5);

        ItemStack unequip = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta unequipMeta = unequip.getItemMeta();
        unequipMeta.setDisplayName(ChatColor.RED + "Unequip Megaperk");
        unequip.setItemMeta(unequipMeta);
        pane.addItem(new GuiItem(unequip, e -> {
            e.setCancelled(true);
            setSelectedMegaPerk(p, null);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
            openMainGui(p);
        }), 4, 4);

        gui.addPane(pane);
        gui.copy().show(p);
    }

    public void openPerkBuyGui(Player p, int slot) {
        ChestGui gui = new ChestGui(6, "Available Perks");
        StaticPane pane = new StaticPane(0, 0, 9, 6);
        int j = 0;
        for(int i = 0; i < Perk.values().length; i++) {
            Perk perk = Perk.getByName(Perk.getKeys().get(i));
            ItemStack visualItem = perk.instance.getGuiItem(p).clone();
            ItemMeta meta = visualItem.getItemMeta();
            assert meta != null;
            List<String> newLore = meta.getLore();
            assert newLore != null;
            if(perk.instance instanceof AbstractSelectablePerk) {
                AbstractSelectablePerk perkInstance = (AbstractSelectablePerk) perk.instance;
                if(ownsPerk(p, perk)) { // show "owned"
                    newLore.add("");
                    newLore.add(ChatColor.GREEN + "Owned");
                    newLore.add(ChatColor.GRAY + "Click to equip");
                } else { // show cost
                    newLore.add("");
                    newLore.add(ChatColor.GOLD + "Costs " + perkInstance.getCost() + ChatColor.BOLD + " Giga Coins");
                    newLore.add(ChatColor.GRAY + "Click to buy and equip");
                }
                meta.setLore(newLore);
                visualItem.setItemMeta(meta);
                GuiItem item = new GuiItem(visualItem, e -> {
                    e.setCancelled(true);
                    List<AbstractSelectablePerk> selectedPerks = getSelectedPerks(p);
                    if(selectedPerks.contains(perk.instance)) {
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.25f, 0.5f);
                        p.sendMessage(ChatColor.RED + "You already have this perk equipped!");
                        return;
                    }
                    if(ownsPerk(p, perk)) {
                        if(selectedPerks.size() < slot) {
                            selectedPerks.add(perkInstance);
                        } else {
                            selectedPerks.set(slot-1, perkInstance);
                        }
                        setSelectedPerks(p, selectedPerks);
                        p.sendMessage(ChatColor.GREEN + "Equipped perk!");
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
                        openMainGui(p);
                    } else {
                        // this deducts coins but does not add the perk
                        boolean canBuy = buyPerk(perk, p);
                        if(canBuy) {
                            addPerk(p, perk);
                            if(selectedPerks.size() < slot) {
                                selectedPerks.add(perkInstance);
                            } else {
                                selectedPerks.set(slot-1, perkInstance);
                            }
                            setSelectedPerks(p, selectedPerks);
                            p.sendMessage(ChatColor.GREEN + "Bought perk!");
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
                            openMainGui(p);
                        } else {
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.25f, 0.5f);
                            p.sendMessage(ChatColor.RED + "You don't have enough " + ChatColor.GOLD + "" + ChatColor.BOLD + "Giga Coins");
                        }
                    }
                });
                if(j >= 3) {
                    pane.addItem(item, j+2, 2);
                } else {
                    pane.addItem(item, j+1, 2);
                }
                j++;
            }
        }
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta meta = cancel.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Go back");
        cancel.setItemMeta(meta);
        pane.addItem(new GuiItem(cancel, e -> {
            e.setCancelled(true);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
            openMainGui(p);
        }), 4, 5);

        ItemStack unequip = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta unequipMeta = unequip.getItemMeta();
        unequipMeta.setDisplayName(ChatColor.RED + "Unequip Perk");
        unequip.setItemMeta(unequipMeta);
        pane.addItem(new GuiItem(unequip, e -> {
            e.setCancelled(true);
            if(getSelectedPerks(p).size() != 0) {
                List<AbstractSelectablePerk> currentPerks = getSelectedPerks(p);
                try{
                    currentPerks.remove(slot-1);
                    setSelectedPerks(p, currentPerks);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
                    openMainGui(p);
                } catch (IndexOutOfBoundsException ex) {
                    p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
                    p.sendMessage(ChatColor.RED + "You don't have a perk equipped in this slot!");
                }

            } else {
                p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
                p.sendMessage(ChatColor.RED + "You don't have a perk equipped in this slot!");
            }
        }), 4, 4);

        gui.addPane(pane);
        gui.copy().show(p);
    }

    // callback for clicking perk slot
    public void clickPerkSlot(InventoryClickEvent e, int slot) {
        Player p = (Player) e.getWhoClicked();
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
        e.setCancelled(true);
        openPerkBuyGui(p, slot);
    }

    public void clickMegaPerk(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
        e.setCancelled(true);
        openMegaPerkBuyGui(p);
    }

//    public boolean buyPermaXP(Player p) {
//        int gigaCoinsHeld = 0;
//        int cost = calculateXPGigaCoinCost(p);
//        for(ItemStack item : p.getInventory().getContents()) {
//            if(Util.isGigaCoin(item)) {
//                gigaCoinsHeld += item.getAmount();
//            }
//        }
//        if(gigaCoinsHeld >= cost) {
//            for(ItemStack item : p.getInventory().getContents()) {
//                if(cost == 0) return true;
//                if(Util.isGigaCoin(item)) {
//                    int amount = item.getAmount();
//                    for(int i = 0; i < amount; i++) {
//                        cost--;
//                        item.setAmount(item.getAmount() - 1);
//                        if(cost == 0) return true;
//                    }
//                }
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * Check if a perk can be bought, and buy it if possible
     * @param perk The perk being purchased
     * @param p The player making the purchase
     * @return True if the purchase is valid, false if not (doesn't handle giving user item)
     */
    public boolean buyPerk(Perk perk, Player p) {
        if(perk.instance instanceof AbstractSelectablePerk) {
            AbstractSelectablePerk perkInstance = (AbstractSelectablePerk) perk.instance;
            int gigaCoinsHeld = 0;
            int cost = perkInstance.getCost();
            for(ItemStack item : p.getInventory().getContents()) {
                if(Util.isGigaCoin(item)) {
                    gigaCoinsHeld += item.getAmount();
                }
            }
            if(gigaCoinsHeld >= cost) {
                for(ItemStack item : p.getInventory().getContents()) {
                    if(cost == 0) return true;
                    if(Util.isGigaCoin(item)) {
                        int amount = item.getAmount();
                        for(int i = 0; i < amount; i++) {
                            cost--;
                            item.setAmount(item.getAmount() - 1);
                            if(cost == 0) return true;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        } else { // AbstractUpgradablePerk
            return false;
        }
    }

    public boolean buyMegaPerk(MegaPerk perk, Player p) {
        if(perk.instance instanceof AbstractSelectablePerk) {
            AbstractSelectablePerk perkInstance = (AbstractSelectablePerk) perk.instance;
            int herbsHeld = 0;
            int cost = perkInstance.getCost();
            for(ItemStack item: p.getInventory().getContents()) {
                if(Util.isGhastlyHerb(item)) {
                    herbsHeld += item.getAmount();
                }
            }
            if(herbsHeld >= cost) {
                for(ItemStack item : p.getInventory().getContents()) {
                    if(cost == 0) return true;
                    if(Util.isGhastlyHerb(item)) {
                        int amount = item.getAmount();
                        for(int i = 0; i < amount; i++) {
                            cost--;
                            item.setAmount(item.getAmount() - 1);
                            if(cost == 0) return true;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        } else { // AbstractUpgradablePerk
            return false;
        }
    }

    public Perk getPerkBySlot(Player p, int slot) {
        return getPurchasedPerks(p).get(slot - 1);
    }

    public boolean ownsPerk(Player p, Perk perk) {
        return getPurchasedPerks(p).contains(perk);
    }

    public boolean ownsMegaPerk(Player p, MegaPerk perk) {
        return getPurchasedMegaPerks(p).contains(perk);
    }

    public void addPerk(Player p, Perk perk) {
        p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, perk.instance.getKey()), PersistentDataType.INTEGER, 1);
    }

    public void addMegaPerk(Player p, MegaPerk perk) {
        p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, perk.instance.getKey()), PersistentDataType.INTEGER, 1);
    }


    public void setSelectedPerks(Player p, List<AbstractSelectablePerk> perks) {
        List<AbstractSelectablePerk> prevPerks = getSelectedPerks(p);
        if(perks.size() == 0) {
            p.getPersistentDataContainer().remove(new NamespacedKey(BoxPlugin.instance, "selected_perks"));
        } else {
            p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "selected_perks"), PersistentDataType.STRING, String.join("\n", perks.stream().map(pe -> pe.getKey()).collect(Collectors.toList())));
        }
        for(AbstractSelectablePerk perk : perks) {
            perk.onEquip(p);
        }
        for(AbstractSelectablePerk perk : prevPerks) { // todo test
            if(!perks.contains(perk)) {
                perk.onUnequip(p);
            }
        }
    }

    public void setSelectedMegaPerk(Player p, AbstractSelectablePerk perk) {
        AbstractSelectablePerk prevPerk = getSelectedMegaPerk(p);
        if(perk != null) {
            perk.onEquip(p);
            p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "selected_megaperk"), PersistentDataType.STRING, perk.getKey());
        } else {
            p.getPersistentDataContainer().remove(new NamespacedKey(BoxPlugin.instance, "selected_megaperk"));
        }
        if(prevPerk != null) prevPerk.onUnequip(p);
    }


    public List<Perk> getPurchasedPerks(Player p) {
        List<Perk> perks = new ArrayList<Perk>();
        for(String key : Perk.getKeys()) {
            if(p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, key), PersistentDataType.INTEGER) && p.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, key), PersistentDataType.INTEGER) == 1) {
                perks.add(Perk.getByName(key));
            }
        }
        return perks;
    }

    public List<MegaPerk> getPurchasedMegaPerks(Player p) {
        List<MegaPerk> perks = new ArrayList<MegaPerk>();
        for(String key : MegaPerk.getKeys()) {
            if(p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, key), PersistentDataType.INTEGER) && p.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, key), PersistentDataType.INTEGER) == 1) {
                perks.add(MegaPerk.getByName(key));
            }
        }
        return perks;
    }

    // stored as list of perks separated by \n, inside one string "selected_perks"
    public ArrayList<AbstractSelectablePerk> getSelectedPerks(Player p) {
        if(!p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "selected_perks"), PersistentDataType.STRING)) {
            return new ArrayList<>();
        } else {
            ArrayList<AbstractSelectablePerk> l = new ArrayList<>();
            for(String key : Arrays.stream(p.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "selected_perks"), PersistentDataType.STRING).split("\n")).collect(Collectors.toList())) {
                if(Perk.getByName(key).instance instanceof AbstractSelectablePerk) {
                    l.add((AbstractSelectablePerk) Perk.getByName(key).instance);
                }
            }
            return l;
        }
    }

    public AbstractSelectablePerk getSelectedMegaPerk(Player p) {
        if(!p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "selected_megaperk"), PersistentDataType.STRING)) {
            return null;
        } else {
            return (AbstractSelectablePerk) MegaPerk.getByName(p.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "selected_megaperk"), PersistentDataType.STRING)).instance;
        }
    }

    public List<AbstractSelectablePerk> getSelectablePerks() {
        List<AbstractSelectablePerk> selectablePerks = new ArrayList<>();
        for(Perk perk : Perk.values()) {
            if(perk.instance instanceof AbstractSelectablePerk) {
                selectablePerks.add((AbstractSelectablePerk) perk.instance);
            }
        }
        return selectablePerks;
    }

    public List<AbstractUpgradablePerk> getUpgradablePerks() {
        List<AbstractUpgradablePerk> upgradablePerks = new ArrayList<>();
        for(Perk perk : Perk.values()) {
            if(perk.instance instanceof AbstractUpgradablePerk) {
                upgradablePerks.add((AbstractUpgradablePerk) perk.instance);
            }
        }
        return upgradablePerks;
    }

}
