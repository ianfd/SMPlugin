package de.craftedcrime.bungee.servermanager.commands;
/*
 * Created by ian on 17.03.21
 * Location: de.craftedcrime.bungee.servermanager.commands
 * Created for the project servermanager with the name HubCommand
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {

    private Servermanager servermanager;

    public HubCommand(Servermanager servermanager) {
        super("hub", "", "lobby");
        this.servermanager = servermanager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
            if (servermanager.getServerHandler().isAlreadyOnLobby(proxiedPlayer)) {
                proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cYou are already on a lobby-server."));
            } else {
                if (!servermanager.getServerHandler().sendToHub(proxiedPlayer)) {
                    proxiedPlayer.sendMessage(new TextComponent("§8| §aServerManager §8| §cSorry, but we couldn't send you to a hub/lobby. §ePlease contact the support or try again later!"));
                }
            }
        } else {
            sender.sendMessage(new TextComponent("§8| §aServerManager §8| §cThis command is only available for players!"));
        }
    }
}
