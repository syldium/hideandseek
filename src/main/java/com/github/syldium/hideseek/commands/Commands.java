package com.github.syldium.hideseek.commands;

import com.github.syldium.hideseek.HSPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

import java.util.Objects;

public class Commands {

    public static final String arena = "hsarena";
    public static final String game = "hsgame";

    public static void registerCommands(HSPlugin plugin) {
        registerCommand(plugin, arena, new ArenaCommands(plugin));
        registerCommand(plugin, game, new GameCommands(plugin));
    }

    private static void registerCommand(HSPlugin plugin, String name, CommandExecutor executor) {
        PluginCommand command = Objects.requireNonNull(plugin.getCommand(name), "Can't register /" + name + " command");
        command.setExecutor(executor);
    }
}
