package fr.momosced.hideandseek.Commands;

import com.google.common.collect.Lists;
import fr.momosced.hideandseek.LocaleManager;
import fr.momosced.hideandseek.Main;
import fr.momosced.hideandseek.Utils.TextUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ArenaCommands implements CommandExecutor, TabCompleter {

    private Main plugin = Main.getInstance();
    private Commands commands = plugin.commands;

    private List<String> tabComplete = Arrays.asList("help", "reload", "create", "setspawn", "sethunter", "setlobby");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (! command.getName().equalsIgnoreCase(commands.cmd1)) return false;
        if (args.length != 2) {
            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                for (String line : LocaleManager.COMMAND_ARENA_HELP) {
                    sender.sendMessage(ChatColor.GOLD + line);
                }
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                plugin.am.clear();
                plugin.am.loadFromConfig();
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
                if (plugin.am.exists(name.toLowerCase())) {
                    TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_ARENA_ALREADY_EXIST.replace("%arena%", name.toLowerCase()));
                } else {
                    plugin.am.registerArena(name.toLowerCase());
                    TextUtils.sendSuccessMessage(sender, LocaleManager.RETURN_CREATE.replace("%arena%", name.toLowerCase()));
                }
                return true;

            case "setspawn":
            case "sethunter":
            case "setlobby":
                if (sender instanceof Player) {
                    if (! plugin.am.exists(name.toLowerCase())) {
                        TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_ARENA_DONT_EXIST.replace("%arena%", name.toLowerCase()));
                        return true;
                    }
                    Location location = ((Player) sender).getLocation();
                    String msg = "";
                    if (action.equalsIgnoreCase("setspawn")) {
                        plugin.am.getArena(name).setSpawnLocation(location);
                        msg = LocaleManager.RETURN_SETSPAWN;
                    } else if (action.equalsIgnoreCase("sethunter")) {
                        plugin.am.getArena(name).setHunterLocation(location);
                        msg = LocaleManager.RETURN_SETHUNTER;
                    } else if (action.equalsIgnoreCase("setlobby")) {
                        plugin.am.getArena(name).setLobbyLocation(location);
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
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabList = Lists.newArrayList();

        if (args.length == 1) {
            for (String c : tabComplete) {
                if (c.startsWith(args[0].toLowerCase())) {
                    tabList.add(c);
                }
            }
            return tabList;
        } else if (args.length == 2) {
            plugin.am.getArenaList().forEach((name,arena) -> {
                if (name.startsWith(args[1].toLowerCase())) {
                    tabList.add(name);
                }
            });
            return tabList;
        }
        return null;
    }
}
