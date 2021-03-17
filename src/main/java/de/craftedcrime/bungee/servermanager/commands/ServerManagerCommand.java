package de.craftedcrime.bungee.servermanager.commands;
/*
 * Created by ian on 02.03.21
 * Location: de.craftedcrime.bungee.servermanager.commands
 * Created for the project servermanager with the name ServerCommand
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import de.craftedcrime.bungee.servermanager.models.ServerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;

public class ServerManagerCommand extends Command {

    private Servermanager servermanager;

    public ServerManagerCommand(Servermanager servermanager) {
        super("sm");
        this.servermanager = servermanager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
            if (args.length == 0) {
                displayHelp(proxiedPlayer);
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list-all")) {
                    displayAllServerInfo(proxiedPlayer);
                } else {
                    displayHelp(proxiedPlayer);
                }
            } else if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "deactivate":
                        servermanager.getServerHandler().deactivateServer(proxiedPlayer, args[1].toLowerCase());
                        break;
                    case "activate":
                        servermanager.getServerHandler().activateServer(proxiedPlayer, args[1].toLowerCase());
                        break;
                    case "delete":
                        servermanager.getServerHandler().deleteServer(proxiedPlayer, args[1].toLowerCase());
                        break;
                    case "info":
                        servermanager.getServerHandler().showServerInfo(proxiedPlayer, args[1].toLowerCase());
                        break;
                    default:
                        displayHelp(proxiedPlayer);
                        break;
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("setmaxplayer")) {
                    if (servermanager.getGeneralUtils().isNumeric(args[2])) {
                        servermanager.getServerHandler().changeMaxPlayerCount(proxiedPlayer, args[1], Integer.parseInt(args[2]));
                    } else {
                        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cFailed to set max-player count, because §8'§e" + args[2] + "§8' §c(player amount) is not a number!"));
                    }
                } else {
                    displayHelp(proxiedPlayer);
                }
            } else if (args.length == 6) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (servermanager.getGeneralUtils().isNumeric(args[3])) {
                        // Syntax for this command /sm create <servername> <ip> <port> <access level> <lobby true / false>
                        String servername = args[1].toLowerCase();
                        String ip = args[2];
                        int port = Integer.parseInt(args[3]);
                        String accessLevel = args[4].toLowerCase();
                        boolean lobby = args[5].equalsIgnoreCase("true");
                        servermanager.getServerHandler().addServer(proxiedPlayer, new ServerObject(0, servername, ip, port, accessLevel, 20), lobby);
                    } else {
                        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cFailed to add server, because §8'§e" + args[3] + "§8' §c(Port) is not a number!"));
                    }
                }
            } else {
                displayHelp(proxiedPlayer);
            }
        } else {
            sender.sendMessage(new TextComponent("§8| §aServerManager §8| §cUsage from the console is currently not supported."));
        }
    }

    private void displayHelp(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage(new TextComponent("§8-=-=-=-=- §7| §a§lServerManager §r§7| §8-=-=-=-=-"));
        proxiedPlayer.sendMessage(servermanager.getGeneralUtils().getCommandSuggestion("§8- §d/sm create §7<§bservername§7> §7<§bIP-Address§7> §7<§bPort§7> §7<§bAccess Level§7> §7<§blobby true/false§7>", "sm create"
                , "§cSuggest into chat."));
        proxiedPlayer.sendMessage(new TextComponent("§a §b §a §b §8-> §6Creates a new server in the bungeecord management context"));
        proxiedPlayer.sendMessage(servermanager.getGeneralUtils().getCommandSuggestion("§8- §d/sm activate §7<§bservername§7>", "/sm activate", "§cSuggest into chat."));
        proxiedPlayer.sendMessage(new TextComponent("§a §b §a §b §8-> §6Activates a deactivated server, that is already registered."));
        proxiedPlayer.sendMessage(servermanager.getGeneralUtils().getCommandSuggestion("§8- §d/sm deactivate §7<§bservername§7>", "/sm deactivate", "§cSuggest into chat."));
        proxiedPlayer.sendMessage(new TextComponent("§a §b §a §b §8-> §6Deactivates an activated server, that is already registered."));
        proxiedPlayer.sendMessage(servermanager.getGeneralUtils().getCommandSuggestion("§8- §d/sm delete §7<§bservername§7>", "/sm delete", "§cSuggest into chat."));
        proxiedPlayer.sendMessage(new TextComponent("§a §b §a §b §8-> §6Deletes a registered server from context."));
        proxiedPlayer.sendMessage(servermanager.getGeneralUtils().getCommandSuggestion("§8- §d/sm setmaxplayer §7<§bservername§7> §7<§bamount of players§7>", "/sm setmaxplayer", "§cSuggest into chat."));
        proxiedPlayer.sendMessage(new TextComponent("§a §b §a §b §8-> §6Changes the max-player count of a server."));
        proxiedPlayer.sendMessage(new TextComponent("§8-=-=-=-=- §7| §a§lServerManager §r§7| §8-=-=-=-=-"));
    }

    private void displayAllServerInfo(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage(new TextComponent("§8-=-=-=-=- §7| §a§lServerManager §r§7| §8-=-=-=-=-"));
        proxiedPlayer.sendMessage(new TextComponent("§8»»»»»»»»»» §6§lLOBBIES: §8««««««««««"));
        Collection<ServerObject> lobbies = servermanager.getMySQLHandler().loadAllLobbies().values();
        if (lobbies.isEmpty()) {
            proxiedPlayer.sendMessage(new TextComponent("§8| §a §b §a §b §cNo lobbies are registered."));
        } else {
            proxiedPlayer.sendMessage(new TextComponent("§8| §6§l#ID §r§8| §9§lSERVERNAME §r§8| §a§lIP-ADDRESS §r§8| §b§lPORT §r§8| §d§lACCESS LEVEL §r§8| §e§lACTIVE"));
            for (ServerObject serverObject : lobbies) {
                displayServerInfo(proxiedPlayer, serverObject);
            }
        }
        proxiedPlayer.sendMessage(new TextComponent("§8»»»»»»»»»» §6§lOTHER SERVERS: §8««««««««««"));
        Collection<ServerObject> nonLobbies = servermanager.getMySQLHandler().loadAllNonLobbies().values();
        if (nonLobbies.isEmpty()) {
            proxiedPlayer.sendMessage(new TextComponent("§8| §a §b §a §b §cNo servers are registered."));
        } else {
            proxiedPlayer.sendMessage(new TextComponent("§8| §6§l#ID §r§8| §9§lSERVERNAME §r§8| §a§lIP-ADDRESS §r§8| §b§lPORT §r§8| §d§lACCESS LEVEL §r§8| §e§lACTIVE"));
            for (ServerObject serverObject : nonLobbies) {
                displayServerInfo(proxiedPlayer, serverObject);
            }
        }
        proxiedPlayer.sendMessage(new TextComponent("§8-=-=-=-=- §7| §a§lServerManager §r§7| §8-=-=-=-=-"));
    }

    private void displayServerInfo(ProxiedPlayer proxiedPlayer, ServerObject serverObject) {
        proxiedPlayer.sendMessage(new TextComponent("§8| §6§l" + serverObject.getServer_id() + " §r§8| §9§l" + serverObject.getServerName() + " §r§8| §a§l" + serverObject.getIpAddress() +
                " §r§8| §d§l" + serverObject.getAccessType() + " §r§8| §e§l" + serverObject.isActive()));
    }
}
