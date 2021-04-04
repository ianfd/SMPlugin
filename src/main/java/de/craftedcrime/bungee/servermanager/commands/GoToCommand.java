package de.craftedcrime.bungee.servermanager.commands;
/*
 * Created by ian on 02.03.21
 * Location: de.craftedcrime.bungee.servermanager.commands
 * Created for the project servermanager with the name GoToCommand
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GoToCommand extends Command {

    private Servermanager servermanager;

    public GoToCommand(String commandName, Servermanager servermanager) {
        super(commandName);
        this.servermanager = servermanager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
            if (args.length == 1) {
                servermanager.getServerHandler().sendPlayerToServer(proxiedPlayer, args[0].toLowerCase());
            } else {
                displayHelp(proxiedPlayer);
            }
        } else {
            sender.sendMessage(new TextComponent("§8| §aServerManager §8| §cThis command can only be performed as a player."));
        }

    }

    private void displayHelp(ProxiedPlayer proxiedPlayer) {

    }
}
