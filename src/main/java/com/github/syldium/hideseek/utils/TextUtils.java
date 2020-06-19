package com.github.syldium.hideseek.utils;

import com.github.syldium.hideseek.manager.LocaleManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class TextUtils {

    public static void sendMessage(@Nullable CommandSender to, @Nullable String message) {
        if (to == null || StringUtils.isEmpty(message)) {
            return;
        }
        to.sendMessage(LocaleManager.PREFIX + " " + message);
    }

    public static void sendInfoMessage(@Nullable CommandSender to, @Nullable String message) {
        sendMessage(to, ChatColor.DARK_AQUA + message);
    }
    public static void sendVerboseMessage(@Nullable CommandSender to, @Nullable String message) {
        sendMessage(to, ChatColor.BLUE + message);
    }
    public static void sendSuccessMessage(@Nullable CommandSender to, @Nullable String message) {
        sendMessage(to, ChatColor.DARK_GREEN + message);
    }
    public static void sendErrorMessage(@Nullable CommandSender to, @Nullable String message) {
        sendMessage(to, ChatColor.RED + message);
    }

    public static void sendConsoleMessage(@Nullable String message) {
        if (StringUtils.isNotEmpty(message)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "HS" + ChatColor.GRAY + "] " + ChatColor.DARK_GREEN + message);
        }
    }
}