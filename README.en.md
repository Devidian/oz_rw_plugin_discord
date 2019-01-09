# Current features:
- post ingame chat to Discord with usernames
- post server status messages such as login, logout, startup and shutdown to Discord 
- post support messages using `/support [message]` to a Discord channel
- every Discord channel can have its own webHook (chat, support and status)
- admins can trigger server restart with `/ozrestart` that is executed after the last player left the server (it causes server shutdown, you need to be sure that server comes up automatically after that)
- player can trigger server restart too (if you confgure it), but only if they have spent an amount of real-time on your server that you can define (default:1 day, if you play 2h a day you need 12 days to achive this)
- plugin detects changes to settings.properties and reloads them. A message can be sent to discord if you like.
- plugin detects changes to jar files and sets restart flag if you like. Can also report this to discord and ingame chat
- players can type /joinDiscord to join your discord if you configure this

# Planned features:
currently none

# Commands ingame:
|Command|Description|
|---|---|
|/support [text]|sends [text] as support message to Discord|
|/ozrestart|set restart flag to shutdown server after last player has left|
|/joinDiscord|join the servers Discord server| 


# Commands discord:
|Command|Description|
|---|---|
|/support [playername] [text]|sends a text message to a player (must be online)|

# Build (Maven):
You have to create a new folder in your projects parent folder called `libs` and insert `PluginAPI.jar` from RisingWorld.
See also: https://forum.rising-world.net/thread/4743-getting-started/

# Installation after build:
just copy `dist/DiscordPlugin` folder after build into your plugin folder, thats it!

## Configuration
The settings.properties contains all you need to configure this plugin

| setting  |  default | description  |
|---|---|---|
|  logLevel |  0 | Logging to server console higher values means less output 0=all (debug)  |
|botEnable|false|Enables usage of DiscordBot|
|botSecure|true|Only Bot owner can use commands if `true`|
|botToken||the token for your bot|
|joinDiscord||the code to join discord (not the full url!)|
|  postChat |  false | if true, chat is posted to the webHook for Chat  |
|  webHookChatUrl |   | this is the webHook used for ingame chat  |
|  postStatus | false  | if true, server status messages like player login/out is send to the webHook for Status  |
|  useServerName | false  | if true, the servername is used as username for status posts  |
|  reportStatusEnabled | true  | if true, a message will be posted when the plugin is enabled (server boot)  |
|  reportStatusDisabled | true  | if true, a message will be posted when the plugin is disabled (server shutdown)  |
|  reportSettingsChanged | true  | if true, a message will be posted if settings.properties has changed  |
|  reportJarChanged | true  | if true, a message will be posted if the jar file has changed (plugin update for example) |
|  statusEnabledMessage |   | the message that will be posted to discord on plugin enable  |
|  statusDisabledMessage |   | the message that will be posted to discord on plugin disable  |
|  statusUsername |   | the fixed username to use for status messages  |
|  webHookStatusUrl |   | this is the webHook used for status messages  |
|  postSupport |   |   |
|  webHookSupportUrl |   | this is the webHook used for support messages  |
|  addTeleportCommand | true  | if true, a teleport command is added to the support message  |
|  sendMOTD | true  | if true, player will see MOTD on login  |
|  motd |   | this is the message player will see on login  |
|allowRestart|false|if true, normal players are allowed to use /ozrestart after they played `restartMinimumTime` seconds on the server|
|restartOnUpdate|true|if true, restart flag is set automatically if plugin file has changed on the server|
|restartMinimumTime|86400|player must play at least this time in seconds to use restart feature|
