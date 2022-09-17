package com.pequla.link;

import org.bukkit.ChatColor;

import java.util.UUID;

public class PluginUtils {

    public static String bold(String input) {
        return "**" + ChatColor.stripColor(input) + "**";
    }

    public static String cleanUUID(UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}
