package com.github.syldium.hideseek.utils;

import com.github.syldium.hideseek.HSPlugin;
import com.github.syldium.hideseek.manager.LocaleManager;
import com.github.syldium.hideseek.players.Role;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class CustomItem {

    public static ItemStack sword() {
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta meta = Objects.requireNonNull(sword.getItemMeta());
        meta.addEnchant(Enchantment.DAMAGE_ALL, 4, false);
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        meta.setDisplayName(ChatColor.RESET + LocaleManager.ITEM_SWORD);
        sword.setItemMeta(meta);
        return sword;
    }

    public static ItemStack bow(boolean trident) {
        ItemStack bow;
        if (trident) {
            bow = new ItemStack(Material.TRIDENT);
            bow.addEnchantment(Enchantment.LOYALTY, 3);
        } else {
            bow = new ItemStack(Material.BOW);
            bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
            bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        }
        ItemMeta meta = Objects.requireNonNull(bow.getItemMeta());
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        meta.setDisplayName(ChatColor.RESET + LocaleManager.ITEM_BOW_TRIDENT);
        bow.setItemMeta(meta);
        return bow;
    }

    public static ItemStack stick() {
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta meta = Objects.requireNonNull(stick.getItemMeta());
        meta.addEnchant(Enchantment.DAMAGE_ALL, 3, false);
        meta.addEnchant(Enchantment.KNOCKBACK, 2, false);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        meta.setDisplayName(ChatColor.RESET + LocaleManager.ITEM_STICK);
        stick.setItemMeta(meta);
        return stick;
    }

    public static ItemStack blaze(boolean usable) {
        Material m;
        String reloading;
        if (usable) {
            m = Material.BLAZE_ROD;
            reloading = "";
        } else {
            m = Material.BLAZE_POWDER;
            reloading = ChatColor.GRAY + " - " + ChatColor.RED + LocaleManager.ITEM_RELOADING;
        }

        ItemStack blaze = new ItemStack(m);
        ItemMeta meta = Objects.requireNonNull(blaze.getItemMeta());
        meta.setDisplayName(ChatColor.YELLOW + LocaleManager.ITEM_BLAZE + reloading);
        blaze.setItemMeta(meta);
        return blaze;
    }

    public static void blazeCountdown(HSPlugin plugin, ItemStack blaze, Player player) {
        blaze.setType(Material.BLAZE_POWDER);
        ItemMeta meta = Objects.requireNonNull(blaze.getItemMeta());
        meta.setDisplayName(ChatColor.YELLOW + LocaleManager.ITEM_BLAZE + ChatColor.GRAY + " - " + ChatColor.RED + LocaleManager.ITEM_RELOADING);
        player.setCooldown(Material.BLAZE_POWDER, 400);
        blaze.setItemMeta(meta);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, SoundCategory.PLAYERS, 1, 1);
            blaze.setType(Material.BLAZE_ROD);
            ItemMeta i = blaze.getItemMeta();
            i.setDisplayName(ChatColor.YELLOW + LocaleManager.ITEM_BLAZE);
            blaze.setItemMeta(i);
        }, 400L);
    }

    public static void give(HSPlugin plugin, Player player, Role role) {
        if (role.equals(Role.HUNTER)) {
            player.getInventory().addItem(sword());
            player.getInventory().addItem(bow(plugin.getConfig().getBoolean("give-trident")));
            player.getInventory().addItem(new ItemStack(Material.ARROW));

            player.getInventory().addItem(blaze(true));
        } else if (role.equals(Role.THIEVE)) {
            player.getInventory().addItem(stick());
        }
    }
}
