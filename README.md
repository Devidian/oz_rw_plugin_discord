# Aktuelle Freatures:
- Ingame-Chat in einen Discord-Kanal posten mit Ingame-Spielernamen
- Logins/Logouts in einen Discord-Kanal posten
- Support Anfragen die ingame per "/support [nachricht]" gesendet werden können
- Server neustart durch Admin per /ozrestart nachdem alle Spieler ausgeloggt haben.
- Server neustart kann optional für Spieler aktiviert werden wenn sie eine bestimmte totalPlaytime aufweisen (echt-Zeit die sie auf dem Server verbracht haben)
- Server erkennt updates in settings.properties und läd diese neu. Kann optional in den Discord status Kanal gepostet werden
- Server erkennt Änderungen am JAR file und reagiert (wenn aktiviert) mit einem neustart des servers (oder aber setzt das restart flag falls noch spieler online sind) Wird im Spiel und optional in Discord reported

# Geplante Features:
Aktuell keine weiteren

# Build (Netbeans):
2 Libraries müssen eingerichtet werden:

## RisingWorldAPI
https://forum.rising-world.net/thread/4743-getting-started/

## ApacheHttp
Inhalt:
```
commons-logging-1.2.jar
httpcore-4.4.10.jar
httpclient-4.5.6.jar
```
https://hc.apache.org/downloads.cgi

# Installation nach build:
Einfach den dist Ordner in den Plugin Ordner verschieben, umbenennen und fertig!

## Konfiguration
Die Datei settings.properties anpassen und nach deinen Wünschen anpassen

| setting  |  default | description  |
|---|---|---|
|  logLevel |  0 | Logging to server console higher values means less output 0=all (debug)  |
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
