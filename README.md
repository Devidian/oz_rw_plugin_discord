# Aktuelle Features:
- Ingame-Chat in einen Discord-Kanal posten mit Ingame-Spielernamen
- Logins/Logouts in einen Discord-Kanal posten
- Support Anfragen die ingame per "/support [nachricht]" gesendet werden können
- Jeder Discord Kanal (chat,support,status) kann über einen eigenen webHook gesendet werden
- Server neustart durch Admin per /ozrestart nachdem alle Spieler ausgeloggt haben.
- Server neustart kann optional für Spieler aktiviert werden wenn sie eine bestimmte totalPlaytime aufweisen (echt-Zeit die sie auf dem Server verbracht haben)
- Server erkennt updates in settings.properties und läd diese neu. Kann optional in den Discord status Kanal gepostet werden
- Server erkennt Änderungen am JAR file und reagiert (wenn aktiviert) mit einem neustart des servers (oder aber setzt das restart flag falls noch spieler online sind) Wird im Spiel und optional in Discord reported
- Spieler können mit /joinDiscord dem Discord des Servers beitreten sofern es konfiguriert wurde

# Geplante Features:
Aktuell keine weiteren

# Befehle im Spiel:
|Command|Description|
|---|---|
|/support [text]|sends [text] as support message to Discord|
|/ozrestart|set restart flag to shutdown server after last player has left|
|/joinDiscord|join the servers Discord server| 

# Befehle discord:
|Command|Description|
|---|---|
|/support [playername] [text]|sendet eine Nachricht an einen Spieler (muss online sein)|
|/restart|set restart flag to shutdown server after last player has left|
|!online|lists all players that are currently online|

# Build (Maven):
Für den build (package) prozess benötigt ihr im Übergeordneten Verzeichnis dieses Projekts einen Ordner `libs` mit der `PluginAPI.jar`
Siehe auch: https://forum.rising-world.net/thread/4743-getting-started/


# Installation nach build:

Einfach den `dist/DiscordPlugin` Ordner in den Plugin Ordner verschieben, fertig!

## Konfiguration
Die Datei settings.properties anpassen und nach deinen Wünschen anpassen

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
