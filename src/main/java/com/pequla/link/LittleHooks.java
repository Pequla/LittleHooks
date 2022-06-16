package com.pequla.link;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class LittleHooks extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager manager = getServer().getPluginManager();
        LittleLink plugin = (LittleLink) manager.getPlugin("LittleLink");
        if (plugin == null) {
            getLogger().warning("Plugin LittleLink not found");
            manager.disablePlugin(this);
            return;
        }

        // Save config from resources to server directory
        saveDefaultConfig();

        // Check if webhook url was edited
        if (Objects.equals(getConfig().getString("webhook-url"), "url")) {
            getLogger().warning("Please change the webhook url and restart the server");
            manager.disablePlugin(this);
            return;
        }

        // Registering the event handler
        manager.registerEvents(new GameHandler(this, plugin), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
