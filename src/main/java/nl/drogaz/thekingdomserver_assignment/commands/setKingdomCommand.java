package nl.drogaz.thekingdomserver_assignment.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.NodeType;
import nl.drogaz.thekingdomserver_assignment.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class setKingdomCommand implements BasicCommand {

    Main plugin = Main.getPlugin(Main.class);

    @Override
    public void execute(CommandSourceStack source, String args[]) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();

        Player player = (Player) source.getExecutor();

        if (args.length == 0 || args.length == 1) {
            player.sendRichMessage("<red>Ongeldige optie. Gebruik <yellow>/setkingdom <player> <kingdom>");
            return;
        }

        if (args.length == 2) {
//            Denkbeeldig perms check
            String targetPlayer = args[0];
            String targetKingdom = args[1];

            try {
                plugin.getConfigManager().getKingdom(targetKingdom).get("name");
            } catch (Exception e) {
                player.sendRichMessage("<red>Dit kingdom bestaat niet!");
                return;
            }

            User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                for (Node node : user.getNodes()) {
                    String perm = node.getKey();
                    if (perm.startsWith("kingdom.")) {
                        LuckPermsProvider.get().getUserManager().modifyUser(player.getUniqueId(), p -> {
                            user.data().remove(Node.builder(perm).build());
                        });
                    }
                }
            }

            LuckPermsProvider.get().getUserManager().modifyUser(player.getUniqueId(), p -> {
                user.data().add(Node.builder("kingdom." + targetKingdom).build());
            });

            player.sendRichMessage("<gold>je hebt <yellow>" + targetPlayer + "</yellow> naar <yellow>" + targetKingdom + "</yellow> gezet</gold>");
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
