package com.github.syldium.hideseek;

import com.github.syldium.hideseek.commands.Commands;
import com.github.syldium.hideseek.listeners.DamageListener;
import com.github.syldium.hideseek.listeners.ItemListener;
import com.github.syldium.hideseek.listeners.PlayerListener;
import com.github.syldium.hideseek.manager.ArenaManager;
import com.github.syldium.hideseek.manager.GameManager;
import com.github.syldium.hideseek.manager.LocaleManager;
import com.github.syldium.hideseek.manager.PlayerManager;
import com.github.syldium.hideseek.utils.ConfigUtil;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class HSPlugin extends JavaPlugin {

    private ArenaManager arenaManager;
    private GameManager gameManager;

    private PlayerManager playerManager;

    static {
        ConfigurationSerialization.registerClass(Arena.class);
    }

    @Override
    public void onEnable() {
        this.saveDefaults();
        instanceClasses();
        this.loadConfig();
    }

    @Override
    public void onDisable() {
        this.arenaManager.saveToConfig();
        saveConfig();
    }

    public void loadConfig() {
        ConfigUtil.setup(this);
        this.arenaManager.loadFromConfig();
        LocaleManager.loadLocale(this);
    }

    private void saveDefaults() {
        this.saveDefaultConfig();
        saveResource("locales/default.yml", true);
        for (String path : new String[] {"locales/fr.yml", "locales/de.yml"}) {
            if (!new File(getDataFolder(), '/' + path).exists()) {
                saveResource(path, false);
            }
        }
    }

    private void instanceClasses(){
        this.arenaManager = new ArenaManager();
        this.gameManager = new GameManager(this);
        this.playerManager = new PlayerManager(this);
        Commands.registerCommands(this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
