package de.craftedcrime.bungee.servermanager.listeners;
/*
 * Created by ian on 18.03.21
 * Location: de.craftedcrime.bungee.servermanager.listeners
 * Created for the project servermanager with the name ServerConnectListener
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ServerConnectListener implements Listener {

    private Servermanager servermanager;

    public ServerConnectListener(Servermanager servermanager) {
        this.servermanager = servermanager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerConnect(ServerConnectEvent event) {
        // check if player is allowed to join the server
        if (event.getPlayer().getServer() != null && event.getPlayer().getServer().getInfo().getName().equalsIgnoreCase(event.getTarget().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(new TextComponent("§8| §aServerManager §8| §cYou are already on this server."));
            return;
        }
        if (event.getReason() == ServerConnectEvent.Reason.JOIN_PROXY) { // on proxy join check if player is on server he's not allowed to be on
            if (servermanager.isForceHub()) { // force hub is enabled!
                System.out.println("forced to join a hub");
                String hub = servermanager.getServerHandler().getJoinableHub(event.getPlayer());
                if (hub != null) {
                    event.setTarget(servermanager.getProxy().getServerInfo(hub));
                    event.getPlayer().sendMessage(new TextComponent("§8| §aServerManager §8| §aYou were moved to a lobby-server."));
                } else {
                    event.setCancelled(true);
                    event.getPlayer().disconnect(new TextComponent("§8| §aServerManager §8| §cThere was no fallback available for you.\n§ePlease try again!"));
                }
            } else {
                if (!servermanager.getServerHandler().isAbleToJoin(event.getPlayer(), servermanager.getServerHandler().getServerInformation(event.getTarget().getName()))) {
                    // check if player is allowed to join this server
                    String fback = servermanager.getServerHandler().getReconnectServer(event.getPlayer(), event.getTarget().getName());
                    if (fback != null) {
                        event.setTarget(servermanager.getProxy().getServerInfo(fback));
                        event.getPlayer().sendMessage(new TextComponent("§8| §aServerManager §8| §cYou're not allowed to join this server, you've been moved to a default hub!"));
                    } else {
                        event.getPlayer().disconnect(new TextComponent("§8| §aServerManager §8| §cThere was no fallback available for you.\n§ePlease try again!"));
                    }
                }
            }
        } else {
            if (!servermanager.getServerHandler().isAbleToJoin(event.getPlayer(), servermanager.getServerHandler().getServerInformation(event.getTarget().getName()))) {
                if (event.getPlayer().getServer() != null && servermanager.getServerHandler().isAbleToJoin(event.getPlayer(), servermanager.getServerHandler().getServerInformation(event.getPlayer().getServer().getInfo().getName()))) {
                    event.setCancelled(true);
                } else {
                    String fback = servermanager.getServerHandler().getReconnectServer(event.getPlayer(), event.getTarget().getName());
                    if (fback != null) {
                        event.setTarget(servermanager.getProxy().getServerInfo(fback));
                        event.getPlayer().sendMessage(new TextComponent("§8| §aServerManager §8| §cYou're not allowed to join this server, you've been moved to a default hub!"));
                    } else {
                        event.getPlayer().disconnect(new TextComponent("§8| §aServerManager §8| §cThere was no fallback available for you.\n§ePlease try again!"));
                    }
                }
            }
        }
    }
}
