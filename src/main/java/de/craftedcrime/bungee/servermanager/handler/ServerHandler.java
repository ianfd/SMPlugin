package de.craftedcrime.bungee.servermanager.handler;
/*
 * Created by ian on 25.02.21
 * Location: de.craftedcrime.bungee.servermanager.servermanager.handler
 * Created for the project servermanager with the name ServerHandler
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import de.craftedcrime.bungee.servermanager.models.ServerObject;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class ServerHandler {

    private Servermanager servermanager;

    public ServerHandler(Servermanager servermanager) {
        this.servermanager = servermanager;
    }

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
        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've added the server §7'" + serverObject.getServerName() + "§7' §a with the address §7' §b" + serverObject.getIpAddress() + ":" + serverObject.getPort() + " §7' to the context!"));
        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aSee more information via: §6/sm info §c" + serverObject.getServerName()));
    }

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


    public HashMap<String, ServerObject> initAllLobbies() {
        HashMap<String, ServerObject> lobbies = servermanager.getMySQLHandler().loadAllActiveLobbies();
        for (ServerObject lobby : lobbies.values()) {
            ServerInfo lo = servermanager.getProxy().constructServerInfo(lobby.getServerName(), new InetSocketAddress(lobby.getIpAddress(), lobby.getPort()), "Lobby:" + lobby.getServerName(), !lobby.getAccessType().equalsIgnoreCase("all"));
            servermanager.getProxy().getServers().put(lobby.getServerName(), lo);
        }
        return lobbies;
    }

    public HashMap<String, ServerObject> initAllNonLobbies() {
        HashMap<String, ServerObject> nonLobbies = servermanager.getMySQLHandler().loadAllActiveNonLobbies();
        for (ServerObject lobby : nonLobbies.values()) {
            ServerInfo lo = servermanager.getProxy().constructServerInfo(lobby.getServerName(), new InetSocketAddress(lobby.getIpAddress(), lobby.getPort()), "Server(NonLobby):" + lobby.getServerName(), !lobby.getAccessType().equalsIgnoreCase("all"));
            servermanager.getProxy().getServers().put(lobby.getServerName(), lo);
        }
        return nonLobbies;
    }

    public boolean serverIsLobby(String servername) {
        return servermanager.getLobbyMap().containsKey(servername);
    }


    public boolean serverAlreadyRegistered(String servername) {
        return servermanager.getProxy().getServers().containsKey(servername);
    }

    // checks if the server is currently orchestrated (active)
    public boolean serverIsOrchestrated(String servername) {
        return servermanager.getNoLobbiesMap().containsKey(servername) || servermanager.getLobbyMap().containsKey(servername);
    }

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

    public void deleteServer(ProxiedPlayer proxiedPlayer, String servername) {
        // general functionality of this method
        if (serverAlreadyRegistered(servername)) {
            if (serverIsOrchestrated(servername)) {
                boolean isLobby = servermanager.getLobbyMap().containsKey(servername);
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

    public boolean sendToFallbackServer(String fromServername) {
        // TODO: check if server is full
        int serverSize = servermanager.getProxy().getServerInfo(fromServername).getPlayers().size();
        String fallBackServerName = null;
        if (servermanager.getLobbyMap().isEmpty()) {
            for (ServerObject so : servermanager.getNoLobbiesMap().values()) {
                if (!so.getServerName().equalsIgnoreCase(fromServername) && so.getAccessType().equalsIgnoreCase("all")) {
                    AtomicInteger playerCount = new AtomicInteger();
                    AtomicInteger maxPlayerCount = new AtomicInteger();
                    servermanager.getProxy().getServerInfo(so.getServerName()).ping((result, error) -> {
                        playerCount.set(result.getPlayers().getOnline());
                        maxPlayerCount.set(result.getPlayers().getMax());
                    });
                    if (maxPlayerCount.get() > playerCount.get()) {
                        fallBackServerName = so.getServerName();
                        break;
                    }
                }
            }
        }
        if (fallBackServerName == null) {
            for (ServerObject so : servermanager.getLobbyMap().values()) {
                if (!so.getServerName().equalsIgnoreCase(fromServername) && so.getAccessType().equalsIgnoreCase("all")) {
                    AtomicInteger playerCount = new AtomicInteger();
                    AtomicInteger maxPlayerCount = new AtomicInteger();
                    servermanager.getProxy().getServerInfo(so.getServerName()).ping((result, error) -> {
                        playerCount.set(result.getPlayers().getOnline());
                        maxPlayerCount.set(result.getPlayers().getMax());
                    });
                    if (maxPlayerCount.get() > playerCount.get()) {
                        fallBackServerName = so.getServerName();
                        break;
                    }
                }
            }
        }
        if (fallBackServerName == null) {
            return false;
        } else {
            ServerInfo targetServer = servermanager.getProxy().getServers().getOrDefault(fallBackServerName, null);
            if (targetServer != null) {
                for (ProxiedPlayer pp : servermanager.getProxy().getServerInfo(fromServername).getPlayers()) {
                    targetServer.ping((result, error) -> {
                        if (result.getPlayers().getMax() > result.getPlayers().getOnline()) {
                            pp.connect(targetServer);
                            pp.sendMessage(new TextComponent("§8| §aServerManager §8| §eYou've been sent to the default fallback server, because the server you've been connected to has been deleted."));
                        } else {
                            pp.disconnect(new TextComponent("§8| §aServerManager §8| §cThe fallback-server was full. Please try again!"));
                        }
                    });
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public void sendPlayerToServer(ProxiedPlayer proxiedPlayer, String serverName) {
        if (proxiedPlayer != null && serverAlreadyRegistered(serverName)) {
            ServerObject serverObject = getServerInformation(serverName);
            if (serverObject != null) {
                if (isAbleToJoin(proxiedPlayer, serverObject)) {
                    // TODO: check if server is full and display appropriate message
                    ServerInfo targetServer = servermanager.getProxy().getServerInfo(serverName);
                    System.out.println("target server " + (targetServer != null));
                    System.out.println(targetServer);
                    if (targetServer != null) {
                        targetServer.ping(new Callback<ServerPing>() {
                            @Override
                            public void done(ServerPing result, Throwable error) {
                                //if (error != null) {
                                if (result != null && error == null) {
                                    if (result.getPlayers().getMax() > result.getPlayers().getOnline()) {
                                        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou're moved to §7'§e" + serverName + "§7'§a."));
                                        proxiedPlayer.connect(servermanager.getProxy().getServerInfo(serverName));
                                    } else {
                                        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cSorry, but the server you're trying to connect to is full. :/"));
                                    }
                                } else {
                                    System.out.println("result is null");
                                }
                            }
                        });
                    } else {
                        System.out.println("moved alone");
                        proxiedPlayer.connect(servermanager.getProxy().getServerInfo(serverName));
                    }

                } else {
                    proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cYou don't have the permission to access the server."));
                }
            } else {
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cThe server you've requested to be sent to can't be joined. Server information not available."));
            }
        }
    }

    public boolean isAbleToJoin(ProxiedPlayer proxiedPlayer, ServerObject serverObject) {
        return proxiedPlayer.hasPermission("server.access." + serverObject.getAccessType()) || proxiedPlayer.hasPermission("server.join." + serverObject.getServerName()) ||
                proxiedPlayer.hasPermission("serverm.admin") || proxiedPlayer.hasPermission("serverm.joinall") || serverObject.getAccessType().equalsIgnoreCase("all");
    }

    public boolean sendToHub(ProxiedPlayer proxiedPlayer) {
        AtomicBoolean ready = new AtomicBoolean();
        ready.set(false);
        for (ServerObject so : servermanager.getLobbyMap().values()) {
            if (isAbleToJoin(proxiedPlayer, so)) {
                servermanager.getProxy().getServerInfo(so.getServerName()).ping((result, error) -> {
                    if (result.getPlayers().getMax() >= (result.getPlayers().getOnline()+1)) {
                        proxiedPlayer.connect(servermanager.getProxy().getServerInfo(so.getServerName()));
                        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've been moved to the hub §7'§e" + so.getServerName() + "§7'§a."));
                    }
                });
                if (ready.get()) break;
            }
        }
        return ready.get();
    }


}
