package com.pequla.link;

import com.pequla.link.model.*;
import com.pequla.link.service.DataService;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DataModel data = core.getPlayerData().get(player.getUniqueId());
        Server server = plugin.getServer();

        sendMessage(player, EmbedModel.builder()
                .color(plugin.getConfig().getInt("color.join"))
                .author(EmbedAuthor.builder()
                        .name(data.getNickname())
                        .icon_url(data.getAvatar())
                        .build())
                .description(bold(event.getJoinMessage()))
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

        sendMessage(player, EmbedModel.builder()
                .color(plugin.getConfig().getInt("color.leave"))
                .author(EmbedAuthor.builder()
                        .name(data.getNickname())
                        .icon_url(data.getAvatar())
                        .build())
                .description(bold(event.getQuitMessage()))
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

        sendMessage(player, EmbedModel.builder()
                .color(plugin.getConfig().getInt("color.death"))
                .author(EmbedAuthor.builder()
                        .name(data.getNickname())
                        .icon_url(data.getAvatar())
                        .build())
                .description(bold(event.getDeathMessage()))
                .footer(EmbedFooter.builder()
                        .text(data.getId())
                        .build())
                .timestamp(Instant.now().toString())
                .build());
    }

    private String getMinecraftAvatarUrl(Player player) {
        return "https://visage.surgeplay.com/face/" + player.getUniqueId().toString().replace("-", "");
    }

    private String bold(String str) {
        return "**" + ChatColor.stripColor(str) + "**";
    }

    private void sendMessage(Player player, EmbedModel model) {
        try {
            String url = plugin.getConfig().getString("webhook-url");
            if (url == null) throw new RuntimeException("Webhook URL not found");

            DataService service = DataService.getInstance();
            String json = service.getMapper().writeValueAsString(MessageModel.builder()
                    .username(player.getName())
                    .avatar_url(getMinecraftAvatarUrl(player))
                    .embeds(List.of(model))
                    .build());

            new Thread(()->{
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                            .build();

                    HttpResponse<String> rsp = service.getClient().send(request, HttpResponse.BodyHandlers.ofString());
                    if (rsp.statusCode() != 204)
                        throw new RuntimeException("Server responded with HTTP " + rsp.statusCode());
                } catch (Exception e) {
                    plugin.getLogger().severe("Webhook could not be sent! " + e.getMessage());
                }

            }).start();
        } catch (Exception e) {
            plugin.getLogger().severe("Webhook could not be sent! " + e.getMessage());
        }
    }
}
