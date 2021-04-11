package de.craftedcrime.bungee.servermanager.listeners;
/*
 * Created by ian on 11.04.21
 * Location: de.craftedcrime.bungee.servermanager.listeners
 * Created for the project servermanager with the name ServerPingListener
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerPingListener implements Listener {

    private Servermanager servermanager;

    public ServerPingListener(Servermanager servermanager) {
        this.servermanager = servermanager;
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing r = event.getResponse();
        r.setDescriptionComponent(new TextComponent(servermanager.getMotd()));
        event.setResponse(r);
    }

}
