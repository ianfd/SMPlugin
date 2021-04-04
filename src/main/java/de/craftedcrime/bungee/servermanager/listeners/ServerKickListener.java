package de.craftedcrime.bungee.servermanager.listeners;
/*
 * Created by ian on 17.03.21
 * Location: de.craftedcrime.bungee.servermanager.listeners
 * Created for the project servermanager with the name ServerKickListener
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ServerKickListener implements Listener {

    private Servermanager servermanager;

    public ServerKickListener(Servermanager servermanager) {
        this.servermanager = servermanager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerKick(ServerKickEvent event) {
        // TODO: error on multiple joins!
        String fback = servermanager.getServerHandler().getReconnectServer(event.getPlayer(), event.getKickedFrom().getName());
        System.out.println("checked on kick " + event.getKickedFrom().getName() + " recon server " + fback);
        servermanager.getProxy().getConsole().sendMessage(event.getKickReasonComponent());
        if (fback == null) {
            System.out.println("player has no fallback");
            event.getPlayer().disconnect(new TextComponent("§8| §aServerManager §8| §cSorry, but we couldn't find a fallback server for you."));
        } else {
            System.out.println("player is allowed to join");
            event.setCancelled(true);
            System.out.println("allowed to join");
            System.out.println("fallback server is " + fback);
            event.setCancelServer(servermanager.getProxy().getServerInfo(fback));
        }
    }
}
