package fr.momosced.hideandseek;

import com.google.common.collect.Lists;
import fr.momosced.hideandseek.Commands.Commands;
import fr.momosced.hideandseek.Game.GameEvents;
import fr.momosced.hideandseek.Game.GameManager;
import fr.momosced.hideandseek.PlayerData.PlayerManager;
import fr.momosced.hideandseek.Utils.Config;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private static Main instance;
    public static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "HS" + ChatColor.GRAY + "]";
    private String locale;

    public ArenaManager am;
    public Commands commands;
    public GameEvents gameevents;
    public GameManager gamemanager;
    public PlayerScoreBoard scoreboard;

    static {
        ConfigurationSerialization.registerClass(Arena.class);
    }

    public HashMap<UUID,PlayerManager> playermanager = new HashMap<UUID,PlayerManager>();

    @Override
    public void onEnable() {
        setInstance(this);
        this.saveDefaults();
        instanceClasses();
        this.loadConfig();
        commands.onEnable();
    }

    @Override
    public void onDisable() {
        this.am.saveToConfig();
        saveConfig();
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        Config.load();
        this.am.loadFromConfig();
        LocaleManager.loadLocale(Config.LOCALE);
    }

    public static Main getInstance() {
        return instance;
    }

    private static void setInstance(Main instance) {
        Main.instance = instance;
    }

    private void saveDefaults() {
        this.saveDefaultConfig();
        saveResource("locales/default.yml", true);
        ArrayList<String> defaults = Lists.newArrayList("locales/fr.yml", "locales/de.yml");
        for (String path : defaults)
            if (!new File(getDataFolder(), '/' + path).exists()) saveResource(path, false);
    }

    private void instanceClasses(){
        this.am = new ArenaManager();
        this.commands = new Commands();
        this.gamemanager = new GameManager();
        this.scoreboard = new PlayerScoreBoard();
        this.gameevents = new GameEvents();
        getServer().getPluginManager().registerEvents(this.gameevents, this);
    }
}
