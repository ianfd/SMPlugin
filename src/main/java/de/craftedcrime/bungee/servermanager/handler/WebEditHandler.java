package de.craftedcrime.bungee.servermanager.handler;
/*
 * Created by ian on 04.04.21
 * Location: de.craftedcrime.bungee.servermanager.http
 * Created for the project servermanager with the name WebEditHandler
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import de.craftedcrime.infrastructure.servermanager.middleware.ConfigUpload;
import de.craftedcrime.infrastructure.servermanager.middleware.KeySecretPair;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import retrofit2.Response;

import java.io.IOException;

public class WebEditHandler {

    private Servermanager servermanager;
    private int lastUploadedConfig = 0;

    public WebEditHandler(Servermanager servermanager) {
        this.servermanager = servermanager;
    }

    public void startWebEditSession(ProxiedPlayer proxiedPlayer) {
        if (lastUploadedConfig != 0) {
            if ((System.currentTimeMillis() - lastUploadedConfig) < 1000 * 60 * 15) {
                long timeleft = (System.currentTimeMillis() - lastUploadedConfig) / 1000;
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cUnable to perform action! Sorry, but you are not allowed to to this. " +
                        "You still have to wait: §6 " + ((int) (timeleft / 60)) + " minutes and " + ((int) (timeleft % 60)) + "."));
                return;
            }
        }
        proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aPlease wait ... we are preparing the editor for you."));
        try {
            Response<KeySecretPair> response = servermanager.getWebEditClient().webEditService.startEditing(new ConfigUpload("dummy", 100, servermanager.getLobbyMap(),
                    servermanager.getNoLobbiesMap())).execute();
            if (response.isSuccessful() && response.body() != null) {
                sendCreationMessage(proxiedPlayer, response.body());
            } else {
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cFailed to connect to the WebEditor. Please try again later. " +
                        "\nIf this problem continues to occur contact me."));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendCreationMessage(ProxiedPlayer proxiedPlayer, KeySecretPair keySecretPair) {
        proxiedPlayer.sendMessage(new TextComponent("§8-=-=-=-=- §7| §a§lServerManager §r§7| §8-=-=-=-=-"));
        proxiedPlayer.sendMessage(new TextComponent("§6We have prepared a link for you. There you can edit your ServerManager config interactively. " +
                "\nPlease do not edit here if you have an open web editor session."));
        proxiedPlayer.sendMessage(servermanager.getGeneralUtils().openURLThing("§7->§6 https://sm.craftedcrime.de/editor?key=" + keySecretPair.getKey() + "&secret=" + keySecretPair.getSecret(),
                "https://sm.craftedcrime.de/editor?key=" + keySecretPair.getKey() + "&secret=" + keySecretPair.getSecret(), "§6Click here to get to the WebEditor!"));
        proxiedPlayer.sendMessage(new TextComponent("§7-> §6Here are your credentials:"));
        proxiedPlayer.sendMessage(new TextComponent("§a§b§a§7 -> §eKey: §6§l" + keySecretPair.getKey()));
        proxiedPlayer.sendMessage(new TextComponent("§a§b§a§7 -> §eSecret: §6§l" + keySecretPair.getSecret()));
        TextComponent te = new TextComponent("§eSee more information under: ");
        te.addExtra(servermanager.getGeneralUtils().openURLThing("§6TUTORIALS", "https://sm.craftedcrime.de/tutorials", "§6Get to the tutorials"));
        proxiedPlayer.sendMessage(te);
        proxiedPlayer.sendMessage(new TextComponent("§8-=-=-=-=- §7| §a§lServerManager §r§7| §8-=-=-=-=-"));
    }
}
