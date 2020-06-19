package com.github.syldium.hideseek.commands;

import com.github.syldium.hideseek.HSPlugin;
import com.github.syldium.hideseek.manager.LocaleManager;
import com.github.syldium.hideseek.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class ArenaCommands implements CommandExecutor, TabCompleter {

    private final HSPlugin plugin;
    private final List<String> tabComplete = Arrays.asList("help", "reload", "create", "setspawn", "sethunter", "setlobby");

    ArenaCommands(HSPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 2) {
            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                for (String line : LocaleManager.COMMAND_ARENA_HELP) {
                    sender.sendMessage(ChatColor.GOLD + line);
                }
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                plugin.getArenaManager().clear();
                plugin.getArenaManager().loadFromConfig();
                TextUtils.sendSuccessMessage(sender, LocaleManager.RETURN_ARENAS_RELOAD);
                return true;
            } else {
                return false;
            }
        }

        String action = args[0];
        String name = args[1];

        switch (action) {
            case "create":
                if (plugin.getArenaManager().exists(name.toLowerCase())) {
                    TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_ARENA_ALREADY_EXIST.replace("%arena%", name.toLowerCase()));
                } else {
                    plugin.getArenaManager().registerArena(name.toLowerCase());
                    TextUtils.sendSuccessMessage(sender, LocaleManager.RETURN_CREATE.replace("%arena%", name.toLowerCase()));
                }
                return true;

            case "setspawn":
            case "sethunter":
            case "setlobby":
                if (sender instanceof Player) {
                    if (! plugin.getArenaManager().exists(name.toLowerCase())) {
                        TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_ARENA_DONT_EXIST.replace("%arena%", name.toLowerCase()));
                        return true;
                    }
                    Location location = ((Player) sender).getLocation();
                    String msg;
                    if (action.equalsIgnoreCase("setspawn")) {
                        plugin.getArenaManager().getArena(name).setSpawnLocation(location);
                        msg = LocaleManager.RETURN_SETSPAWN;
                    } else if (action.equalsIgnoreCase("sethunter")) {
                        plugin.getArenaManager().getArena(name).setHunterLocation(location);
                        msg = LocaleManager.RETURN_SETHUNTER;
                    } else {
                        plugin.getArenaManager().getArena(name).setLobbyLocation(location);
                        msg = LocaleManager.RETURN_SETLOBBY;
                    }
                    TextUtils.sendSuccessMessage(sender, msg.replace("%xyz%", "(" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ() + ")"));
                } else {
                    TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_MUST_BE_PLAYER);
                }
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return tabComplete.stream()
                    .filter(action -> action.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return plugin.getArenaManager().suggest(args[1].toLowerCase());
        }
        return Collections.emptyList();
    }
}
