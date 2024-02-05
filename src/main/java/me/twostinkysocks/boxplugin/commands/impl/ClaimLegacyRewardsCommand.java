package me.twostinkysocks.boxplugin.commands.impl;

import me.twostinkysocks.boxplugin.BoxPlugin;
import me.twostinkysocks.boxplugin.MessageConstants;
import me.twostinkysocks.boxplugin.commands.api.SimpleCommandHandler;
import me.twostinkysocks.boxplugin.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ClaimLegacyRewardsCommand extends SimpleCommandHandler {

    private static final NamespacedKey LEGACY_LEVELS_COMPENSATED = new NamespacedKey(BoxPlugin.instance, "legacylevelscompensated");

    public ClaimLegacyRewardsCommand() {
        super("aetherconquest.command.claimlegacyrewards");
        addExtraPermission("boxplugin.claimlegacyrewards");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(MessageConstants.PLAYER_REQUIRED);
            return true;
        }

        if(!hasPermission(player)) {
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        if(player.getPersistentDataContainer().has(LEGACY_LEVELS_COMPENSATED, PersistentDataType.INTEGER)) {
            player.sendMessage(ChatColor.RED + "You have no rewards to claim!");
        } else {
            int total = getPlugin().getXpManager().getCumulativeLevelUpReward(getPlugin().getXpManager().getLevel(player));
            HashMap<Integer, ItemStack> toDrop = player.getInventory().addItem(Util.itemArray(total, Util::gigaCoin));
            toDrop.forEach((index, item) -> {
                Item entity = (Item) player.getWorld().spawnEntity(player.getLocation(), EntityType.DROPPED_ITEM);
                entity.setItemStack(item);
            });
            player.sendMessage(ChatColor.GREEN + "Claimed " + total + " giga coins!");
            player.getPersistentDataContainer().set(new NamespacedKey(BoxPlugin.instance, "legacylevelscompensated"), PersistentDataType.INTEGER, 1);
        }
        return true;
    }
}
