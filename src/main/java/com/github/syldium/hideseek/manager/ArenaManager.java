package com.github.syldium.hideseek.manager;

import com.github.syldium.hideseek.Arena;
import com.github.syldium.hideseek.HSPlugin;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaManager {

    private final Plugin plugin = HSPlugin.getPlugin(HSPlugin.class);

    private final Map<String, Arena> arenas = new HashMap<>();

    public void registerArena(String name) {
        arenas.put(name, new Arena());
    }

    public void registerArena(String name, Location spawnLoc, Location seekerLoc, Location lobbyLoc) {
        arenas.put(name, new Arena(spawnLoc, seekerLoc, lobbyLoc));
    }

    public void remove(String name) {
        arenas.remove(name);
    }

    public void clear() {
        arenas.clear();
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public Map<String, Arena> getArenaList() {
        return arenas;
    }

    public boolean exists(String name) {
        return arenas.get(name) != null;
    }

    public void loadFromConfig() {
        for (Map.Entry<String, Object> entry : plugin.getConfig().getConfigurationSection("arenas").getValues(true).entrySet()) {
            arenas.put(entry.getKey(), (Arena)entry.getValue());
        }
    }

    public void saveToConfig() {
        plugin.getConfig().set("arenas", arenas);
    }

    public List<String> suggest(String from) {
        List<String> suggestions = new ArrayList<>();
        for (String arenaName : arenas.keySet()) {
            if (arenaName.startsWith(from)) {
                suggestions.add(arenaName);
            }
        }
        return suggestions;
    }
}
