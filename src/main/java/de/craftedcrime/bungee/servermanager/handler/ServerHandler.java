package de.craftedcrime.bungee.servermanager.handler;
/*
 * Created by ian on 25.02.21
 * Location: de.craftedcrime.bungee.servermanager.servermanager.handler
 * Created for the project servermanager with the name ServerHandler
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import de.craftedcrime.bungee.servermanager.models.ServerObject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;

public class ServerHandler {

    private Servermanager servermanager;

    public ServerHandler(Servermanager servermanager) {
        this.servermanager = servermanager;
    }

    public void addServer(ProxiedPlayer proxiedPlayer, ServerObject serverObject) {
        if (serverAlreadyRegistered(serverObject.getServerName())) {
            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cA server with this name is §l§calready §r§cregistered. "));
            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cYou can lookup all registered servers via: §6/sm listall "));
            return;
        }
        ServerInfo serverInfo = servermanager.getProxy().constructServerInfo(serverObject.getServerName(), new InetSocketAddress(serverObject.getIpAddress(), serverObject.getPort()), "Just another server", serverObject.isRestrictedAccess());
        servermanager.getProxy().getServers().put(serverObject.getServerName(), serverInfo);
        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou've added the server §7'" + serverObject.getServerName() + "§7' §a with the address §7' §b" + serverObject.getIpAddress() + ":" + serverObject.getPort() + " §7' to the context!"));
        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aSee more information via: §6/sm info §c" + serverObject.getServerName()));
    }

    public boolean serverAlreadyRegistered(String servername) {
        return servermanager.getProxy().getServers().containsKey(servername);
    }

}
