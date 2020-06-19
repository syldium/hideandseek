package com.github.syldium.hideseek.listeners;

import com.github.syldium.hideseek.HSPlugin;
import com.github.syldium.hideseek.manager.LocaleManager;
import com.github.syldium.hideseek.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final HSPlugin plugin;
    private boolean abort = false;

    public PlayerListener(HSPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String msg = "";
        if (plugin.getPlayerManager().isInGame(uuid)) {
            plugin.getPlayerManager().getProfile(event.getPlayer()).getScoreBoard().apply(event.getPlayer());
            boolean previousState = this.abort;
            if (this.abort) {
                this.abort = plugin.getGameManager().abortNeeded();
            }
            if (previousState != this.abort) {
                msg = LocaleManager.GAME_RESUME;
                TextUtils.sendConsoleMessage(msg);
            }
            event.setJoinMessage("");
            String finalMsg = msg;
            plugin.getPlayerManager().forEach(profile -> {
                Player player = Bukkit.getPlayer(profile.getUuid());
                TextUtils.sendVerboseMessage(player, LocaleManager.RECONNECT
                        .replace("%player%", event.getPlayer().getName()));
                if (!finalMsg.equals("")) {
                    TextUtils.sendVerboseMessage(player, finalMsg);
                }
            });
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (plugin.getPlayerManager().isInGame(uuid)) {
            if (!plugin.getGameManager().isStarted()) {
                plugin.getPlayerManager().leave(uuid);
                return;
            }
            event.setQuitMessage("");
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                this.abort = plugin.getGameManager().abortNeeded();
                plugin.getPlayerManager().forEach(profile -> {
                    Player player = Bukkit.getPlayer(profile.getUuid());
                    TextUtils.sendVerboseMessage(player, LocaleManager.DISCONNECT
                            .replace("%player%", event.getPlayer().getName()));
                    if (this.abort) {
                        TextUtils.sendVerboseMessage(player, LocaleManager.GAME_PAUSE);
                    }
                });
            }, 1L);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (plugin.getGameManager().isStarted() && this.abort) {
                    plugin.getGameManager().gameStop();
                }
            }, 400L);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (plugin.getPlayerManager().isInGame(uuid)) {
            plugin.getPlayerManager().getProfile(uuid).initStanding();
        }
    }
}
