package me.twostinkysocks.boxplugin.manager;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.Util;
import me.twostinkysocks.boxplugin.perks.*;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PerksManager {

    // TODO: add more text to stuff to describe what's happening
    // TODO: megaperk

    private CraftEntity perkNPC;


    // perk_speed, etc, are all integers, 0 or 1

    // selected_perks is a string of all perk keys, separated by \n

    public enum Perk {
        SPEED(new PerkSpeed()),
        STRENGTH(new PerkStrength()),
        WATER_BREATHING(new PerkWaterBreathing()),
        HASTE(new PerkHaste()),
        ROCKETS(new PerkRockets());

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

    public PerksManager() {
        for(Entity e : Bukkit.getWorld("Xanatos").getEntities()) {
            if(e.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "perk_npc"), PersistentDataType.INTEGER) && e.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "perk_npc"), PersistentDataType.INTEGER) == 1) {
                e.remove();
            }
        }
    }

    public void openMainGui(Player p) {
        ChestGui gui = new ChestGui(6, "Equipped Perks");
        StaticPane pane = new StaticPane(0, 0, 9, 6);
        List<Perk> selectedPerks = getSelectedPerks(p);
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
                Perk selected = selectedPerks.get(0);
                ItemStack itemOne = selected.instance.getGuiItem().clone();
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
                Perk selected = selectedPerks.get(0);
                ItemStack itemOne = selected.instance.getGuiItem().clone();
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
                Perk selected = selectedPerks.get(0);
                ItemStack itemOne = selected.instance.getGuiItem().clone();
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
                ItemStack itemTwo = selected.instance.getGuiItem().clone();
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

        gui.addPane(pane);

        gui.copy().show(p);
    }

    public void resetPerks(Player p) {
        for(Perk perk : getSelectedPerks(p)) {
            perk.instance.onUnequip(p);
        }
        p.getPersistentDataContainer().remove(new NamespacedKey(BoxPlugin.instance, "selected_perks"));
        for(Perk perk : Perk.values()) {
            if(p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, perk.instance.getKey()), PersistentDataType.INTEGER)) {
                p.getPersistentDataContainer().remove(new NamespacedKey(BoxPlugin.instance, perk.instance.getKey()));
            }
        }
    }

    public void openPerkBuyGui(Player p, int slot) {
        ChestGui gui = new ChestGui(6, "Available Perks");
        StaticPane pane = new StaticPane(0, 0, 9, 6);
        for(int i = 0; i < Perk.getKeys().size(); i++) {
            final int j = i;
            Perk perk = Perk.getByName(Perk.getKeys().get(i));
            ItemStack visualItem = perk.instance.getGuiItem().clone();
            ItemMeta meta = visualItem.getItemMeta();
            assert meta != null;
            List<String> newLore = meta.getLore();
            assert newLore != null;
            if(ownsPerk(p, perk)) { // show "owned"
                newLore.add("");
                newLore.add(ChatColor.GREEN + "Owned");
                newLore.add(ChatColor.GRAY + "Click to equip");
            } else { // show cost
                newLore.add("");
                newLore.add(ChatColor.GOLD + "Costs " + perk.instance.getCost() + ChatColor.BOLD + " Giga Coins");
                newLore.add(ChatColor.GRAY + "Click to buy and equip");
            }
            meta.setLore(newLore);
            visualItem.setItemMeta(meta);
            GuiItem item = new GuiItem(visualItem, e -> {
                e.setCancelled(true);
                List<Perk> selectedPerks = getSelectedPerks(p);
                if(selectedPerks.contains(perk)) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.25f, 0.5f);
                    p.sendMessage(ChatColor.RED + "You already have this perk equipped!");
                    return;
                }
               if(ownsPerk(p, perk)) {
                   if(selectedPerks.size() < slot) {
                       selectedPerks.add(perk);
                   } else {
                       selectedPerks.set(slot-1, perk);
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
                           selectedPerks.add(perk);
                       } else {
                           selectedPerks.set(slot-1, perk);
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
            pane.addItem(item, j+2, 2);

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

    // callback for clicking perk slot
    public void clickPerkSlot(InventoryClickEvent e, int slot) {
        Player p = (Player) e.getWhoClicked();
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
        e.setCancelled(true);
        openPerkBuyGui(p, slot);
    }

    /**
     * Check if a perk can be bought, and buy it if possible
     * @param perk The perk being purchased
     * @param p The player making the purchase
     * @return True if the purchase is valid, false if not (doesn't handle giving user item)
     */
    public boolean buyPerk(Perk perk, Player p) {
        int gigaCoinsHeld = 0;
        int cost = perk.instance.getCost();
        for(ItemStack item : p.getInventory().getContents()) {
            if(Util.isGigaCoin(item)) {
                gigaCoinsHeld += item.getAmount();
            }
        }
        if(gigaCoinsHeld >= cost) {
            for(ItemStack item : p.getInventory().getContents()) {
                if(cost == 0) return true;
                if(Util.isGigaCoin(item)) {
                    for(int i = 0; i < item.getAmount(); i++) {
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
    }

    public Perk getPerkBySlot(Player p, int slot) {
        return getPurchasedPerks(p).get(slot - 1);
    }

    public boolean ownsPerk(Player p, Perk perk) {
        return getPurchasedPerks(p).contains(perk);
    }

    public void addPerk(Player p, Perk perk) {
        p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, perk.instance.getKey()), PersistentDataType.INTEGER, 1);
    }

    public void setSelectedPerks(Player p, List<Perk> perks) {
        if(perks.size() == 0) {
            p.getPersistentDataContainer().remove(new NamespacedKey(BoxPlugin.instance, "selected_perks"));
        } else {
            p.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "selected_perks"), PersistentDataType.STRING, String.join("\n", perks.stream().map(pe -> pe.instance.getKey()).collect(Collectors.toList())));
        }
        for(Perk perk : perks) {
            perk.instance.onEquip(p);
        }
        ArrayList<Perk> allPerks = new ArrayList<Perk>(List.of(Perk.values()));
        allPerks.removeAll(perks);
        for(Perk perk : allPerks) { // todo test
            perk.instance.onUnequip(p);
        }
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

    // stored as list of perks separated by \n, inside one string "selected_perks"
    public ArrayList<Perk> getSelectedPerks(Player p) {
        if(!p.getPersistentDataContainer().has(new NamespacedKey(BoxPlugin.instance, "selected_perks"), PersistentDataType.STRING)) {
            return new ArrayList<>();
        } else {
            List<Perk> l = Arrays.stream(p.getPersistentDataContainer().get(new NamespacedKey(BoxPlugin.instance, "selected_perks"), PersistentDataType.STRING).split("\n")).map(s -> Perk.getByName(s)).collect(Collectors.toList());
            ArrayList<Perk> newList = new ArrayList<Perk>(l);
            return newList;
        }
    }

    public void respawnPerkNPC() throws CommandSyntaxException {
        double x = BoxPlugin.instance.getConfig().getDouble("perknpc.x");
        double y = BoxPlugin.instance.getConfig().getDouble("perknpc.y");
        double z = BoxPlugin.instance.getConfig().getDouble("perknpc.z");
        if(perkNPC != null) perkNPC.remove();
        String typeString = BoxPlugin.instance.getConfig().getString("perknpc.type");
        String nbtString = BoxPlugin.instance.getConfig().getString("perknpc.nbt");
        NBTTagCompound nbt = MojangsonParser.a(nbtString);
        EntityType type = EntityType.fromName(typeString);
        Location loc = new Location(Bukkit.getWorld("Xanatos"), x, y, z, 180f, 0f);
        perkNPC = (CraftEntity) Bukkit.getWorld("Xanatos").spawnEntity(loc, type);
        perkNPC.getHandle().g(nbt);
        perkNPC.teleport(loc);
        perkNPC.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "perk_npc"), PersistentDataType.INTEGER, 1);
    }

}
