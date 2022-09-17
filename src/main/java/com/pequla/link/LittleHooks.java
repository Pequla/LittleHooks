package com.pequla.link;

import com.pequla.link.model.EmbedModel;
import com.pequla.link.model.MessageModel;
import com.pequla.link.service.DataService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
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

        // Starting message
        sendMessage("Server starting");

        // Registering the event handler
        manager.registerEvents(new GameHandler(this, plugin), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        sendMessage("Server stopped");
    }

    public void sendMessage(MessageModel model) {
        try {
            String url = getConfig().getString("webhook-url");
            if (url == null) throw new RuntimeException("Webhook URL not found");

            DataService service = DataService.getInstance();
            String json = service.getMapper().writeValueAsString(model);

            new Thread(() -> {
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
                    getLogger().severe("Webhook could not be sent! " + e.getMessage());
                }

            }).start();
        } catch (Exception e) {
            getLogger().severe("Webhook could not be sent! " + e.getMessage());
        }
    }

    public void sendMessage(Player player, EmbedModel model) {
        sendMessage(MessageModel.builder()
                .username(player.getName())
                .avatar_url("https://visage.surgeplay.com/face/" + PluginUtils.cleanUUID(player.getUniqueId()))
                .embeds(List.of(model))
                .build()
        );
    }

    public void sendMessage(String content) {
        sendMessage(MessageModel.builder()
                .embeds(List.of(EmbedModel.builder()
                        .color(getConfig().getInt("color.system"))
                        .description(PluginUtils.bold(content))
                        .timestamp(Instant.now().toString())
                        .build()))
                .build()
        );
    }
}
