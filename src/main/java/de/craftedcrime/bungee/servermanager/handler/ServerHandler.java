package de.craftedcrime.bungee.servermanager.handler;
/*
 * Created by ian on 25.02.21
 * Location: de.craftedcrime.bungee.servermanager.servermanager.handler
 * Created for the project servermanager with the name ServerHandler
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import de.craftedcrime.infrastructure.servermanager.middleware.ServerObject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ServerHandler {

    private Servermanager servermanager;

    public ServerHandler(Servermanager servermanager) {
        this.servermanager = servermanager;
    }

    /**
     * add a new server to the management of ServerManager
     *
     * @param proxiedPlayer player who's adding a new server
     * @param serverObject  object that contains all information to create a new managed server
     * @param lobby         whether the new server is a lobby or not
     */
    public void addServer(ProxiedPlayer proxiedPlayer, ServerObject serverObject, boolean lobby) {
        if (servermanager.getMySQLHandler().serverExists(serverObject.getServerName())) {
            if (serverAlreadyRegistered(serverObject.getServerName())) {
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cA server with this name is §l§calready §r§cregistered and §l§cactive§r§c."));
            } else {
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cA server with this name is §l§calready §r§cregistered, but §e§lINACTIVE§r§c."));
            }
            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §eYou can lookup all registered servers via: §6§l/sm list-all "));
            return;
        }
        if (!servermanager.getIpValidationUtils().isValidInet4Address(serverObject.getIpAddress())) {
            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cThe IP-Address §8(§7'§b" + serverObject.getIpAddress() + "§7'§8)§c you've entered isn't valid."));
            return;
        }
        boolean restricted = true;
        if (serverObject.getAccessType().equalsIgnoreCase("all")) restricted = false;
        ServerInfo serverInfo = servermanager.getProxy().constructServerInfo(serverObject.getServerName(), new InetSocketAddress(serverObject.getIpAddress(), serverObject.getPort()), "Just another server", restricted);
        servermanager.getProxy().getServers().put(serverObject.getServerName(), serverInfo);
        servermanager.getMySQLHandler().addServer(serverObject, lobby);
        if (lobby) {
            servermanager.getLobbyMap().put(serverObject.getServerName(), serverObject);
        } else {
            servermanager.getNoLobbiesMap().put(serverObject.getServerName(), serverObject);
        }
        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've added the server §7'§e" + serverObject.getServerName() + "§7' §a with the address §7'§e" + serverObject.getIpAddress() + ":" + serverObject.getPort() + "§7'§a to the context!"));
        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've added the server with the access level §7'§e" + serverObject.getAccessType() + "§7'§a."));
        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aThe max-player amount for this server has been set to §7'§e20§7'§a. \n§a      §aYou can change this via: §d/sm setmaxplayer §7<§bservername§7> §7<§bamount of players§7>"));
        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aSee more information via: §6/sm info §c" + serverObject.getServerName()));
    }

    /**
     * deactivates a server that is managed in ServerManager
     *
     * @param proxiedPlayer player who's deactivating a server
     * @param servername    server that's going to be deactivated
     */
    public void deactivateServer(ProxiedPlayer proxiedPlayer, String servername) {
        if (serverAlreadyRegistered(servername) && serverIsOrchestrated(servername)) {
            if (sendToFallbackServer(servername)) {
                servermanager.getProxy().getServers().remove(servername);
                servermanager.getMySQLHandler().deactivateServer(servername, serverIsLobby(servername));
                if (serverIsLobby(servername)) {
                    servermanager.getLobbyMap().remove(servername);
                    proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've successfully deactivated the lobby §7'§e" + servername + "§7'§a."));
                } else {
                    servermanager.getNoLobbiesMap().remove(servername);
                    proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've successfully server the lobby §7'§e" + servername + "§7'§a."));
                }
            } else {
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cFailed to deactivate the server §7'§e" + servername + "§7', §cbecause no other server is available as a fallback server. \n" +
                        "§eHas at least one server the access level 'all'. See the tutorial for more help: URL TBA"));
            }
        } else {
            if (servermanager.getMySQLHandler().serverExists(servername)) {
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cThis server can't be deactivated because it's already inactive."));
            } else {
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cThis server can't be deactivated because it's not managed by this plugin."));
            }
        }
    }

    /**
     * activate a managed server
     *
     * @param proxiedPlayer who's trying to activate a server
     * @param servername    server you want to activate
     */
    public void activateServer(ProxiedPlayer proxiedPlayer, String servername) {
        // check if the server exists but isn't orchestrated
        if (servermanager.getMySQLHandler().serverExists(servername)) {
            if (!serverIsOrchestrated(servername) && !serverAlreadyRegistered(servername)) {
                if (servermanager.getMySQLHandler().activateServer(servername, servermanager.getMySQLHandler().isLobby(servername))) {
                    proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've successfully activated the server §7'§e" + servername + "§7'§a."));
                } else {
                    proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cAn error occurred, while you tried to activate the server. §eIs the server already active? Is the database connection okay?" +
                            " Maybe a reboot helps."));
                }
            } else {
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cThis server can't be activated, because it's already active."));
            }
        } else {
            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cThis server can't be deactivated because it's not orchestrated."));
        }

    }


    /**
     * loads all lobbies and registers them in BungeeCord
     *
     * @return returns all lobbies in a hashmap
     */
    public HashMap<String, ServerObject> initAllLobbies() {
        HashMap<String, ServerObject> lobbies = servermanager.getMySQLHandler().loadAllActiveLobbies();
        for (ServerObject lobby : lobbies.values()) {
            ServerInfo lo = servermanager.getProxy().constructServerInfo(lobby.getServerName(), new InetSocketAddress(lobby.getIpAddress(), lobby.getPort()), "Lobby:" + lobby.getServerName(), !lobby.getAccessType().equalsIgnoreCase("all"));
            servermanager.getProxy().getServers().put(lobby.getServerName(), lo);
        }
        return lobbies;
    }

    /**
     * loads all non lobbies and registers them in BungeeCord
     *
     * @return returns all non lobbies in a hashmap
     */
    public HashMap<String, ServerObject> initAllNonLobbies() {
        HashMap<String, ServerObject> nonLobbies = servermanager.getMySQLHandler().loadAllActiveNonLobbies();
        for (ServerObject lobby : nonLobbies.values()) {
            ServerInfo lo = servermanager.getProxy().constructServerInfo(lobby.getServerName(), new InetSocketAddress(lobby.getIpAddress(), lobby.getPort()), "Server(NonLobby):" + lobby.getServerName(), !lobby.getAccessType().equalsIgnoreCase("all"));
            servermanager.getProxy().getServers().put(lobby.getServerName(), lo);
        }
        return nonLobbies;
    }

    /**
     * checks if a server is a lobby or not
     *
     * @param servername name of the server you want to check
     * @return whether the server is a lobby or not
     */
    public boolean serverIsLobby(String servername) {
        return servermanager.getLobbyMap().containsKey(servername);
    }

    /**
     * check if a server is registered in Bungeecord
     *
     * @param servername name of the server you want to check
     * @return whether the server is registered in BungeeCord or not
     */
    public boolean serverAlreadyRegistered(String servername) {
        return servermanager.getProxy().getServers().containsKey(servername);
    }

    /**
     * checks if a server is orchestrated, means is registered in ServerManager
     *
     * @param servername name of the server you want to check
     * @return whether the server is orchestrated by ServerManager or not
     */
    public boolean serverIsOrchestrated(String servername) {
        return servermanager.getNoLobbiesMap().containsKey(servername) || servermanager.getLobbyMap().containsKey(servername);
    }

    /**
     * get the server info from the database
     *
     * @param servername name of the server
     * @return null if not exists or an error occurred, otherwise returns database server info object
     */
    public ServerObject getServerInformation(String servername) {
        ServerObject serverObject = null;
        if (serverAlreadyRegistered(servername)) {
            if (serverIsLobby(servername)) {
                serverObject = servermanager.getLobbyMap().get(servername);
            } else {
                serverObject = servermanager.getNoLobbiesMap().get(servername);
            }
        }
        return serverObject;
    }

    /**
     * delete a managed server
     *
     * @param proxiedPlayer who's deleting the server
     * @param servername    servername you want to delete
     */
    public void deleteServer(ProxiedPlayer proxiedPlayer, String servername) {
        deleteServer(proxiedPlayer, servername, servermanager.getLobbyMap().containsKey(servername));
    }

    public void deleteServer(ProxiedPlayer proxiedPlayer, String servername, boolean isLobby) {
        // general functionality of this method
        if (serverAlreadyRegistered(servername)) {
            if (serverIsOrchestrated(servername)) {

                if (isLobby) {
                    servermanager.getLogger().log(Level.INFO, "Attention, you're about to delete a lobby!");
                    if (servermanager.getLobbyMap().size() == 1) {
                        if (proxiedPlayer != null)
                            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cFailed to delete the lobby with the name §7'§e" + servername + "§7'§c, because" +
                                    " no other lobby has been defined. §bYou have to define another lobby to delete this one."));
                    } else {
                        if (sendToFallbackServer(servername)) {
                            servermanager.getProxy().getServers().remove(servername);
                            servermanager.getLobbyMap().remove(servername);
                            servermanager.getMySQLHandler().deleteServer(servername, true);
                            if (proxiedPlayer != null)
                                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've successfully deleted the lobby §7'§e" + servername + "§7' §afrom " +
                                        "the context."));
                        } else {
                            if (proxiedPlayer != null) {
                                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cFailed to delete the server! This could be, because no other server can be a default fallback server. \n" +
                                        "§8| §aServerManager §8| §eHas at least one server the access level 'all'? See the tutorial for more help: URL TBA"));
                            }
                        }
                    }
                } else {
                    if (sendToFallbackServer(servername)) {
                        servermanager.getProxy().getServers().remove(servername);
                        servermanager.getNoLobbiesMap().remove(servername);
                        servermanager.getMySQLHandler().deleteServer(servername, false);
                        if (proxiedPlayer != null)
                            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've successfully deleted the server §7'§e" + servername + "§7' §afrom the context."));
                    } else {
                        if (proxiedPlayer != null)
                            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cFailed to delete the server! This could be, because no other server can be a default fallback server. \n" +
                                    "§8| §aServerManager §8| §eHas at least one server the access level 'all'? See the tutorial for more help: URL TBA"));
                    }
                }
            } else {
                if (proxiedPlayer != null)
                    proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cFailed to delete this server, because the server is not orchestrated by the ServerManager " +
                            "plugin, see more information on how to orchestrate a server under: URL TBA"));
            }
        } else {
            if (proxiedPlayer != null) proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cFailed to delete this server, because this server is not registered."));
        }
    }

    /**
     * send all players from the server to a random fallback server, all players that don't fit onto that fallback are going to be kicked from the server
     *
     * @param fromServername name of the server you want all players to be sent to a fallback
     * @return whether it worked or not (essentially if there is a fallback available or not)
     */
    public boolean sendToFallbackServer(String fromServername) {
        // TODO: check if server is full
        ServerObject fallBackServerName = null;
        if (servermanager.getLobbyMap().isEmpty()) {
            for (ServerObject so : servermanager.getNoLobbiesMap().values()) {
                if (!so.getServerName().equalsIgnoreCase(fromServername) && so.getAccessType().equalsIgnoreCase("all")) {
                    if (so.getMaxPlayers() > servermanager.getProxy().getServerInfo(so.getServerName()).getPlayers().size()) {
                        fallBackServerName = so;
                        break;
                    }
                }
            }
        }
        if (fallBackServerName == null) {
            for (ServerObject so : servermanager.getLobbyMap().values()) {
                if (!so.getServerName().equalsIgnoreCase(fromServername) && so.getAccessType().equalsIgnoreCase("all")) {
                    if (so.getMaxPlayers() > servermanager.getProxy().getServerInfo(so.getServerName()).getPlayers().size()) {
                        fallBackServerName = so;
                        break;
                    }
                }
            }
        }
        if (fallBackServerName == null) {
            return false;
        } else {
            ServerInfo targetServer = servermanager.getProxy().getServers().getOrDefault(fallBackServerName.getServerName(), null);
            if (targetServer != null) {
                for (ProxiedPlayer pp : servermanager.getProxy().getServerInfo(fromServername).getPlayers()) {
                    if (fallBackServerName.getMaxPlayers() > targetServer.getPlayers().size()) {
                        pp.connect(targetServer);
                        pp.sendMessage(new TextComponent("§8| §aServerManager §8| §eYou've been sent to the default fallback server, because the server you've been connected to has been deleted."));
                    } else {
                        pp.disconnect(new TextComponent("§8| §aServerManager §8| §cThe fallback-server was full. Please try again!"));
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * sends a player to a server (doesn't have to be managed via ServerManager)
     *
     * @param proxiedPlayer who's going to be sent to a server
     * @param serverName    name of the server the player wants to be sent to
     */
    public void sendPlayerToServer(ProxiedPlayer proxiedPlayer, String serverName) {
        if (proxiedPlayer != null)
            if (serverAlreadyRegistered(serverName)) {
                if (serverIsOrchestrated(serverName)) {
                    if (!proxiedPlayer.getServer().getInfo().getName().equalsIgnoreCase(serverName)) {
                        ServerObject serverObject = getServerInformation(serverName);
                        if (serverObject != null) {
                            if (isAbleToJoin(proxiedPlayer, serverObject)) {
                                ServerInfo targetServer = servermanager.getProxy().getServerInfo(serverName);
                                if (targetServer != null) {
                                    if (serverObject.getMaxPlayers() > targetServer.getPlayers().size()) {
                                        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou're moved to §7'§e" + serverName + "§7'§a."));
                                        proxiedPlayer.connect(servermanager.getProxy().getServerInfo(serverName));
                                    } else {
                                        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cSorry, but the server you're trying to connect to is full. :/"));
                                    }
                                } else {
                                    proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cSorry, but we weren't able to find this server."));
                                }
                            } else {
                                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cYou don't have the permission to access the server."));
                            }
                        } else {
                            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cThe server you've requested to be sent to can't be joined. Server information not available."));
                        }
                    } else {
                        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cYou're already connected to this server."));
                    }
                }
            } else {
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cAttention! You are joining a server that in not managed by ServerManager!"));
                proxiedPlayer.connect(servermanager.getProxy().getServerInfo(serverName));
            }

    }

    /**
     * @param proxiedPlayer who's going to be checked
     * @param serverObject  server that sets the conditions
     * @return whether the player is allowed to join or not
     */
    public boolean isAbleToJoin(ProxiedPlayer proxiedPlayer, ServerObject serverObject) {
        return proxiedPlayer.hasPermission("server.access." + serverObject.getAccessType()) || proxiedPlayer.hasPermission("server.join." + serverObject.getServerName()) ||
                proxiedPlayer.hasPermission("serverm.admin") || proxiedPlayer.hasPermission("serverm.joinall") || serverObject.getAccessType().equalsIgnoreCase("all");
    }

    /**
     * send a specific player to a hub
     *
     * @param proxiedPlayer who's going to be sent to a hub
     * @return whether it worked or not
     */
    public boolean sendToHub(ProxiedPlayer proxiedPlayer) {
        boolean ready = false;
        if (proxiedPlayer.getServer() != null && isAlreadyOnLobby(proxiedPlayer)) {
            return true;
        }
        for (ServerObject so : servermanager.getLobbyMap().values()) {
            System.out.println("Inspecting server: " + so.getServerName());
            if (isAbleToJoin(proxiedPlayer, so)) {
                System.out.println("is Able to join");
                if (so.getMaxPlayers() > servermanager.getProxy().getServerInfo(so.getServerName()).getPlayers().size()) {
                    System.out.println("Enough space to join!");
                    proxiedPlayer.connect(servermanager.getProxy().getServerInfo(so.getServerName()));
                    proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've been moved to the hub §7'§e" + so.getServerName() + "§7'§a."));
                    ready = true;
                    break;
                }
            }
        }
        return ready;
    }

    public String getJoinableHub(ProxiedPlayer proxiedPlayer) {
        String hub = null;
        for (ServerObject so : servermanager.getLobbyMap().values()) {
            System.out.println("Inspecting server: " + so.getServerName());
            if (isAbleToJoin(proxiedPlayer, so)) {
                System.out.println("is Able to join");
                if (so.getMaxPlayers() > servermanager.getProxy().getServerInfo(so.getServerName()).getPlayers().size()) {
                    System.out.println("Enough space to join!");
                    hub = so.getServerName();
                    break;
                }
            }
        }
        return hub;
    }

    /**
     * send a specific player to a hub except from one specific hub
     *
     * @param proxiedPlayer who's going to be sent to a hub
     * @param exceptServer  the server that's going to be ignored
     * @return whether it worked or not
     */
    public String getReconnectServer(ProxiedPlayer proxiedPlayer, String exceptServer) {
        Map<String, ServerObject> copy = new HashMap<>(servermanager.getLobbyMap());
        copy.remove(exceptServer);
        for (ServerObject so : copy.values()) {
            if (isAbleToJoin(proxiedPlayer, so)) {
                if (so.getMaxPlayers() > servermanager.getProxy().getServerInfo(so.getServerName()).getPlayers().size()) {
                    return so.getServerName();
                }
            }
        }
        return null;
    }

    /**
     * change the max player account to allow more or less players to join this server
     *
     * @param proxiedPlayer who's trying to change the max-player amount
     * @param servername    server where he's trying to change it
     * @param playerCount   new player count
     */
    public void changeMaxPlayerCount(ProxiedPlayer proxiedPlayer, String servername, int playerCount) {
        if (serverAlreadyRegistered(servername)) {
            if (playerCount > 0) {
                // server is registered and new player count is > 0
                if (serverIsOrchestrated(servername)) {
                    if (servermanager.getMySQLHandler().changePlayerCount(servername, playerCount)) {
                        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've successfully changed the max-player amount for the server §7'§e" + servername + "§7'§a to §7'§e" + playerCount + "§7'§a."));
                    } else {
                        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cSorry, but we couldn't change the max-player amount for server §7'§e" + servername + "§7'§c, look into the BungeeCord console for further information."));
                    }
                } else {
                    proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cTo edit the max-player count of a server, this server has to be active. " +
                            "\n§a      §cThe server §7'§e" + servername + "§7'§c is offline."));
                }
            } else {
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cThe max-player count has to be positive and greater than 0."));
            }
        } else {
            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cSorry, but we couldn't find a server with this name."));
        }
    }

    /**
     * checks if a player is already on a lobby server
     *
     * @param proxiedPlayer who's going to be checked
     * @return whether the player is already on a lobby or not
     */
    public boolean isAlreadyOnLobby(ProxiedPlayer proxiedPlayer) {
        return serverIsLobby(proxiedPlayer.getServer().getInfo().getName());
    }

    /**
     * show's you a wonderful presentation of information about a server
     *
     * @param proxiedPlayer who's going to see the information
     * @param servername    server you want to get some information from
     */
    public void showServerInfo(ProxiedPlayer proxiedPlayer, String servername) {
        boolean registeredInBungee; // server is loaded in BungeeCord, may be not registered in database
        boolean registeredInServerManager; // server is registered in database
        ServerObject serverObject; // server info object from the database
        serverObject = servermanager.getMySQLHandler().getServerObjectByName(servername); // get server info object from the database
        registeredInBungee = serverAlreadyRegistered(servername); // get if server is loaded in bungeecord
        registeredInServerManager = (serverObject != null); // get if server is registered in the database (server object null or not)
        ServerInfo serverInfo = null;
        System.out.println("Registered in bungee " + registeredInBungee + " | registered in server manager " + registeredInServerManager);
        if (registeredInBungee) serverInfo = servermanager.getProxy().getServerInfo(servername);
        if (registeredInBungee || registeredInServerManager) { // only do if server is registered in BungeeCord and/or in the database
            proxiedPlayer.sendMessage(new TextComponent("§8-=-=-=-=- §7| §a§lServerManager §r§7| §8-=-=-=-=-")); // Header of message
            proxiedPlayer.sendMessage(new TextComponent("§dServername: §e" + servername));
            if (registeredInServerManager) proxiedPlayer.sendMessage(new TextComponent("§dServer-ID: §e" + serverObject.getServerId()));
            if (registeredInBungee) {
                proxiedPlayer.sendMessage(new TextComponent("§a⬤ §7-> §aThis server is registered in BungeeCord."));
            } else {
                if (registeredInServerManager) proxiedPlayer.sendMessage(new TextComponent("§c⬤ §7-> §cThis server is §ndeactivated§r§c."));
            }
            if (registeredInServerManager) {
                proxiedPlayer.sendMessage(new TextComponent("§a⬤ §7-> §aThis server is managed via ServerManager."));
            } else {
                proxiedPlayer.sendMessage(new TextComponent("§c⬤ §7-> §cThis server is §nnot§r§c managed via ServerManager."));
            }
            if (serverInfo != null) {
                proxiedPlayer.sendMessage(new TextComponent("§dOnline players: §e" + serverInfo.getPlayers().size()));
                if (registeredInServerManager) proxiedPlayer.sendMessage(new TextComponent("§dMax. Players: §e" + serverObject.getMaxPlayers()));
                proxiedPlayer.sendMessage(new TextComponent("§dServer address: §e" + serverInfo.getSocketAddress().toString().replaceAll("/", "")));
            }
            if (registeredInServerManager) {
                proxiedPlayer.sendMessage(new TextComponent("§dAccess-Type: §e" + serverObject.getAccessType()));
            }
            proxiedPlayer.sendMessage(new TextComponent("§8-=-=-=-=- §7| §a§lServerManager §r§7| §8-=-=-=-=-")); // Footer of message
        } else {
            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cSorry, but we couldn't find a server with this name."));
        }
    }


}
