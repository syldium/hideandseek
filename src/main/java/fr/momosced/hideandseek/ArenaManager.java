package fr.momosced.hideandseek;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ArenaManager {

    private Plugin plugin = Main.getPlugin(Main.class);

    private Map<String, Arena> arenas = new HashMap<>();

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
}
