package com.github.syldium.hideseek.manager;

import com.github.syldium.hideseek.HSPlugin;
import com.github.syldium.hideseek.players.PlayerProfile;
import com.github.syldium.hideseek.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocaleManager {

    private static final FileConfiguration def_locale = YamlConfiguration.loadConfiguration(new File(HSPlugin.getPlugin(HSPlugin.class).getDataFolder() + "/locales/default.yml"));
    private static String lang = "en";
    private static FileConfiguration locale;

    public static String PREFIX;

    public static String TITLE_READY;
    public static String TITLE_WAIT;
    public static String TITLE_WAIT_EXPLANATION;
    public static String TITLE_GO;
    public static String TITLE_GO_HIDE;

    public static String ACTIONBAR_GAME_START_IN;
    public static String ACTIONBAR_HUNTER_COMING_IN;
    public static String ACTIONBAR_TELEPORT_IN;

    public static Map<String, String> ROLES;
    public static String COMPOSITION;
    public static String YOU_ARE;
    public static String YOU_ARE_NOW;

    public static String JOIN_PARTY;
    public static String RECONNECT;
    public static String GAME_RESUME;
    public static String QUIT_PARTY;
    public static String DISCONNECT;
    public static String GAME_PAUSE;

    public static String WIN;
    public static String END;

    public static String RETURN_CREATE;
    public static String RETURN_SETSPAWN;
    public static String RETURN_SETHUNTER;
    public static String RETURN_SETLOBBY;
    public static String RETURN_ARENAS_RELOAD;
    public static String RETURN_GAME_RELOAD;
    public static String RETURN_ONE_RANDOM;
    public static String RETURN_SEVERAL_RANDOM;
    public static String RETURN_STOP;
    public static String RETURN_JOINALL;
    public static String RETURN_KICKALL;

    public static String ERROR_MUST_BE_PLAYER;
    public static String ERROR_ARENA_DONT_EXIST;
    public static String ERROR_ARENA_ALREADY_EXIST;
    public static String ERROR_EMPTY_GAME;
    public static String ERROR_ALREADY_IN;
    public static String ERROR_NOT_JOINED;
    public static String ERROR_ROLES_OUTSIDE;
    public static String ERROR_ROLE_UNKNOWN;
    public static String ERROR_ALREADY_STARTED;

    public static String ITEM_SWORD;
    public static String ITEM_BOW_TRIDENT;
    public static String ITEM_STICK;
    public static String ITEM_BLAZE;
    public static String ITEM_RELOADING;

    public static List<String> COMMAND_GAME_HELP;
    public static List<String> COMMAND_ARENA_HELP;

    public static String SCOREBOARD_NAME;
    public static String SCOREBOARD_ROLE;
    public static String SCOREBOARD_PHASE;

    public static void loadLocale(HSPlugin plugin) {
        lang = plugin.getConfig().getString("lang");
        if (lang.equals("en")) {
            lang = "default";
        }
        File file = new File(plugin.getDataFolder() + "/locales/" + lang + ".yml");
        if (!file.exists()) {
            TextUtils.sendConsoleMessage("Can't load locale '" + lang + "'");
            TextUtils.sendConsoleMessage("Switching to default locale 'en'");
            locale = def_locale;
        } else {
            locale = YamlConfiguration.loadConfiguration(file);
            TextUtils.sendConsoleMessage("Locale '" + lang + "' successfully loaded");
        }
        loadStrings();
    }

    public static String roleSentence(PlayerProfile pm, boolean isNew) {
        String role = pm.getRole().toString().toLowerCase();
        if (isNew)
            return YOU_ARE_NOW.replace("%role%", pm.getTabNameColor() + ROLES.get(role) + ChatColor.DARK_AQUA);
        else
            return YOU_ARE.replace("%role%", pm.getTabNameColor() + ROLES.get(role) + ChatColor.DARK_AQUA);
    }

    private static void loadStrings() {
        PREFIX = getString("prefix");

        TITLE_READY = getString("messages.title.ready");
        TITLE_WAIT = getString("messages.title.wait");
        TITLE_WAIT_EXPLANATION = getString("messages.title.wait-explanation");
        TITLE_GO = getString("messages.title.go");
        TITLE_GO_HIDE = getString("messages.title.go-hide");

        ACTIONBAR_GAME_START_IN = getString("messages.actionbar.game-start-in");
        ACTIONBAR_HUNTER_COMING_IN = getString("messages.actionbar.hunter-coming-in");
        ACTIONBAR_TELEPORT_IN = getString("messages.actionbar.teleport-in");

        final Map<String, String> rolesByName = new HashMap<>();
        rolesByName.put("thieve", getString("role.thieve.name"));
        rolesByName.put("hunter", getString("role.hunter.name"));
        rolesByName.put("spectator", getString("role.spectator.name"));
        rolesByName.put("thieves", getString("role.thieve.plural"));
        rolesByName.put("hunters", getString("role.hunter.plural"));
        rolesByName.put("spectators", getString("role.spectator.plural"));
        ROLES = Collections.unmodifiableMap(rolesByName);
        COMPOSITION = getString("messages.role.composition");
        YOU_ARE = getString("messages.role.you-are");
        YOU_ARE_NOW = getString("messages.role.you-are-now");

        JOIN_PARTY = getString("messages.join.party");
        RECONNECT = getString("messages.join.reconnect");
        GAME_RESUME = getString("messages.join.game-resume");
        QUIT_PARTY = getString("messages.quit.party");
        DISCONNECT = getString("messages.quit.disconnect");
        GAME_PAUSE = getString("messages.quit.game-pause");

        WIN = getString("messages.win");
        END = getString("messages.end");

        RETURN_CREATE = getString("messages.return.create");
        RETURN_SETSPAWN = getString("messages.return.setspawn");
        RETURN_SETHUNTER = getString("messages.return.sethunter");
        RETURN_SETLOBBY = getString("messages.return.setlobby");
        RETURN_ARENAS_RELOAD = getString("messages.return.arenas-reload");
        RETURN_GAME_RELOAD = getString("messages.return.game-reload");
        RETURN_ONE_RANDOM = getString("messages.return.random.one");
        RETURN_SEVERAL_RANDOM = getString("messages.return.random.several");
        RETURN_STOP = getString("messages.return.stop");
        RETURN_JOINALL = getString("messages.return.joinall");
        RETURN_KICKALL = getString("messages.return.kickall");

        ERROR_MUST_BE_PLAYER = getString("messages.error.must-be-player");
        ERROR_ARENA_DONT_EXIST = getString("messages.error.arena-dont-exist");
        ERROR_ARENA_ALREADY_EXIST = getString("messages.error.arena-already-exist");
        ERROR_EMPTY_GAME = getString("messages.error.empty-game");
        ERROR_ALREADY_IN = getString("messages.error.already-in");
        ERROR_NOT_JOINED = getString("messages.error.not-joined");
        ERROR_ROLES_OUTSIDE = getString("messages.error.roles-outside");
        ERROR_ROLE_UNKNOWN = getString("messages.error.unknown-role");
        ERROR_ALREADY_STARTED = getString("messages.error.already-started");

        ITEM_SWORD = getString("item.sword");
        ITEM_BOW_TRIDENT = getString("item.bow-trident");
        ITEM_STICK = getString("item.stick");
        ITEM_BLAZE = getString("item.blaze");
        ITEM_RELOADING = getString("item.reloading");

        COMMAND_GAME_HELP = getStringList("command.hsgame-help");
        COMMAND_ARENA_HELP = getStringList("command.hsarena-help");

        SCOREBOARD_NAME = getString("scoreboard.name");
        SCOREBOARD_ROLE = getString("scoreboard.role");
        SCOREBOARD_PHASE = getString("scoreboard.phase");
    }

    private static String getString(String path) {
        if (locale == null)
            throw new NullPointerException("Locale not loaded");

        try {
            String message = ChatColor.translateAlternateColorCodes('&', locale.getString(path));
            return message.contains("_UNUSED") ? null : message;
        } catch (NullPointerException e) {
            TextUtils.sendConsoleMessage(ChatColor.DARK_RED + "Unable to find '" + path + "' in locale " + lang + ". Bad File?");
            TextUtils.sendConsoleMessage(ChatColor.DARK_RED + "Using default locale to get value");
            return def_locale.getString(path);
        }
    }

    private static List<String> getStringList(String path) {
        if (locale == null)
            throw new NullPointerException("Locale not loaded");

        try {
            List<String> raw = locale.getStringList(path);
            List<String> list = new ArrayList<>();
            for (String s : raw) {
                list.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            return list;

        } catch (IllegalArgumentException e) {
            TextUtils.sendConsoleMessage(ChatColor.DARK_RED + "Unable to find '" + path + "' in locale " + lang + ". Bad File?");
            TextUtils.sendConsoleMessage(ChatColor.DARK_RED + "Using default locale to get value");
            return def_locale.getStringList(path);
        }
    }
}
