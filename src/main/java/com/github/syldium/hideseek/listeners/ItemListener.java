package com.github.syldium.hideseek.listeners;

import com.github.syldium.hideseek.HSPlugin;
import com.github.syldium.hideseek.utils.CustomItem;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

    private final HSPlugin plugin;

    public ItemListener(HSPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (plugin.getPlayerManager().isInGame(event.getPlayer())) {
                ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
                useSpecialItem(item, event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (plugin.getPlayerManager().isInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    public void useSpecialItem(ItemStack item, Player user) {
        if (item.getType().equals(Material.BLAZE_ROD)) {
            user.getWorld().playEffect(user.getLocation(), Effect.BOW_FIRE, 5);
            user.playSound(user.getLocation(), Sound.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 1,1);
            plugin.getGameManager().highlight();
            CustomItem.blazeCountdown(plugin, item, user);
        } else if (item.getType().equals(Material.BLAZE_POWDER)) {
            user.playSound(user.getLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.2f,1);
        }
    }
}
