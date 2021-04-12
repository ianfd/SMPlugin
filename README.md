TODO:
- finish Mysql stuff
- add commands
- add futher utils
- add config with saving of default
- insert commands into bungee.yml
- aaaaand any other necessary stuff \
..... further comes soon 
IMPORT method to import all servers from the default bungeecord context
  
Orchestration stuff haha added in the future !!

TBD NOW: load servers at startup and add to context !!!!!

Joins, force hub
Joins, what to do when a server is not available


# Documentation

## Usage

### Commands

`/server` A bit more advanced than the default bungee server command 

`/server <servername>` Sends a player to the server with that servername (if he has sufficient permission)

`/sm` Shows you the ServerManager help (sm command usage)

`/sm list-all` Shows you all servers, that are registered in the database (with ServerManager)

`/sm info <servername>` Shows you all information about this server, see for your-self and try it out

`/sm deactivate <servername>` Deactivates the server with this name (if it is managed via ServerManager)

`/sm activate <servername>` Activates an existing server that is deactivated

`/sm delete <servername>` Deletes a server with that name from the database

`/sm setmaxplayer <servername> <max-player amount>` Changes the max player amount that are allowed to be on this server

`/sm create <servername> <ip-address> <port> <access-level> <lobby? true / false>` Creates a new server in the database. 
It's going to be managed via ServerManager.

### Permissions

`server.access.<access type>` Allows the player to join all servers with a certain access type

`server.join.<server name>` Allows the player to join the server with the name in the permission

`serverm.admin` Servermanager admin access (administration + access to all servers)

`serverm.joinall` Allows a player to join all servers

`serverm.teamchat` Allows a player to write in the TeamChat

`serverm.teamlist` shows the player in the team list and grants access to this command


## Stats

Stats are used to see who is using this plugin at what scale. It's going to log errors / player amount / server amount / amount of bungees.

The server updates its information every 10 minutes. 

You can disable this feature in the stats section of the `ServerManager` Plugin.

The stats are (for abuse purposes) associated this a stats-ID that identifies the server. The log-data is stored 
anonymously and is used to improve the plugin. 

## Future Stuff 

### Stuff that's going to be added to this plugin in the future.

- orchestration feature (self activation of servers)
- webeditor prettier
- ingame MOTD edititing
- setting custom player count
- 



