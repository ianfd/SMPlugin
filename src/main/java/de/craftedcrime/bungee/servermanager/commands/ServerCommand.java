package de.craftedcrime.bungee.servermanager.commands;
/*
 * Created by ian on 02.03.21
 * Location: de.craftedcrime.bungee.servermanager.commands
 * Created for the project servermanager with the name ServerCommand
 */

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ServerCommand extends Command {

    public ServerCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }
}
