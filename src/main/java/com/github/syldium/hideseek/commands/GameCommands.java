package com.github.syldium.hideseek.commands;

import com.github.syldium.hideseek.manager.LocaleManager;
import com.github.syldium.hideseek.HSPlugin;
import com.github.syldium.hideseek.players.PlayerProfile;
import com.github.syldium.hideseek.players.Role;
import com.github.syldium.hideseek.utils.TextUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

class GameCommands implements CommandExecutor, TabCompleter {

    private final HSPlugin plugin;
    private final List<String> tabComplete = Arrays.asList("help", "reload", "join", "leave", "start", "stop", "role", "randomall", "joinall", "kickall");

    GameCommands(HSPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1 || args.length > 2) return false;

        String action = args[0];
        switch (action) {
            case "help":
                for (String line : LocaleManager.COMMAND_GAME_HELP) {
                    sender.sendMessage(ChatColor.GOLD + line);
                }
                return true;

            case "reload":
                if (!hasPermission(sender,"hsgame.admin.reload")) return true;
                plugin.reloadConfig();
                LocaleManager.loadLocale(plugin);
                TextUtils.sendSuccessMessage(sender, LocaleManager.RETURN_GAME_RELOAD);
                return true;

            case "join":
                if (!hasPermission(sender,"hsgame.join")) return true;
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!plugin.getPlayerManager().isInGame(player)) {
                        plugin.getPlayerManager().join(player);
                        TextUtils.sendInfoMessage(player, LocaleManager.JOIN_PARTY);
                    } else {
                        TextUtils.sendErrorMessage(player, LocaleManager.ERROR_ALREADY_IN);
                    }
                } else {
                    TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_MUST_BE_PLAYER);
                }
                return true;

            case "leave":
                if (!hasPermission(sender,"hsgame.leave")) return true;
                if (sender instanceof Player) {
                    UUID uuid = ((Player) sender).getUniqueId();
                    if (plugin.getPlayerManager().isInGame(uuid)) {
                        plugin.getPlayerManager().leave(uuid);
                       TextUtils.sendInfoMessage(sender, LocaleManager.QUIT_PARTY);
                    } else {
                        TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_NOT_JOINED);
                    }
                } else {
                    TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_MUST_BE_PLAYER);
                }
                return true;

            case "start":
                if (!hasPermission(sender,"hsgame.start")) return true;
                if (args.length != 2) return false;
                if (! plugin.getArenaManager().exists(args[1].toLowerCase())) {
                    TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_ARENA_DONT_EXIST.replace("%arena%", args[1].toLowerCase()));
                    return true;
                }
                if (! plugin.getGameManager().isStarted()) {
                    if (!plugin.getPlayerManager().isEmpty()) {
                        plugin.getGameManager().setArena(args[1].toLowerCase());
                        plugin.getGameManager().gameStart(sender.getName());
                    } else {
                        TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_EMPTY_GAME);
                    }
                } else {
                    TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_ALREADY_STARTED);
                }
                return true;

            case "stop":
                if (!hasPermission(sender,"hsgame.stop")) return true;
                plugin.getGameManager().gameStop();
                TextUtils.sendSuccessMessage(sender, LocaleManager.RETURN_STOP);
                return true;

            case "role":
                if (sender instanceof Player) {
                    UUID uuid = ((Player) sender).getUniqueId();
                    if (plugin.getPlayerManager().isInGame(uuid)) {
                        PlayerProfile profile = plugin.getPlayerManager().getProfile(uuid);
                        if (args.length == 1) {
                            TextUtils.sendInfoMessage(sender, LocaleManager.roleSentence(profile, false));
                        } else {
                            if (args[1].toLowerCase().equals("list")) {
                                plugin.getGameManager().playersList().forEach(sender::sendMessage);
                                return true;
                            } else if (!plugin.getGameManager().isStarted()) {
                                if (args[1].toLowerCase().equals("hunter")) {
                                    profile.setRole(Role.HUNTER);
                                    TextUtils.sendInfoMessage(sender, LocaleManager.roleSentence(profile, true));
                                } else if (args[1].toLowerCase().equals("thieve")) {
                                    profile.setRole(Role.THIEVE);
                                    TextUtils.sendInfoMessage(sender, LocaleManager.roleSentence(profile, true));
                                } else {
                                    TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_ROLE_UNKNOWN.replace("%role%", args[1].toLowerCase()));
                                }
                            }
                        }
                    } else {
                        TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_ROLES_OUTSIDE);
                    }
                } else {
                    TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_MUST_BE_PLAYER);
                }
                return true;

            case "randomall":
                if (!hasPermission(sender,"hsgame.admin.randomall")) return true;
                plugin.getGameManager().randomRole();
                int size = plugin.getPlayerManager().size();
                if (size > 1) {
                    TextUtils.sendInfoMessage(sender, LocaleManager.RETURN_SEVERAL_RANDOM.replace("%nb%", "" + size));
                } else {
                    TextUtils.sendInfoMessage(sender, LocaleManager.RETURN_ONE_RANDOM.replace("%nb%", "" + size));
                }
                return true;

            case "joinall":
                if (!hasPermission(sender,"hsgame.admin.joinall")) return true;
                for(Player player : plugin.getServer().getOnlinePlayers()) {
                    if (!plugin.getPlayerManager().isInGame(player)) {
                        plugin.getPlayerManager().join(player);
                    }
                }
                plugin.getGameManager().randomRole();
                TextUtils.sendSuccessMessage(sender, LocaleManager.RETURN_JOINALL);
                return true;
            case "kickall":
                if (!hasPermission(sender,"hsgame.admin.kickall")) return true;
                for(Player player : plugin.getServer().getOnlinePlayers()) {
                    if (plugin.getPlayerManager().isInGame(player)) {
                        plugin.getPlayerManager().leave(player);
                    }
                }
                TextUtils.sendSuccessMessage(sender, LocaleManager.RETURN_KICKALL);
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> tabList = new ArrayList<>();

        if (args.length == 1) {
            for (String c : tabComplete) {
                if (c.startsWith(args[0].toLowerCase())) {
                    tabList.add(c);
                }
            }
            return tabList;
        } else if (args.length == 2) {
            if (args[0].toLowerCase().equals("role")) {
                if ("list".startsWith(args[1])) tabList.add("list");
                //if ("random".startsWith(args[1])) tabList.add("random");
                if ("hunter".startsWith(args[1])) tabList.add("hunter");
                if ("thieve".startsWith(args[1])) tabList.add("thieve");
                return tabList;
            } else if (args[0].toLowerCase().equals("start")) {
                return plugin.getArenaManager().suggest(args[1].toLowerCase());
            }
        }
        return null;
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            return true;
        } else {
            TextUtils.sendErrorMessage(sender,"You do not have permission!");
            return false;
        }
    }
}
