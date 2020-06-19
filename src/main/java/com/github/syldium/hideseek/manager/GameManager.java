package com.github.syldium.hideseek.manager;

import com.github.syldium.hideseek.game.GameTask;
import com.github.syldium.hideseek.HSPlugin;
import com.github.syldium.hideseek.players.PlayerProfile;
import com.github.syldium.hideseek.utils.CustomItem;
import com.github.syldium.hideseek.utils.TextUtils;
import com.github.syldium.hideseek.players.Role;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GameManager {

    private final HSPlugin plugin;

    private boolean isStarted = false;
    private String arena;
    private BukkitTask task;

    private int hunters = 0;
    private int thieves = 0;

    public GameManager(HSPlugin plugin) {
        this.plugin = plugin;
    }

    public void gameStart(String from) {
        if (isStarted) return;
        isStarted = true;
        task = new GameTask(plugin).runTaskTimer(plugin, plugin.getPlayerManager().players.size() * 5L, 20L);
    }

    public void randomRole() {
        hunters = 0;
        thieves = 0;
        Random r = new Random();
        for (PlayerProfile profile : plugin.getPlayerManager().players.values()) {
            if (thieves == hunters) {
                if (r.nextInt(11) >= 5) {
                    profile.setRole(Role.HUNTER);
                    hunters++;
                } else {
                    profile.setRole(Role.THIEVE);
                    thieves++;
                }
            } else {
                if (thieves < hunters) {
                    profile.setRole(Role.THIEVE);
                    thieves++;
                } else {
                    profile.setRole(Role.HUNTER);
                    hunters++;
                }
            }
        }
    }

    public void gameStop() {
        isStarted = false;
        task.cancel();

        plugin.getPlayerManager().players.forEach((uuid, profile) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                player.getInventory().clear();
                player.setGameMode(profile.getGameMode());
                if (plugin.getConfig().getBoolean("save-inventory")) {
                    player.getInventory().setContents(profile.getInventory());
                }
                player.updateInventory();
                player.setWalkSpeed(0.2F);
                TextUtils.sendMessage(player, ChatColor.YELLOW + LocaleManager.END);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, SoundCategory.PLAYERS, 1, 1);

                player.setPlayerListName(ChatColor.RESET + player.getName());

                if (plugin.getArenaManager().getArena(arena).getLobbyLocation() != null) {
                    player.teleport(plugin.getArenaManager().getArena(arena).getLobbyLocation());
                }
            }
        });
        plugin.getPlayerManager().players.clear();
    }

    public void setArena(String arenaName) {
        this.arena = arenaName;
    }

    public void countPlayers() {
        hunters = 0;
        thieves = 0;
        plugin.getPlayerManager().players.forEach((uuid, profile) -> {
            Player player = Bukkit.getPlayer(profile.getUuid());
            if (profile.getRole() == Role.HUNTER && profile.isAlive() && player != null && player.isOnline()) hunters++;
            if (profile.getRole() == Role.THIEVE && profile.isAlive() && player != null && player.isOnline()) thieves++;
        });
    }

    public List<String> playersList() {
        List<String> message = new ArrayList<>();
        message.add(ChatColor.WHITE + "["  + ChatColor.DARK_GREEN + LocaleManager.COMPOSITION  + ChatColor.WHITE + "]");
        plugin.getPlayerManager().players.forEach((uuid, profile) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            message.add(ChatColor.WHITE + player.getName() + " : " + profile.getRoleString(true));
        });
        return message;
    }

    public boolean abortNeeded() {
        countPlayers();
        return (hunters < 1 || thieves < 1);
    }

    public void checkGame() {
        if (thieves < 1) {
            for (UUID uuid : plugin.getPlayerManager().players.keySet()) {
                Player player = Bukkit.getPlayer(uuid);
                TextUtils.sendMessage(player, ChatColor.RESET + LocaleManager.WIN.replace("%winner%", ChatColor.BLUE + LocaleManager.ROLES.get("hunters") + ChatColor.RESET));
            }
            gameStop();
        } else if (hunters < 1) {
            timerEnd();
            gameStop();
        }
    }

    public void countdownEnd() {
        plugin.getPlayerManager().players.forEach((uuid, profile) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            player.setInvulnerable(false);
            player.getInventory().clear();
            player.setPlayerListName(profile.getTabNameColor() + player.getName());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1.0f, 1.0f);

            if (profile.getRole() == Role.THIEVE) {
                CustomItem.give(plugin, player, profile.getRole());
                player.setWalkSpeed(0.2F);
                player.teleport(plugin.getArenaManager().getArena(arena).getSpawnLocation());
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 19 * plugin.getConfig().getInt("hide-timer"), 1, false, false));
                player.sendTitle(
                        ChatColor.DARK_RED + LocaleManager.TITLE_GO,
                        ChatColor.GRAY + LocaleManager.TITLE_GO_HIDE,
                        5, 60, 5
                );
            } else if (profile.getRole() == Role.HUNTER) {
                player.teleport(plugin.getArenaManager().getArena(arena).getHunterLocation());
                player.setWalkSpeed(0.3F);
                player.sendTitle(
                        ChatColor.DARK_GREEN + LocaleManager.TITLE_WAIT,
                        ChatColor.GRAY + LocaleManager.TITLE_WAIT_EXPLANATION,
                        5, 60, 5
                );
            }
        });
    }

    public void hideEnd() {
        plugin.getPlayerManager().players.forEach((uuid, profile) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1.0f, 1.0f);

                if (profile.getRole() == Role.HUNTER) {
                    CustomItem.give(plugin, player, profile.getRole());
                    player.teleport(plugin.getArenaManager().getArena(arena).getSpawnLocation());
                }
            }
        });
    }

    public void timerEnd() {
        for (UUID uuid : plugin.getPlayerManager().players.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            TextUtils.sendMessage(player, ChatColor.RESET + LocaleManager.WIN.replace("%winner%", ChatColor.DARK_RED + LocaleManager.ROLES.get("thieves") + ChatColor.RESET));
        }
    }

    public void highlight() {
        plugin.getPlayerManager().players.forEach((uuid, pm) -> {
            if (pm.getRole() == Role.THIEVE) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0));
                }
            }
        });
    }

    public boolean isStarted() {
        return isStarted;
    }
}
