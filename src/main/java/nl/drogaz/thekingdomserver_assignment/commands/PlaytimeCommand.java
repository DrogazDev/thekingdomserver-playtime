package nl.drogaz.thekingdomserver_assignment.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nl.drogaz.thekingdomserver_assignment.Main;
import nl.drogaz.thekingdomserver_assignment.helper.TimeFormatHelper;
import nl.drogaz.thekingdomserver_assignment.listeners.PlaytimeListener;
import nl.drogaz.thekingdomserver_assignment.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class PlaytimeCommand implements BasicCommand {

    Main plugin = Main.getPlugin(Main.class);

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();

        Player player = (Player) source.getExecutor();

        if (args.length == 0) {
            try {
                String kingdom = "eo";
                String color = plugin.getConfigManager().getKingdom(kingdom).get("color");
                String kingdomName = plugin.getConfigManager().getKingdom(kingdom).get("name");
                String displayName = kingdomName.substring(0, 1).toUpperCase() + kingdomName.substring(1);

                // Playtime
                int pSessionTime = plugin.getPlaytimeListener().getPlayerPlayTime(player.getUniqueId());
                int pSavedTime = plugin.getConfigManager().getPlayerConfig(player.getUniqueId()).getInt("playtime");
                int pTotalTime = pSessionTime + pSavedTime;
                String playtime = TimeFormatHelper.getTime(pTotalTime);

                // AFK Time
                int aSessionTime = plugin.getPlaytimeListener().getPlayerAfkTime(player.getUniqueId());
                int aSavedTime = plugin.getConfigManager().getPlayerConfig(player.getUniqueId()).getInt("afktime");
                int aTotalTime = aSessionTime + aSavedTime;
                String afktime = TimeFormatHelper.getTime(aTotalTime);

                player.sendRichMessage("<dark_gray><st>+----------------***----------------+</st></dark_gray>");
                player.sendRichMessage("");
                player.sendRichMessage("<gold>Je playtime is <yellow>" + playtime + "</yellow></gold>");
                player.sendRichMessage("<gold>Je AFK time is <yellow>" + afktime + "</yellow></gold>");
                player.sendRichMessage("<gold>Je staat <yellow>1ste</yellow> van het kingdom " + color + displayName + "</gold>");
                player.sendRichMessage("");
                player.sendRichMessage("<dark_gray><st>+----------------***----------------+</st></dark_gray>");
            } catch (Exception e) {
                player.sendRichMessage("<red>Er is een fout opgetreden tijdens het ophalen van je kingdom, meld dit bij staff.");
            }
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

}
