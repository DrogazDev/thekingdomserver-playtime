package nl.drogaz.thekingdomserver_assignment.listeners;

import lombok.Getter;
import nl.drogaz.thekingdomserver_assignment.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaytimeListener implements Listener {
    Main plugin = Main.getPlugin(Main.class);

    private final Map<UUID, PlayerData> players = new HashMap<>();

    private final int AFK_DELAY = 5 * 60;
    private final int BACK_DELAY = 10;

    public PlaytimeListener() {
        // Update per seconde
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis() / 1000;
                for (Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
                    PlayerData data = entry.getValue();
                    Player player = plugin.getServer().getPlayer(entry.getKey());
                    if (player == null) continue;
                    if (!data.isAfk) {
                        if (now - data.lastActivity > AFK_DELAY) {
                            data.isAfk = true;
                        } else {
                            data.playtime++;
                            data.weeklyPlaytime++;
                        }
                    } else {
                        data.afktime++;
                        if (data.backSince > 0 && now - data.backSince >= BACK_DELAY) {
                            data.isAfk = false;
                            data.backSince = 0;
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20, 20);

        // Reset weekly elke zondag om 00:00
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                if (now.getDayOfWeek() == DayOfWeek.SUNDAY && now.getHour() == 0 && now.getMinute() == 0) {
                    resetWeeklyPlaytime();
                }
            }
        }.runTaskTimer(plugin, 1200, 1200); // elke minuut checken
    }

    private void resetWeeklyPlaytime() {
        Bukkit.getLogger().info("[TheKingdom] Weekly playtime reset!");
        for (UUID uuid : players.keySet()) {
            PlayerData data = players.get(uuid);
            if (data != null) {
                data.weeklyPlaytime = 0;
            }
            FileConfiguration config = plugin.getConfigManager().getPlayerConfig(uuid);
            config.set("weeklyPlaytime", 0);
            config.set("lastWeeklyReset", System.currentTimeMillis());
            plugin.getConfigManager().savePlayerConfig(uuid, config);
        }
    }

    private void markActive(Player player) {
        PlayerData playerData = players.get(player.getUniqueId());
        if (playerData == null) return;

        playerData.lastActivity = System.currentTimeMillis() / 1000;
        if (playerData.isAfk && playerData.backSince == 0) {
            playerData.backSince = playerData.lastActivity;
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
            markActive(event.getPlayer());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        markActive(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        PlayerData playerData = loadPlayerData(id);
        players.put(id, playerData);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        savePlayerData(id, players.get(id));
        players.remove(id);
    }

    private PlayerData loadPlayerData(UUID uuid) {
        FileConfiguration configuration = plugin.getConfigManager().getPlayerConfig(uuid);
        PlayerData data = new PlayerData();
        data.playtime = configuration.getInt("playtime", 0);
        data.afktime = configuration.getInt("afktime", 0);
        data.weeklyPlaytime = configuration.getInt("weeklyPlaytime", 0);
        return data;
    }

    private void savePlayerData(UUID uuid, PlayerData data) {
        FileConfiguration configuration = plugin.getConfigManager().getPlayerConfig(uuid);
        configuration.set("playtime", data.playtime);
        configuration.set("afktime", data.afktime);
        configuration.set("weeklyPlaytime", data.weeklyPlaytime);
        plugin.getConfigManager().savePlayerConfig(uuid, configuration);
    }

    private static class PlayerData {
        int playtime = 0;
        int afktime = 0;
        int weeklyPlaytime = 0;
        long lastActivity = System.currentTimeMillis() / 1000;
        boolean isAfk = false;
        long backSince = 0;
    }
}
