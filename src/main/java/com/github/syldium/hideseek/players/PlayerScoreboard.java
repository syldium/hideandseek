package com.github.syldium.hideseek.players;

import com.github.syldium.hideseek.game.GamePhase;
import com.github.syldium.hideseek.manager.LocaleManager;
import com.github.syldium.hideseek.manager.PlayerManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerScoreboard {

    private final Scoreboard scoreboard;
    private final Objective objective;

    private final Team hunters;
    private final Team thieves;
    private final Team nhide;

    private final List<String> scores = new ArrayList<>();

    public PlayerScoreboard() {
        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        objective = scoreboard.registerNewObjective("hsgame", "", "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.DARK_GREEN + LocaleManager.SCOREBOARD_NAME);

        hunters = scoreboard.registerNewTeam("hunters");
        hunters.setColor(ChatColor.BLUE);
        hunters.setAllowFriendlyFire(false);
        thieves = scoreboard.registerNewTeam("thieves");
        thieves.setColor(ChatColor.DARK_RED);
        thieves.setAllowFriendlyFire(false);
        nhide = scoreboard.registerNewTeam("nhide");
        nhide.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    public void apply(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void updateTeams(PlayerManager playerManager, int nametagHide) {
        playerManager.forEach(profile -> {
            updateTeam(hunters, profile.getName(), profile.getRole().equals(Role.HUNTER));
            updateTeam(thieves, profile.getName(), profile.getRole().equals(Role.THIEVE));
            updateTeam(nhide, profile.getName(), profile.getStanding() >= nametagHide);
        });
    }

    private void updateTeam(Team team, String entry, boolean excepted) {
        if (excepted) {
            if (!team.hasEntry(entry)) {
                team.addEntry(entry);
            }
        } else {
            if (team.hasEntry(entry)) {
                team.removeEntry(entry);
            }
        }
    }

    public void refresh(Player player, PlayerProfile profile, GamePhase gamePhase, int timeLeft) {
        if (player == null) {
            return;
        }

        scores.removeIf(score -> {
            scoreboard.resetScores(score);
            return true;
        });

        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            scores.add(profile.getRoleString(true));
        } else {
            scores.add(LocaleManager.SCOREBOARD_ROLE + " : " + profile.getRoleString(true));
        }

        scores.add(LocaleManager.SCOREBOARD_PHASE + " : " + gamePhase.toString());
        scores.add("");

        if (profile.getKills() > 1) {
            scores.add("Kills : " + ChatColor.RED + profile.getKills());
        } else {
            scores.add("Kill : " + ChatColor.RED + profile.getKills());
        }

        int min = timeLeft / 60;
        int sec = timeLeft % 60;
        scores.add(ChatColor.YELLOW + "" + min + ":" + StringUtils.leftPad(String.valueOf(sec), 2, "0"));

        int s = scores.size() + 1;
        for (String value : scores) {
            Score score = objective.getScore(value);
            score.setScore(s--);
        }

        if (!player.getScoreboard().equals(scoreboard)) {
            player.setScoreboard(scoreboard);
        }
    }
}
