package com.github.syldium.hideseek.manager;

import com.github.syldium.hideseek.HSPlugin;
import com.github.syldium.hideseek.players.PlayerProfile;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerManager {

    private final HSPlugin plugin;

    protected Map<UUID, PlayerProfile> players = new HashMap<>();

    public PlayerManager(HSPlugin plugin) {
        this.plugin = plugin;
    }

    public PlayerProfile join(Player player) {
        PlayerProfile profile = new PlayerProfile(player);
        profile.getScoreBoard().apply(player);
        if (plugin.getConfig().getBoolean("save-inventory")) {
            profile.setInventory(player.getInventory().getContents());
            // @todo save into file ?
        }
        players.put(player.getUniqueId(), profile);
        return profile;
    }

    public void leave(UUID uuid) {
        players.remove(uuid);
    }

    public void leave(Player player) {
        leave(player.getUniqueId());
    }

    public PlayerProfile getProfile(UUID uuid) {
        return players.get(uuid);
    }

    public PlayerProfile getProfile(Entity entity) {
        return getProfile(entity.getUniqueId());
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean isInGame(UUID uuid) {
        return players.containsKey(uuid);
    }

    public boolean isInGame(Entity entity) {
        return isInGame(entity.getUniqueId());
    }

    public void forEach(Consumer<? super PlayerProfile> consumer) {
        players.values().forEach(consumer);
    }

    public int size() {
        return players.size();
    }
}
