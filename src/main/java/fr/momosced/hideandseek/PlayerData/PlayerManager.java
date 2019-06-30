package fr.momosced.hideandseek.PlayerData;

import fr.momosced.hideandseek.LocaleManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerManager {

    private UUID uuid;
    private boolean inGame;
    private boolean alive;
    private int kills;
    private Role role;
    private GameMode gameMode;
    private byte standing;
    private ItemStack[] inventory;

    public PlayerManager(UUID uuid, boolean inGame, boolean alive, int kills, Role role, GameMode gameMode) {
        this.setUuid(uuid);
        this.setInGame(inGame);
        this.setAlive(alive);
        this.setKills(kills);
        this.setRole(role);
        this.setGameMode(gameMode);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
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

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKills(int nb) {
        this.kills += nb;
    }

    public void addStanding() {
        standing+=1;
    }
    public void initStanding() {
        standing=0;
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
}
