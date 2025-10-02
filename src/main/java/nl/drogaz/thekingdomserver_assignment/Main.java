package nl.drogaz.thekingdomserver_assignment;

import lombok.Getter;
import lombok.Setter;
import nl.drogaz.thekingdomserver_assignment.commands.PlaytimeCommand;
import nl.drogaz.thekingdomserver_assignment.util.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    @Setter
    private ConfigManager configManager;

    @Override
    public void onEnable() {

        configManager = new ConfigManager(this);
        configManager.setup();

        registerCommand("playtime", new PlaytimeCommand());
    }

    @Override
    public void onDisable() {
        configManager.saveKingdoms();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
