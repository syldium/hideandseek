package com.github.syldium.hideseek.players;

import com.github.syldium.hideseek.manager.LocaleManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerProfile {

    private final UUID uuid;
    private final String name;
    private boolean alive = true;
    private int kills = 0;
    private Role role = Role.SPECTATOR;
    private GameMode gameMode = GameMode.ADVENTURE;
    private byte standing = 0;
    private ItemStack[] inventory;
    private final PlayerScoreboard scoreBoard = new PlayerScoreboard();

    public PlayerProfile(Player player) {
        this(player.getUniqueId(), player.getName());
        this.gameMode = player.getGameMode();
        this.inventory = player.getInventory().getContents();
    }

    public PlayerProfile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getKills() {
        return kills;
    }

    public void addKills(int nb) {
        this.kills += nb;
    }

    public void addStanding() {
        standing += 1;
    }
    public void initStanding() {
        standing = 0;
    }
    public byte getStanding() {
        return standing;
    }

    public Role getRole() {
        return role;
    }

    public String getRoleString(boolean capitalize) {
        String roleName = LocaleManager.ROLES.get(role.toString().toLowerCase());

        if (capitalize) {
            return getTabNameColor() + StringUtils.capitalize(roleName);
        } else {
            return getTabNameColor() + roleName;
        }
    }

    public ChatColor getTabNameColor() {
        if (role == Role.HUNTER) return ChatColor.BLUE;
        else if (role == Role.THIEVE) return ChatColor.DARK_RED;
        else return ChatColor.ITALIC;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public String getName() {
        return this.name;
    }

    public PlayerScoreboard getScoreBoard() {
        return scoreBoard;
    }
}
