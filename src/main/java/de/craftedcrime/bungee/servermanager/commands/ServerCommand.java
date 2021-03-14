package de.craftedcrime.bungee.servermanager.commands;
/*
 * Created by ian on 13.03.21
 * Location: de.craftedcrime.bungee.servermanager.commands
 * Created for the project servermanager with the name ServerCommand
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import de.craftedcrime.bungee.servermanager.models.ServerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ServerCommand extends Command {

    private Servermanager servermanager;

    public ServerCommand(Servermanager servermanager) {
        super("server");
        this.servermanager = servermanager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
            if (args.length == 0) {
                // show all the servers the player has access to
                showServersWithAccess(proxiedPlayer);
            } else if (args.length == 1) {

            } else {

            }


        } else {
            sender.sendMessage(new TextComponent("§8| §aServerManager §8| §cThis command is only available as a player."));
        }
    }

    private void showServersWithAccess(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage(new TextComponent("§8-=-=-=-=- §7| §a§lServerManager §r§7| §8-=-=-=-=-"));
        proxiedPlayer.sendMessage(new TextComponent("§6§lLobbies:"));
        TextComponent lobbyStart = new TextComponent("§8 ->§6");
        for (ServerObject serverObject : servermanager.getLobbyMap().values()) {
            if (servermanager.getServerHandler().isAbleToJoin(proxiedPlayer, serverObject)) {
                lobbyStart.addExtra(servermanager.getGeneralUtils().getCommandExecute("§8 | §6" + serverObject.getServerName(), "/goto " + serverObject.getServerName(), "§aGoto that server."));
            }
        }
        proxiedPlayer.sendMessage(lobbyStart);
        proxiedPlayer.sendMessage(new TextComponent("§a§b§a §b"));
        proxiedPlayer.sendMessage(new TextComponent("§6§lOther Servers:"));
        TextComponent otherServersStart = new TextComponent("§8 ->§6");
        for (ServerObject serverObject : servermanager.getNoLobbiesMap().values()) {
            if (servermanager.getServerHandler().isAbleToJoin(proxiedPlayer, serverObject)) {
                otherServersStart.addExtra(servermanager.getGeneralUtils().getCommandExecute("§8 | §6" + serverObject.getServerName(), "/goto " + serverObject.getServerName(), "§aGoto that server."));
            }
        }
        proxiedPlayer.sendMessage(otherServersStart);
        proxiedPlayer.sendMessage(new TextComponent("§8-=-=-=-=- §7| §a§lServerManager §r§7| §8-=-=-=-=-"));
    }
}
