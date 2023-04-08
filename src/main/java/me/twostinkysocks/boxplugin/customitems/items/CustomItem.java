package me.twostinkysocks.boxplugin.customitems.items;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.customitems.CustomItemsMain;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class CustomItem {

    private String name;
    private String[] lore;
    private Material material;
    private ItemStack item;
    private CustomItemsMain plugin;
    private String itemId;
    private Consumer<Player> leave;
    private Consumer<Player> join;
    private Consumer<EntityInteractEvent> entityInteract;
    private BiConsumer<PlayerInteractEvent, Action> click;
    private Consumer<EntityTargetEvent> entityTarget;

    public CustomItem(String name, String itemId, Material material, CustomItemsMain plugin, String...lore) {
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.plugin = plugin;
        this.itemId = itemId;
        this.click = (e, a) -> {};
        this.leave = p -> {};
        this.join = p -> {};
        this.entityTarget = e -> {};
        this.entityInteract = e -> {};
        this.item = new ItemStack(material);

        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(List.of(lore));
        im.setUnbreakable(true);

        im.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "ITEM_ID"), PersistentDataType.STRING, itemId);

        item.setItemMeta(im);
    }

    public ItemStack getItemStack() {
        return item;
    }

    public String getItemId() {
        return itemId;
    }

    public void setClick(BiConsumer<PlayerInteractEvent, Action> toRun) {
        this.click = toRun;
    }

    public void setEntityTarget(Consumer<EntityTargetEvent> toRun) {
        this.entityTarget = toRun;
    }

    public void setLeave(Consumer<Player> toRun) {
        this.leave = toRun;
    }

    public void setJoin(Consumer<Player> toRun) {
        this.join = toRun;
    }

    public void setEntityInteract(Consumer<EntityInteractEvent> e) {
        this.entityInteract = e;
    }

    public Consumer<EntityInteractEvent> getEntityInteract() {
        return entityInteract;
    }

    public BiConsumer<PlayerInteractEvent, Action> getClick() {
        return click;
    }

    public Consumer<EntityTargetEvent> getEntityTarget() {
        return entityTarget;
    }

    public Consumer<Player> getLeave() {
        return leave;
    }

    public Consumer<Player> getJoin() {
        return join;
    }

    public String getName() {
        return name;
    }
}
