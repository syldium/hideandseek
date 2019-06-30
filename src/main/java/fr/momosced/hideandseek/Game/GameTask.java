package fr.momosced.hideandseek.Game;

import fr.momosced.hideandseek.LocaleManager;
import fr.momosced.hideandseek.Main;
import fr.momosced.hideandseek.PlayerData.Role;
import fr.momosced.hideandseek.Utils.Config;
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

    private Main plugin = Main.getInstance();
    private GamePhase gamePhase = GamePhase.COUNTDOWN;

    private int countdown = Config.COUNTDOWN;
    private int hidetimer = Config.HIDE_TIMER;
    private int timeleft = Config.GAME_DURATION;

    @Override
    public void run() {
        switch (gamePhase) {
            case COUNTDOWN:
                countdown--;
                plugin.playermanager.forEach((uuid, pm) -> {
                    Player player = Bukkit.getPlayer(uuid);
                    player.setGameMode(GameMode.ADVENTURE);
                    plugin.scoreboard.scoreingame(player, pm, gamePhase, countdown);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new ComponentBuilder(LocaleManager.ACTIONBAR_GAME_START_IN.replace("%time%", "" + countdown))
                                    .color(ChatColor.AQUA).create()
                    );
                });
                if (countdown < 1) {
                    plugin.gamemanager.countdownEnd();
                    gamePhase = GamePhase.HIDE;
                }
                break;
            case HIDE:
                hidetimer--;
                plugin.playermanager.forEach((uuid, pm) -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        if (pm.getRole() == Role.THIEVE) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    new ComponentBuilder(LocaleManager.ACTIONBAR_HUNTER_COMING_IN.replace("%time%", "" + hidetimer))
                                            .color(ChatColor.DARK_RED).create()
                            );
                        } else if (pm.getRole() == Role.HUNTER) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    new ComponentBuilder(LocaleManager.ACTIONBAR_TELEPORT_IN.replace("%time%", "" + hidetimer))
                                            .color(ChatColor.BLUE).create()
                            );
                        }
                        plugin.scoreboard.scoreingame(player, pm, gamePhase, hidetimer);
                        if (pm.getStanding() < Config.NAMETAG_HIDE+2) {
                            pm.addStanding();
                        }
                    }
                });
                if (hidetimer <= 1) {
                    plugin.gamemanager.hideEnd();
                    gamePhase = GamePhase.SEEK;
                }
                break;
            case SEEK:
                timeleft--;

                plugin.playermanager.forEach((uuid, pm) -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        plugin.scoreboard.scoreingame(player, pm, gamePhase, timeleft);
                        if (timeleft < 10 && timeleft > 5) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 1, 1);
                        } else if (timeleft <= 5) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 1, 1);
                        }
                    }
                    if (pm.getStanding() < Config.NAMETAG_HIDE+2) {
                        pm.addStanding();
                    }
                });

                if (timeleft < 1) {
                    gamePhase = GamePhase.END;
                }
                break;
            case END:
                plugin.gamemanager.timerEnd();
                plugin.gamemanager.gameStop();
                this.cancel();
            }
    }
}
