package nl.drogaz.thekingdomserver_assignment;

import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import nl.drogaz.thekingdomserver_assignment.commands.PlaytimeCommand;
import nl.drogaz.thekingdomserver_assignment.commands.setKingdomCommand;
import nl.drogaz.thekingdomserver_assignment.listeners.PlaytimeListener;
import nl.drogaz.thekingdomserver_assignment.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    @Setter
    private ConfigManager configManager;

    @Getter
    private PlaytimeListener playtimeListener;

    @Override
    public void onEnable() {

        configManager = new ConfigManager(this);
        configManager.setup();

        playtimeListener = new PlaytimeListener();
        getServer().getPluginManager().registerEvents(playtimeListener, this);

        getConfigManager().getWeeklyConfig().set("lastReset", System.currentTimeMillis());
        getConfigManager().saveWeeklyConfig();

        registerCommand("playtime", new PlaytimeCommand());
        registerCommand("setkingdom", new setKingdomCommand());

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms api = provider.getProvider();
        }
    }

    @Override
    public void onDisable() {
        configManager.saveKingdoms();
    }

}
