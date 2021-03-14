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

### Permissions

`server.access.<access type>` Allows the player to join all servers with a certain access type

`server.join.<server name>` Allows the player to join the server with the name in the permission

`serverm.admin` Servermanager admin access (administration + access to all servers)

`serverm.joinall` Allows a player to join all servers

## Stats

Stats are used to see who is using this plugin at what scale. It's going to log errors / player amount / server amount / amount of bungees.

The server updates its information every 10 minutes. 

You can disable this feature in the stats section of the `ServerManager` Plugin.

The stats are (for abuse purposes) associated this a stats-ID that identifies the server. The log-data is stored 
anonymously and is used to improve the plugin. 

