package com.pequla.link;

import com.pequla.link.model.*;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.time.Instant;
import java.util.List;

public class GameHandler implements Listener {

    private final LittleHooks plugin;
    private final LittleLink core;

    public GameHandler(LittleHooks plugin, LittleLink core) {
        this.plugin = plugin;
        this.core = core;
    }

    @EventHandler
    public void onServerLoadEvent(ServerLoadEvent event) {
        new Thread(() -> {
            if (event.getType() == ServerLoadEvent.LoadType.STARTUP) {
                plugin.sendMessage("Server loaded");
            }
        }).start();
    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event) {
        if (plugin.getServer().getWorlds().get(0).equals(event.getWorld())) {
            plugin.sendMessage("Loading the world");
        }
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DataModel data = core.getPlayerData().get(player.getUniqueId());
        Server server = plugin.getServer();

        plugin.sendMessage(player, EmbedModel.builder()
                .color(plugin.getConfig().getInt("color.join"))
                .author(EmbedAuthor.builder()
                        .name(data.getNickname())
                        .icon_url(data.getAvatar())
                        .build())
                .description(PluginUtils.bold(event.getJoinMessage()))
                .fields(List.of(EmbedField.builder()
                        .name("Online:")
                        .value(server.getOnlinePlayers().size() + "/" + server.getMaxPlayers())
                        .inline(false)
                        .build()))
                .footer(EmbedFooter.builder()
                        .text(data.getId())
                        .build())
                .timestamp(Instant.now().toString())
                .build());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DataModel data = core.getPlayerData().get(player.getUniqueId());
        Server server = plugin.getServer();

        plugin.sendMessage(player, EmbedModel.builder()
                .color(plugin.getConfig().getInt("color.leave"))
                .author(EmbedAuthor.builder()
                        .name(data.getNickname())
                        .icon_url(data.getAvatar())
                        .build())
                .description(PluginUtils.bold(event.getQuitMessage()))
                .fields(List.of(EmbedField.builder()
                        .name("Online:")
                        .value((server.getOnlinePlayers().size() - 1) + "/" + server.getMaxPlayers())
                        .inline(false)
                        .build()))
                .footer(EmbedFooter.builder()
                        .text(data.getId())
                        .build())
                .timestamp(Instant.now().toString())
                .build());
    }

    @EventHandler
    public void handlePlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        DataModel data = core.getPlayerData().get(player.getUniqueId());

        plugin.sendMessage(player, EmbedModel.builder()
                .color(plugin.getConfig().getInt("color.death"))
                .author(EmbedAuthor.builder()
                        .name(data.getNickname())
                        .icon_url(data.getAvatar())
                        .build())
                .description(PluginUtils.bold(event.getDeathMessage()))
                .footer(EmbedFooter.builder()
                        .text(data.getId())
                        .build())
                .timestamp(Instant.now().toString())
                .build());
    }
}
