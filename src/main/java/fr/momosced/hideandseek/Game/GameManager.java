package fr.momosced.hideandseek.Game;

import fr.momosced.hideandseek.PlayerScoreBoard;
import fr.momosced.hideandseek.Utils.Config;
import fr.momosced.hideandseek.Utils.CustomItem;
import fr.momosced.hideandseek.LocaleManager;
import fr.momosced.hideandseek.Main;
import fr.momosced.hideandseek.PlayerData.PlayerManager;
import fr.momosced.hideandseek.PlayerData.Role;
import fr.momosced.hideandseek.Utils.TextUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;

public class GameManager {

    private Main plugin = Main.getInstance();

    public boolean isStarted = false;
    public String arena;
    private BukkitTask task;

    private int hunters = 0;
    private int thieves = 0;

    public void gameStart(String from) {
        if (isStarted) return;
        isStarted = true;
        plugin.scoreboard = new PlayerScoreBoard();
        task = new GameTask().runTaskTimer(plugin, plugin.playermanager.size()*5l, 20l);
    }

    public void randomRole() {
        hunters = 0;
        thieves = 0;
        Random r = new Random();
        for (Map.Entry<UUID, PlayerManager> entry : plugin.playermanager.entrySet()) {
            if (thieves == hunters) {
                if (r.nextInt(11) >= 5) {
                    entry.getValue().setRole(Role.HUNTER);
                    hunters++;
                } else {
                    entry.getValue().setRole(Role.THIEVE);
                    thieves++;
                }
            } else {
                if (thieves < hunters) {
                    entry.getValue().setRole(Role.THIEVE);
                    thieves++;
                } else {
                    entry.getValue().setRole(Role.HUNTER);
                    hunters++;
                }
            }
        }
    }

    public void gameStop() {
        isStarted = false;
        task.cancel();

        plugin.playermanager.forEach((uuid,pm) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                player.getInventory().clear();
                player.setGameMode(pm.getGameMode());
                if (Config.SAVE_INVENTORY) {
                    player.getInventory().setContents(pm.getInventory());
                }
                player.updateInventory();
                player.setWalkSpeed(0.2F);
                TextUtils.sendMessage(player, ChatColor.YELLOW + LocaleManager.END);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, SoundCategory.PLAYERS, 1, 1);

                player.setPlayerListName(ChatColor.RESET + player.getName());

                if (plugin.am.getArena(arena).getLobbyLocation() != null) {
                    player.teleport(plugin.am.getArena(arena).getLobbyLocation());
                }
            }
        });
        plugin.playermanager.clear();
    }

    public void setArena(String arenaName) {
        this.arena = arenaName;
    }

    public void countPlayers() {
        hunters = 0;
        thieves = 0;
        plugin.playermanager.forEach((uuid,pm) -> {
            Player player = Bukkit.getPlayer(pm.getUuid());
            if (pm.getRole() == Role.HUNTER && pm.isAlive() && player!=null && player.isOnline()) hunters++;
            if (pm.getRole() == Role.THIEVE && pm.isAlive() && player!=null && player.isOnline()) thieves++;
        });
    }

    public List<String> playersList() {
        List<String> message = new ArrayList<>();
        message.add(ChatColor.WHITE + "["  + ChatColor.DARK_GREEN + LocaleManager.COMPOSITION  + ChatColor.WHITE + "]");
        plugin.playermanager.forEach((uuid,pm) -> {
            if (Bukkit.getPlayer(uuid) == null) return;
            message.add(ChatColor.WHITE + Bukkit.getPlayer(uuid).getName() + " : " + pm.getRoleString(true));
        });
        return message;
    }

    public boolean abortNeeded() {
        countPlayers();
        return (hunters < 1 || thieves < 1);
    }

    public void checkGame() {
        if (thieves < 1) {
            plugin.playermanager.forEach((uuid,pm) -> {
                Player player = Bukkit.getPlayer(uuid);
                TextUtils.sendMessage(player, ChatColor.RESET + LocaleManager.WIN.replace("%winner%", ChatColor.BLUE + LocaleManager.ROLES.get("hunters") + ChatColor.RESET));
            });
            gameStop();
        } else if (hunters < 1) {
            timerEnd();
            gameStop();
        }
    }

    public void countdownEnd() {
        plugin.playermanager.forEach((uuid,pm) -> {
            Player player = Bukkit.getPlayer(uuid);
            player.setInvulnerable(false);
            player.getInventory().clear();
            player.setPlayerListName(pm.getTabNameColor() + player.getName());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1.0f, 1.0f);

            if (pm.getRole() == Role.THIEVE) {
                CustomItem.give(player, pm.getRole());
                player.setWalkSpeed(0.2F);
                player.teleport(plugin.am.getArena(arena).getSpawnLocation());
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 19 * Config.HIDE_TIMER, 1, false, false));
                player.sendTitle(
                        ChatColor.DARK_RED + LocaleManager.TITLE_GO,
                        ChatColor.GRAY + LocaleManager.TITLE_GO_HIDE,
                        5, 60, 5
                );
            } else if (pm.getRole() == Role.HUNTER) {
                player.teleport(plugin.am.getArena(arena).getHunterLocation());
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
        plugin.playermanager.forEach((uuid,pm) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1.0f, 1.0f);

                if (pm.getRole() == Role.HUNTER) {
                    CustomItem.give(player, pm.getRole());
                    player.teleport(plugin.am.getArena(arena).getSpawnLocation());
                }
            }
        });
    }

    public void timerEnd() {
        plugin.playermanager.forEach((uuid,pm) -> {
            Player player = Bukkit.getPlayer(uuid);
            TextUtils.sendMessage(player, ChatColor.RESET + LocaleManager.WIN.replace("%winner%", ChatColor.DARK_RED + LocaleManager.ROLES.get("thieves") + ChatColor.RESET));
        });
    }

    public void highlight() {
        plugin.playermanager.forEach((uuid,pm) -> {
            if (pm.getRole() == Role.THIEVE) {
                Player player = Bukkit.getPlayer(uuid);
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING , 100, 0));
            }
        });
    }
}
