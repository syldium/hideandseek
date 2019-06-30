package fr.momosced.hideandseek.Commands;

import com.google.common.collect.Lists;
import fr.momosced.hideandseek.LocaleManager;
import fr.momosced.hideandseek.Main;
import fr.momosced.hideandseek.PlayerData.PlayerManager;
import fr.momosced.hideandseek.PlayerData.Role;
import fr.momosced.hideandseek.Utils.Config;
import fr.momosced.hideandseek.Utils.TextUtils;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GameCommands implements CommandExecutor, TabCompleter {

    private Main plugin = Main.getInstance();
    private Commands commands = plugin.commands;

    private List<String> tabComplete = Arrays.asList("help", "reload", "join", "leave", "start", "stop", "role", "randomall", "joinall", "kickall");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (! command.getName().equalsIgnoreCase(commands.cmd2)) return false;
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
                Config.load();
                LocaleManager.loadLocale(Config.LOCALE);
                TextUtils.sendSuccessMessage(sender, LocaleManager.RETURN_GAME_RELOAD);
                return true;

            case "join":
                if (!hasPermission(sender,"hsgame.join")) return true;
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    UUID uuid = player.getUniqueId();
                    if (! plugin.playermanager.containsKey(uuid)) {
                        PlayerManager pm = new PlayerManager(uuid, false,true, 0, Role.SPECTATOR, player.getGameMode());
                        if (Config.SAVE_INVENTORY) {
                            pm.setInventory(player.getInventory().getContents());
                            // @todo save into file ?
                        }
                        plugin.playermanager.put(uuid, pm);
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
                    if (plugin.playermanager.containsKey(uuid)) {
                        plugin.playermanager.remove(uuid);
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
                if (! plugin.am.exists(args[1].toLowerCase())) {
                    TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_ARENA_DONT_EXIST.replace("%arena%", args[1].toLowerCase()));
                    return true;
                }
                if (! plugin.gamemanager.isStarted) {
                    if (plugin.playermanager.size()!=0) {
                        plugin.gamemanager.setArena(args[1].toLowerCase());
                        plugin.gamemanager.gameStart(sender.getName());
                    } else {
                        TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_EMPTY_GAME);
                    }
                } else {
                    TextUtils.sendErrorMessage(sender, LocaleManager.ERROR_ALREADY_STARTED);
                }
                return true;

            case "stop":
                if (!hasPermission(sender,"hsgame.stop")) return true;
                plugin.gamemanager.gameStop();
                TextUtils.sendSuccessMessage(sender, LocaleManager.RETURN_STOP);
                return true;

            case "role":
                if (sender instanceof Player) {
                    UUID uuid = ((Player) sender).getUniqueId();
                    if (plugin.playermanager.containsKey(uuid)) {
                        PlayerManager pm = plugin.playermanager.get(uuid);
                        if (args.length == 1) {
                            TextUtils.sendInfoMessage(sender, LocaleManager.roleSentence(pm, false));
                        } else if (args.length == 2) {
                            if (args[1].toLowerCase().equals("list")) {
                                plugin.gamemanager.playersList().forEach(msg -> sender.sendMessage(msg));
                                return true;
                            } else if (!plugin.gamemanager.isStarted) {
                                if (args[1].toLowerCase().equals("hunter")) {
                                    pm.setRole(Role.HUNTER);
                                    TextUtils.sendInfoMessage(sender, LocaleManager.roleSentence(pm, true));
                                } else if (args[1].toLowerCase().equals("thieve")) {
                                    pm.setRole(Role.THIEVE);
                                    TextUtils.sendInfoMessage(sender, LocaleManager.roleSentence(pm, true));
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
                plugin.gamemanager.randomRole();
                int size = plugin.playermanager.size();
                if (size > 1) {
                    TextUtils.sendInfoMessage(sender, LocaleManager.RETURN_SEVERAL_RANDOM.replace("%nb%", "" + size));
                } else {
                    TextUtils.sendInfoMessage(sender, LocaleManager.RETURN_ONE_RANDOM.replace("%nb%", "" + size));
                }
                return true;

            case "joinall":
                if (!hasPermission(sender,"hsgame.admin.joinall")) return true;
                for(Player player : plugin.getServer().getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    if (! plugin.playermanager.containsKey(uuid)) {
                        PlayerManager pm = new PlayerManager(uuid, true,true, 0, Role.SPECTATOR, player.getGameMode());
                        if (Config.SAVE_INVENTORY) {
                            pm.setInventory(player.getInventory().getContents());
                            // @todo save into file ?
                        }
                        plugin.playermanager.put(uuid, pm);
                    }
                }
                plugin.gamemanager.randomRole();
                TextUtils.sendSuccessMessage(sender, LocaleManager.RETURN_JOINALL);
                return true;
            case "kickall":
                if (!hasPermission(sender,"hsgame.admin.kickall")) return true;
                for(Player player : plugin.getServer().getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    if (plugin.playermanager.containsKey(uuid)) {
                        plugin.playermanager.remove(uuid);
                    }
                }
                TextUtils.sendSuccessMessage(sender, LocaleManager.RETURN_KICKALL);
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
            if (args[0].toLowerCase().equals("role")) {
                if ("list".startsWith(args[1])) tabList.add("list");
                //if ("random".startsWith(args[1])) tabList.add("random");
                if ("hunter".startsWith(args[1])) tabList.add("hunter");
                if ("thieve".startsWith(args[1])) tabList.add("thieve");
                return tabList;
            } else if (args[0].toLowerCase().equals("start")) {
                plugin.am.getArenaList().forEach((name,arena) -> {
                    if (name.startsWith(args[1].toLowerCase())) {
                        tabList.add(name);
                    }
                });
                return tabList;
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
