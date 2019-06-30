package fr.momosced.hideandseek.Utils;

import fr.momosced.hideandseek.LocaleManager;
import fr.momosced.hideandseek.Main;
import fr.momosced.hideandseek.PlayerData.Role;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomItem {

    public static ItemStack sword() {
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta swordmeta = sword.getItemMeta();
        swordmeta.addEnchant(Enchantment.DAMAGE_ALL, 4, false);
        swordmeta.addEnchant(Enchantment.DURABILITY, 10, true);
        swordmeta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        swordmeta.setDisplayName(LocaleManager.ITEM_SWORD);
        sword.setItemMeta(swordmeta);
        return sword;
    }

    public static ItemStack bow() {
        ItemStack bow;
        if (Config.GIVE_TRIDENT) {
            bow = new ItemStack(Material.TRIDENT);
            bow.addEnchantment(Enchantment.LOYALTY, 3);
        } else {
            bow = new ItemStack(Material.BOW);
            bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
            bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        }
        ItemMeta bowmeta = bow.getItemMeta();
        bowmeta.addEnchant(Enchantment.DURABILITY, 10, true);
        bowmeta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        bowmeta.setDisplayName(LocaleManager.ITEM_BOW_TRIDENT);
        bow.setItemMeta(bowmeta);
        return bow;
    }

    public static ItemStack stick() {
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta stickmeta = stick.getItemMeta();
        stickmeta.addEnchant(Enchantment.DAMAGE_ALL, 3, false);
        stickmeta.addEnchant(Enchantment.KNOCKBACK, 2, false);
        stickmeta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        stickmeta.setDisplayName(LocaleManager.ITEM_STICK);
        stick.setItemMeta(stickmeta);
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
        ItemMeta blazemeta = blaze.getItemMeta();
        blazemeta.setDisplayName(ChatColor.YELLOW + LocaleManager.ITEM_BLAZE + reloading);
        blaze.setItemMeta(blazemeta);
        return blaze;
    }

    public static void blazeCountdown(Main plugin, ItemStack blaze, Player player) {
        blaze.setType(Material.BLAZE_POWDER);
        ItemMeta im = blaze.getItemMeta();
        im.setDisplayName(ChatColor.YELLOW + LocaleManager.ITEM_BLAZE + ChatColor.GRAY + " - " + ChatColor.RED + LocaleManager.ITEM_RELOADING);
        blaze.setItemMeta(im);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, SoundCategory.PLAYERS, 1, 1);
                blaze.setType(Material.BLAZE_ROD);
                ItemMeta i = blaze.getItemMeta();
                i.setDisplayName(ChatColor.YELLOW + LocaleManager.ITEM_BLAZE);
                blaze.setItemMeta(i);

            }
        }.runTaskLater(plugin, 400l);
    }

    public static void give(Player player, Role role) {
        if (role.equals(Role.HUNTER)) {
            player.getInventory().addItem(sword());
            player.getInventory().addItem(bow());
            player.getInventory().addItem(new ItemStack(Material.ARROW));

            player.getInventory().addItem(blaze(true));
        } else if (role.equals(Role.THIEVE)) {
            player.getInventory().addItem(stick());
        }
    }
}
