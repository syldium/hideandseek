package com.github.syldium.hideseek.listeners;

import com.github.syldium.hideseek.HSPlugin;
import com.github.syldium.hideseek.players.PlayerProfile;
import com.github.syldium.hideseek.players.Role;
import com.github.syldium.hideseek.utils.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class DamageListener implements Listener {

    private final HSPlugin plugin;

    public DamageListener(HSPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && plugin.getPlayerManager().isInGame(event.getEntity())) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                double reducer = plugin.getConfig().getInt("fall-reducer");
                if (Math.abs(reducer) < 1) {
                    event.setCancelled(true);
                } else {
                    event.setDamage(event.getDamage() / reducer);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if (plugin.getPlayerManager().isInGame(event.getEntity()) && plugin.getPlayerManager().isInGame(event.getDamager())) {
                if (plugin.getPlayerManager().getProfile(event.getDamager()).getRole().equals(plugin.getPlayerManager().getProfile(event.getEntity()).getRole())) {
                    event.setCancelled(true); // Friendly Fire
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        if (plugin.getPlayerManager().isInGame(event.getEntity())) {
            PlayerProfile profile = plugin.getPlayerManager().getProfile(uuid);
            if (profile.getRole().equals(Role.THIEVE) && plugin.getConfig().getBoolean("become-hunter")) {
                player.teleport(player.getLocation());
                player.getInventory().clear();
                profile.setRole(Role.HUNTER);
                CustomItem.give(plugin, player, profile.getRole());
            } else {
                event.getEntity().setGameMode(GameMode.SPECTATOR);
                profile.setAlive(false);
            }
            profile.setAlive(false);
            if (event.getEntity().getKiller() != null) {
                UUID killerUuid = event.getEntity().getKiller().getUniqueId();
                if (plugin.getPlayerManager().isInGame(killerUuid)) {
                    plugin.getPlayerManager().getProfile(killerUuid).addKills(1);
                }
            }
            plugin.getGameManager().countPlayers();
            plugin.getGameManager().checkGame();
        }
    }
}
