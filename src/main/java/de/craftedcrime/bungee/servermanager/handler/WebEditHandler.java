package de.craftedcrime.bungee.servermanager.handler;
/*
 * Created by ian on 04.04.21
 * Location: de.craftedcrime.bungee.servermanager.http
 * Created for the project servermanager with the name WebEditHandler
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import de.craftedcrime.infrastructure.servermanager.middleware.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import retrofit2.Response;

import java.io.IOException;

public class WebEditHandler {

    private Servermanager servermanager;
    private long lastUploadedConfig = 0;

    public WebEditHandler(Servermanager servermanager) {
        this.servermanager = servermanager;
    }

    public void startWebEditSession(ProxiedPlayer proxiedPlayer) {
        if (lastUploadedConfig != 0) {
            if ((System.currentTimeMillis() - lastUploadedConfig) < 1000 * 60 * 15) {
                long timeleft = ((lastUploadedConfig+1000*60*15) - System.currentTimeMillis()) / 1000;
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
                lastUploadedConfig = System.currentTimeMillis();
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

    public void saveConfig(String key, String secret, ProxiedPlayer proxiedPlayer) {
        try {
            Response<ConfigEdit> response = servermanager.getWebEditClient().webEditService.downloadConfigEdit(key, secret).execute();
            if (response.isSuccessful() && response.body() != null) {
                savingProcess(response.body(), proxiedPlayer);
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void removeAllRegisteredServers() {
        // unloading all servers
        for (ServerObject soo : servermanager.getLobbyMap().values()) {
            servermanager.getProxy().getConsole().sendMessage(new TextComponent("§a §b §a §b §a§7-> §eUnloading lobby §7'§b" + soo.getServerName() + "§7'"));
            servermanager.getProxy().getServers().remove(soo.getServerName());
        }
        servermanager.getLobbyMap().clear();
        for (ServerObject soo : servermanager.getNoLobbiesMap().values()) {
            servermanager.getProxy().getConsole().sendMessage(new TextComponent("§a §b §a §b §a§7-> §eUnloading non-lobby §7'§b" + soo.getServerName() + "§7'"));
            servermanager.getProxy().getServers().remove(soo.getServerName());
        }
        servermanager.getNoLobbiesMap().clear();
        servermanager.getProxy().getConsole().sendMessage(new TextComponent("§aSuccessfully unloaded all servers."));
    }

    // edit icon §e✎
    // add icon §a+
    // remove icon §c-

    public void savingProcess(ConfigEdit configEdit, ProxiedPlayer proxiedPlayer) {


        if (!configEdit.getEditList().isEmpty()) {
            proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §aYou're starting the import process from your new config. " +
                    "\nDuring this process all players are going to be kicked!" +
                    "\nYou can see more information about the import process in the BungeeCord console!"));
            proxiedPlayer.disconnect(new TextComponent("§eThe import process has started! \n§aSee more information in the server logs."));
            for (ProxiedPlayer pp : servermanager.getProxy().getPlayers()) {
                pp.disconnect(new TextComponent("§eWe are updating the network for you! \n§aPlease wait a few seconds before reconnecting!"));
            }

            servermanager.getProxy().getConsole().sendMessage(new TextComponent("§8-=-=-=-=- §7| §a§lServerManager §r§7| §8-=-=-=-=-"));
            servermanager.getProxy().getConsole().sendMessage(new TextComponent("§aStarting the import process!"));
            servermanager.getProxy().getConsole().sendMessage(new TextComponent("§8 -> §eYour MOTD: §7'§r" + ChatColor.translateAlternateColorCodes('&', configEdit.getMotdBungee()) + "§7'"));
            servermanager.getProxy().getConsole().sendMessage(new TextComponent("§8 -> §eYour shown player count: §7'§6" + configEdit.getMaxPlayerBungee() + "§7'"));
            servermanager.getProxy().getConsole().sendMessage(new TextComponent("§8 -> §eUnloading all servers..."));
            removeAllRegisteredServers();
            servermanager.getProxy().getConsole().sendMessage(new TextComponent("§8 -> §aCreating new servers..."));
            for (ServerEdit s : configEdit.getEditList()) {
                if (s.getEditAction() == EditAction.CREATE) {
                    if (!servermanager.getMySQLHandler().serverExists(s.getServerObject().getServerName())) {
                        String active = "§aACTIVE";
                        if (!s.getServerObject().isActive()) active = "§cINACTIVE";
                        String lobby = "§bLOBBY";
                        if (!s.isLobby()) lobby = "§bNON-LOBBY";
                        servermanager.getProxy().getConsole().sendMessage(new TextComponent("§a+ §8 | §7'§b" + s.getServerObject().getServerName() + "§7' §8| " + active + " §8| §b" + s.getServerObject().getMaxPlayers() + "§e players §8| §7'" + lobby + "§7'"));
                        servermanager.getProxy().getConsole().sendMessage(new TextComponent("§a§b§a §8-> §7'§b" + s.getServerObject().getIpAddress() + "§7' §8| §7'§b" + s.getServerObject().getPort() + "§7' §8| §7'§b" + s.getServerObject().getAccessType() + "§7'"));
                        servermanager.getMySQLHandler().addServer(s.getServerObject(), s.isLobby());
                    } else {
                        servermanager.getProxy().getConsole().sendMessage(new TextComponent("§4⚡ §8| §cError, creating a server with the name §7'§b" + s.getServerObject().getServerName() + "§7'§c this server does already exists." +
                                "If this is wrong please try again."));
                    }
                }
            }
            servermanager.getProxy().getConsole().sendMessage(new TextComponent("§8 -> §aModifying existing servers..."));
            for (ServerEdit s : configEdit.getEditList()) {
                if (s.getEditAction() == EditAction.EDIT) {
                    if (s.getServerObject().getServerId() != 0) {
                        servermanager.getMySQLHandler().modifyServer(s.getServerObject(), s.isLobby());
                        servermanager.getProxy().getConsole().sendMessage(new TextComponent("§e✎ §8| §aSaving modified server: §b#" + s.getServerObject().getServerId() + "§e."));
                    } else {
                        servermanager.getProxy().getConsole().sendMessage(new TextComponent("§4⚡ §8| §cError, couldn't modify a server with the ID §7'§b0§7'§c."));
                    }
                }
            }
            servermanager.getProxy().getConsole().sendMessage(new TextComponent("§8 -> §aDeleting existing servers..."));
            for (ServerEdit s : configEdit.getEditList()) {
                if (s.getEditAction() == EditAction.DELETE) {
                    servermanager.getProxy().getConsole().sendMessage(new TextComponent("§c- §8| §aDeleting the server: §b#" + s.getServerObject().getServerId()));
                    servermanager.getMySQLHandler().deleteServerById(s.getServerObject().getServerId(), s.isLobby());
                }
            }

            servermanager.getProxy().getConsole().sendMessage(new TextComponent("§8 -> §aReloading lobbies and non-lobbies!"));
            servermanager.getServerHandler().initAllLobbies();
            servermanager.getServerHandler().initAllNonLobbies();
        } else {
            proxiedPlayer.sendMessage(new TextComponent("nothing to do in here !!!!"));
        }

    }

}
