package fr.momosced.hideandseek;

import fr.momosced.hideandseek.Game.GamePhase;
import fr.momosced.hideandseek.PlayerData.PlayerManager;
import fr.momosced.hideandseek.PlayerData.Role;
import fr.momosced.hideandseek.Utils.Config;
import fr.momosced.hideandseek.Utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class PlayerScoreBoard {

    private Main plugin = Main.getPlugin(Main.class);

    public ScoreboardManager manager;
    public Scoreboard scoreboard;
    public Objective o;

    public Score role;
    public Score phase;
    public Score separator;
    public Score nbkills;
    public Score timeind;

    public void scoreingame(Player player, PlayerManager pm, GamePhase gamePhase, int timeleft) {
        manager = Bukkit.getScoreboardManager();

        scoreboard = manager.getNewScoreboard();
        o = scoreboard.registerNewObjective("hsgame", "", "");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(ChatColor.DARK_GREEN + LocaleManager.SCOREBOARD_NAME);

        Team hunters = scoreboard.registerNewTeam("hunters");
        hunters.setColor(ChatColor.BLUE);
        hunters.setAllowFriendlyFire(false);
        Team thieves = scoreboard.registerNewTeam("thieves");
        thieves.setColor(ChatColor.DARK_RED);
        thieves.setAllowFriendlyFire(false);
        Team hide = scoreboard.registerNewTeam("nhide");
        hide.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

        plugin.playermanager.forEach((uuid, ppm) -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                if (ppm.getRole().equals(Role.HUNTER)) {
                    hunters.addEntry(p.getName());
                } else if (ppm.getRole().equals(Role.THIEVE)) {
                    thieves.addEntry(p.getName());
                }
                if (ppm.getStanding() >= Config.NAMETAG_HIDE) {
                    hide.addEntry(p.getName());
                }
            }
        });

        if (player.getGameMode().equals(GameMode.SPECTATOR))
            role = o.getScore(pm.getRoleString(true));
        else {
            role = o.getScore(LocaleManager.SCOREBOARD_ROLE + " : " + pm.getRoleString(true));
        }
        role.setScore(5);

        phase = o.getScore(LocaleManager.SCOREBOARD_PHASE + " : " + gamePhase.toString());
        phase.setScore(4);

        separator = o.getScore("");
        separator.setScore(3);

        if (pm.getKills() > 1) {
            nbkills = o.getScore("Kills : " + ChatColor.RED + pm.getKills());
        } else {
            nbkills = o.getScore("Kill : " + ChatColor.RED + pm.getKills());
        }
        nbkills.setScore(2);

        int min = timeleft/60;
        int sec = timeleft%60;
        if (sec > 9) {
            timeind = o.getScore(ChatColor.YELLOW + "" + min + ":" + sec);
        } else {
            timeind = o.getScore(ChatColor.YELLOW + "" + min + ":0" + sec);
        }
        timeind.setScore(1);

        if (player != null) player.setScoreboard(scoreboard);
    }
}
