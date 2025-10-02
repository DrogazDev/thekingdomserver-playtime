package nl.drogaz.thekingdomserver_assignment.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ConfigManager {
    private final JavaPlugin plugin;
    private File kingdomsFile;
    private FileConfiguration kingdomsConfig;
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
}
