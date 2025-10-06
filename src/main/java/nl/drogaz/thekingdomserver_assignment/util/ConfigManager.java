package nl.drogaz.thekingdomserver_assignment.util;

import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ConfigManager {
    private final JavaPlugin plugin;
    private File kingdomsFile;
    private FileConfiguration kingdomsConfig;
    private File weeklyFile;
    private FileConfiguration weeklyConfig;
    private File playerdataFolder;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        kingdomsFile = new File(plugin.getDataFolder(), "kingdoms.yml");
        if (!kingdomsFile.exists()) {
            plugin.saveResource("kingdoms.yml", false); // copy from resources
        }
        kingdomsConfig = YamlConfiguration.loadConfiguration(kingdomsFile);

        playerdataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!playerdataFolder.exists()) playerdataFolder.mkdirs();

        weeklyFile = new File(plugin.getDataFolder(), "weekly.yml");
        if (!weeklyFile.exists()) {
            try {
                weeklyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        weeklyConfig = YamlConfiguration.loadConfiguration(weeklyFile);

        // Zet standaard waarde als die nog niet bestaat
        if (!weeklyConfig.contains("lastReset")) {
            weeklyConfig.set("lastReset", System.currentTimeMillis());
            saveWeeklyConfig();
        }
    }

    public void saveWeeklyConfig() {
        try {
            weeklyConfig.save(weeklyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveKingdoms() {
        saveFile(kingdomsFile, kingdomsConfig);
    }

    public FileConfiguration getPlayerConfig(UUID uuid) {
        File file = new File(playerdataFolder, uuid + ".yml");
        createFileIfMissing(file);
        return YamlConfiguration.loadConfiguration(file);
    }

    public void savePlayerConfig(UUID uuid, FileConfiguration config) {
        saveFile(new File(playerdataFolder, uuid + ".yml"), config);
    }

    private void createFileIfMissing(File file) {
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void saveFile(File file, FileConfiguration config) {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public Map<String, String> getKingdom(String name) {
        List<Map<?, ?>> kingdoms = kingdomsConfig.getMapList("kingdoms");
        for (Map<?, ?> kingdom : kingdoms) {
            if (kingdom.get("name").toString().equalsIgnoreCase(name)) {
                return Map.of(
                        "name", kingdom.get("name").toString(),
                        "color", kingdom.get("color").toString()
                );
            }
        }
        return null; // kingdom not found
    }

    public String getPlayerKingdom(UUID uuid) {
        UserManager userManager = LuckPermsProvider.get().getUserManager();

        try {
            User user = userManager.loadUser(uuid).join();
            if (user != null) {
                for (Node node : user.getNodes()) {
                    String perm = node.getKey();
                    if (perm.startsWith("kingdom.")) {
                        return perm.replace("kingdom.", "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Map<String, String>> getTopAfkPerKingdom(Player player) { // Word atm niet gebruikt
        String kingdom = getPlayerKingdom(player.getUniqueId());
        if (kingdom == null) return new ArrayList<>();

        File[] files = playerdataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return new ArrayList<>();

        List<Map<String, String>> result = new ArrayList<>();

        for (File file : files) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(fileName));

            String playerKingdom = getPlayerKingdom(offlinePlayer.getUniqueId());
            if (playerKingdom == null || !playerKingdom.equalsIgnoreCase(kingdom)) continue;

            int afkTime = config.getInt("afktime");

            result.add(Map.of(
                    "uuid", offlinePlayer.getUniqueId().toString(),
                    "name", offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown",
                    "afkTime", String.valueOf(afkTime)
            ));
        }

        result.sort((a, b) -> Long.compare(
                Long.parseLong(b.get("afkTime")),
                Long.parseLong(a.get("afkTime"))
        ));

        if (result.size() > 10) {
            return result.subList(0, 10);
        }
        return result;
    }

    public List<Map<String, String>> getTopAfk(Player player) {
        File[] files = playerdataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return new ArrayList<>();

        List<Map<String, String>> result = new ArrayList<>();

        for (File file : files) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(fileName));

            int afkTime = config.getInt("afktime");

            result.add(Map.of(
                    "uuid", offlinePlayer.getUniqueId().toString(),
                    "name", offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown",
                    "afkTime", String.valueOf(afkTime)
            ));
        }

        result.sort((a, b) -> Long.compare(
                Long.parseLong(b.get("afkTime")),
                Long.parseLong(a.get("afkTime"))
        ));

        if (result.size() > 10) {
            return result.subList(0, 10);
        }
        return result;
    }

    public List<Map<String, String>> getAllKingdoms() {
        List<Map<?, ?>> kingdoms = kingdomsConfig.getMapList("kingdoms");
        List<Map<String, String>> result = new ArrayList<>();

        for (Map<?, ?> kingdom : kingdoms) {
            result.add(Map.of(
                    "name", kingdom.get("name").toString(),
                    "color", kingdom.get("color").toString()
            ));
        }

        return result;
    }

}
