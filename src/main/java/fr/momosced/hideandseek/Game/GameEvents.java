package fr.momosced.hideandseek.Game;

import fr.momosced.hideandseek.LocaleManager;
import fr.momosced.hideandseek.Utils.Config;
import fr.momosced.hideandseek.Utils.CustomItem;
import fr.momosced.hideandseek.Main;
import fr.momosced.hideandseek.PlayerData.PlayerManager;
import fr.momosced.hideandseek.PlayerData.Role;
import fr.momosced.hideandseek.Utils.TextUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class GameEvents implements Listener {

    private Main plugin = Main.getInstance();
    public boolean abort = false;

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && plugin.playermanager.containsKey(event.getEntity().getUniqueId())) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                double reducer = Config.FALL_REDUCER;
                if (reducer==0)
                    event.setCancelled(true);
                else if (reducer > 1)
                    event.setDamage(event.getDamage()/reducer);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (damaged instanceof Player && damager instanceof Player) {
            UUID udamaged = damaged.getUniqueId();
            UUID udamager = damager.getUniqueId();
            if (plugin.playermanager.containsKey(udamaged) && plugin.playermanager.containsKey(udamager)) {
                if (plugin.playermanager.get(udamaged).getRole().equals(plugin.playermanager.get(udamager).getRole())) {
                    event.setCancelled(true); // Friendly Fire
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        if (plugin.playermanager.containsKey(uuid)) {
            PlayerManager pm = plugin.playermanager.get(uuid);
            if (pm.getRole().equals(Role.THIEVE) && Config.BECOME_HUNTER) {
                player.teleport(player.getLocation());
                player.getInventory().clear();
                pm.setRole(Role.HUNTER);
                CustomItem.give(player, pm.getRole());
            } else {
                event.getEntity().setGameMode(GameMode.SPECTATOR);
                pm.setAlive(false);
            }
            plugin.playermanager.get(uuid).setAlive(false);
            if (event.getEntity().getKiller() != null) {
                UUID killeruuid = event.getEntity().getKiller().getUniqueId();
                if (plugin.playermanager.containsKey(killeruuid)) {
                    plugin.playermanager.get(killeruuid).addKills(1);
                }
            }
            plugin.gamemanager.countPlayers();
            plugin.gamemanager.checkGame();
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (plugin.playermanager.containsKey(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (plugin.playermanager.containsKey(event.getPlayer().getUniqueId())) {
                ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
                if (item != null) useSpecialItem(item, event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        UUID puuid = event.getPlayer().getUniqueId();
        if (plugin.playermanager.containsKey(puuid)) {
            plugin.playermanager.get(puuid).initStanding();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID puuid = event.getPlayer().getUniqueId();
        String msg = "";
        if (plugin.playermanager.containsKey(puuid)) {
            boolean previousState = this.abort;
            if (this.abort) {
                this.abort = plugin.gamemanager.abortNeeded();
            }
            if (previousState != this.abort) {
                msg = LocaleManager.GAME_RESUME;
                TextUtils.sendConsoleMessage(msg);
            }
            event.setJoinMessage("");
            for (Map.Entry<UUID, PlayerManager> entry : plugin.playermanager.entrySet()) {
                UUID uuid = entry.getKey();
                PlayerManager pm = entry.getValue();
                Player player = Bukkit.getPlayer(uuid);
                TextUtils.sendVerboseMessage(player, LocaleManager.RECONNECT
                        .replace("%player%", event.getPlayer().getName()));
                if (msg != "") {
                    TextUtils.sendVerboseMessage(player, msg);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID puuid = event.getPlayer().getUniqueId();
        if (plugin.playermanager.containsKey(puuid)) {
            if (! plugin.gamemanager.isStarted) {
                plugin.playermanager.remove(puuid);
                return;
            }
            event.setQuitMessage("");
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.gameevents.abort = plugin.gamemanager.abortNeeded();
                    plugin.playermanager.forEach((uuid, pm) -> {
                        Player player = Bukkit.getPlayer(uuid);
                        TextUtils.sendVerboseMessage(player, LocaleManager.DISCONNECT
                                .replace("%player%", event.getPlayer().getName()));
                        if (plugin.gameevents.abort) {
                            TextUtils.sendVerboseMessage(player, LocaleManager.GAME_PAUSE);
                        }
                    });
                }
            }.runTaskLater(plugin, 1l);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (plugin.gamemanager.isStarted && plugin.gameevents.abort) {
                        plugin.gamemanager.gameStop();
                    }
                }
            }.runTaskLater(plugin, 400l);
        }
    }

    public void useSpecialItem(ItemStack item, Player user) {
        if (item.getType().equals(Material.BLAZE_ROD)) {
            user.getWorld().playEffect(user.getLocation(), Effect.BOW_FIRE, 5);
            user.playSound(user.getLocation(), Sound.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 1,1);
            plugin.gamemanager.highlight();
            CustomItem.blazeCountdown(plugin, item, user);
        } else if (item.getType().equals(Material.BLAZE_POWDER)) {
            user.playSound(user.getLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.2f,1);
        }
    }
}
