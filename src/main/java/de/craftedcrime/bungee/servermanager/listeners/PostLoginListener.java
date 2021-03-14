package de.craftedcrime.bungee.servermanager.listeners;
/*
 * Created by ian on 14.03.21
 * Location: de.craftedcrime.bungee.servermanager.listeners
 * Created for the project servermanager with the name PostLoginListener
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class PostLoginListener implements Listener {

    private Servermanager servermanager;

    public PostLoginListener(Servermanager servermanager) {
        this.servermanager = servermanager;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        if (servermanager.isForceHub()) {
            if (!servermanager.getServerHandler().sendToHub(event.getPlayer())) {
                event.getPlayer().disconnect(new TextComponent("§8| §aServerManager §8| §cWe couldn't find a hub you can join. §ePlease try again later."));
            }
        } else {
            AtomicBoolean joined = new AtomicBoolean();
            joined.set(false);
            System.out.println("CONNECT SERVER " + event.getPlayer().getReconnectServer().getName());
        }

    }
}
