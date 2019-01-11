### Version 0.9.1
- New Discord command: `/restart` set restart flag if there is atleast one player online, shutdown server if not
- New Discord command: `!online` prints a list of players currently online
- New Discord command: `!help` prints a list of commands available in discord
- Fixed: Plugin update watcher was broken


### Version 0.9.0
- New: JavaCord integrated. Now it is possible to activate a discord bot to send commands from discord to your server (for now only /support)
- New: Discord command `/support PLAYERNAME TEXT...` to send a (support) message to a player
- New settings: `botEnable` enables the DiscordBot if true (default is false)
- New settings: `botSecure` only the Bot Owner can send commands if true (default)
- New settings: `botToken` the bot token

### Version 0.8.3
- Project converted to Maven, now it can be build in vscode!

### Version 0.8.2
- typos...

### Version 0.8.1
- english translation for readme and history
- Fix: chat messages that begin with # are not send to discord (See my other Plugin Global Intercom)

### Version 0.8.0
- New: players can use /joinDiscord if it is configured

### Version 0.7.1:
- Project cleaned up for Github

### Version 0.7.0:
- Neu: Autodetection of new settings.properties and reload without server restart
- New: Autodetection of new jar files with optional auto-shutdown after last player has left the server

### Version 0.6.0:
- New: Server restart flag can now set by players after they have played a total time of 1 day on the server (can be configured or turned off)
- Fix: ​Support tickets now include world coordinates with a teleport command to this location. Useful if players report bugs.

### Version 0.5.0:
- Fix: Messages with utf8 characters can now be send (was not correctly sent as utf8)
- New: with `/ozrestart` an Admin can set a restart flag that will shutdown the server as soon as the last player left the server. 
WARNING: only use this if your server comes back automatically

### Version 0.4.0:
- For each channel (Chat,Status,Support) you can now configure a different webHook if you like
- You can now change the username of the status messages
- Additional the servername can be used automatically for messages to the status webHook (Player names have a limit of 32 chars and must not have any of @#:`)

### Version 0.3.0:
- New: You can configure a MOTD if you like to show a message if a player logs in (fo example info about this plugin)
- New: a second webHook can be configured for support tickets send by `/support [text]`, the player receives a message that his text was send to Disorc

### Version 0.2.0:
- New: shows now logins and logouts
- New: visibility of chat or logins can be configured in settings.properties

### Version 0.1.0:
- initial Plugin, only chat is posted
