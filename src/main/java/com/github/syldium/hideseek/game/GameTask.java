package com.github.syldium.hideseek.game;

import com.github.syldium.hideseek.HSPlugin;
import com.github.syldium.hideseek.manager.LocaleManager;
import com.github.syldium.hideseek.players.Role;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {

    private final HSPlugin plugin;

    private GamePhase gamePhase = GamePhase.COUNTDOWN;
    private int countdown;
    private int hideTimer;
    private int timeLeft;

    public GameTask(HSPlugin plugin) {
        this.plugin = plugin;
        this.countdown = plugin.getConfig().getInt("countdown");
        this.hideTimer = plugin.getConfig().getInt("hide-timer");
        this.timeLeft = plugin.getConfig().getInt("game-duration");
    }

    @Override
    public void run() {
        int nametagHide = plugin.getConfig().getInt("nametag-hide");
        switch (gamePhase) {
            case COUNTDOWN:
                countdown--;
                plugin.getPlayerManager().forEach(profile -> {
                    Player player = Bukkit.getPlayer(profile.getUuid());
                    if (player == null) {
                        return;
                    }
                    player.setGameMode(GameMode.ADVENTURE);
                    profile.getScoreBoard().updateTeams(plugin.getPlayerManager(), nametagHide);
                    profile.getScoreBoard().refresh(player, profile, gamePhase, countdown);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new ComponentBuilder(LocaleManager.ACTIONBAR_GAME_START_IN.replace("%time%", "" + countdown))
                                    .color(ChatColor.AQUA).create()
                    );
                });
                if (countdown < 1) {
                    plugin.getGameManager().countdownEnd();
                    gamePhase = GamePhase.HIDE;
                }
                break;
            case HIDE:
                hideTimer--;
                plugin.getPlayerManager().forEach(profile -> {
                    Player player = Bukkit.getPlayer(profile.getUuid());
                    if (player == null) {
                        return;
                    }
                    if (profile.getRole() == Role.THIEVE) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new ComponentBuilder(LocaleManager.ACTIONBAR_HUNTER_COMING_IN.replace("%time%", "" + hideTimer))
                                        .color(ChatColor.DARK_RED).create()
                        );
                    } else if (profile.getRole() == Role.HUNTER) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new ComponentBuilder(LocaleManager.ACTIONBAR_TELEPORT_IN.replace("%time%", "" + hideTimer))
                                        .color(ChatColor.BLUE).create()
                        );
                    }
                    profile.getScoreBoard().updateTeams(plugin.getPlayerManager(), nametagHide);
                    profile.getScoreBoard().refresh(player, profile, gamePhase, hideTimer);
                    if (profile.getStanding() < nametagHide + 2) {
                        profile.addStanding();
                    }
                });
                if (hideTimer <= 1) {
                    plugin.getGameManager().hideEnd();
                    gamePhase = GamePhase.SEEK;
                }
                break;
            case SEEK:
                timeLeft--;

                plugin.getPlayerManager().forEach(profile -> {
                    Player player = Bukkit.getPlayer(profile.getUuid());
                    if (player == null) {
                        return;
                    }
                    profile.getScoreBoard().updateTeams(plugin.getPlayerManager(), nametagHide);
                    profile.getScoreBoard().refresh(player, profile, gamePhase, timeLeft);
                    if (timeLeft < 10 && timeLeft > 5) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 1, 1);
                    } else if (timeLeft <= 5) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 1, 1);
                    }
                    if (profile.getStanding() < nametagHide + 2) {
                        profile.addStanding();
                    }
                });

                if (timeLeft < 1) {
                    gamePhase = GamePhase.END;
                }
                break;
            case END:
                plugin.getGameManager().timerEnd();
                plugin.getGameManager().gameStop();
                this.cancel();
        }
    }
}
