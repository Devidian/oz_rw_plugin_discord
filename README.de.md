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
- Support Nachrichten haben automatisch einen screenshot beigefügt
- Mit `+screen` im chat kann ein screenshot in den discord kanal hochgeladen werden
- `Badass Tracker` meldet Tötungen von zahmen Tieren und Pferden sowohl Zerstörung nd Entfernung von Objekten die aufgehoben werden können
- Public API für andere Plugins (Status/Event Kanal)
- Es können mehrere Zeitpunkte für einen terminierten Server neustart definiert werden. Der Server startet neu wenn alle Spieler den Server verlassen haben (eine Nachricht informiert die Spieler)
- Ein geplanter Neustart kann nach einer bestimmten Anzahl Minuten erzwungen werden. Die Spieler die noch auf dem Server sind werden vorher gekickt.

# Geplante Features:
- aktuell keine

# Befehle im Spiel:
|Command|Beschreibung|
|---|---|
|/support [text]|sendet [text] als Support-Anfrage an Discord|
|/dp restart|Setzt ein Neustart-Flag oder startet den Server direkt neu wenn keine Spieler online sind|
|/joinDiscord|tritt dem Discord Server bei (öffnet Discord Webseite)|
|/dp info|Zeige Beschreibung des Plugins|
|/dp help|Zeige Liste aller Befehle|
|/dp status|Zeige aktuellen Status (Version, Verbindung, Sprache,...)|

# Befehle discord:
|Command|Beschreibung|
|---|---|
|**informative Befehle:**||
|?help                         | zeigt hilfe an (Liste aller Befehle)|
|?version                      | zeigt die aktuell installierte Plugin Version|
|?online                       | zeigt eine Liste der Spieler die gerade online sind|
|?weather                      | gibt das aktuelle Wetter im Spiel zurück|
|?time                         | gibt die aktuelle Uhrzeit im Spiel zurück|
|?banned                       | zeigt eine Liste aller gebannten Spieler mit name, uid und Grund|
|**administrative Befehle:**||
|/restart                      | setzt das neustart-flag falls noch Spieler online sind oder startet den Server direkt neu|
|/support [PLAYERNAME] [TEXT]  | sende einen TEXT zu PLAYERNAME als [SUPPORT] Nachricht|
|/kick [PLAYERNAME] [REASON?]  | kicke einen Spieler vom Server, Grund ist optional|
|/ban [PLAYERNAME] [REASON?]   | banne einen Spieler vom Server, Grund ist optional|
|/group [PLAYERNAME] [GROUP]   | setze die Gruppe eines Spielers|
|/yell [TEXT]                  | sende einen TEXT über yell (schrei)|
|/bc [TYPE] [TEXT]             | sende einen TEXT an alle als [TYPE] Nachricht|
|/unban [UID]                  | entbannt einen Spieler anhand seiner uid|
|/tptp [PLAYER_A] [PLAYER_B]   | teleportiert Spieler A nach Spieler B wenn beide online sind|
|/mkadmin [PLAYER]             | gibt einem Spieler Admin Status|
|/unadmin [PLAYER]             | nimmt einem Spieler Admin Status|
|/setweather [Weather]            | setzt das Wetter im Spiel|
|/settime [HOUR] [MINUTE]      | setzt die Uhrzeit im Spiel|
|/sethealth [PLAYER] [VALUE]   | setzt den `health` (Leben) Wert eines Spielers auf `value`|
|/sethunger [PLAYER] [VALUE]   | setzt den `hunger` (Hunger) Wert eines Spielers auf `value`|
|/setthirst [PLAYER] [VALUE]   | setzt den `thirst` (Durst) Wert eines Spielers auf `value`|

# Installation (prebuild)
Lade die aktuellen Veröffentlichungen (releases) herunter von [hier (shared)](https://github.com/Devidian/oz_rw_plugin_tools/releases) und [hier (Discord Plugin)](https://github.com/Devidian/oz_rw_plugin_discord/releases) und entpacke sie in dein `RisingWorld/plugins` Verzeichnis

## Dateibaum

Sollte wie folgt aussehen:

```css
    ── RisingWorld
        ├── plugins
        │    ├── DiscordPlugin
        │    │    ├── i18n...
        │    │    ├── lib...
        │    │    ├── HISTORY.de.md
        │    │    ├── HISTORY.md
        │    │    ├── OZ-DiscordPlugin.jar
        │    │    ├── README.de.md
        │    │    ├── README.md
        │    │    └── settings.properties
        │    ├── shared
        │    │    ├── assets...
        │    │    └── lib
        │    │         ├── HISTORY.md
        │    │         └── tools.jar
        :    :
```

# Build (Maven):
Für den build (package) prozess benötigt ihr im Übergeordneten Verzeichnis dieses Projekts einen Ordner `libs` mit der `PluginAPI.jar`
Siehe auch: https://forum.rising-world.net/thread/4743-getting-started/


# Installation nach build:

Einfach den `dist/DiscordPlugin` Ordner in den Plugin Ordner verschieben, fertig!

## Konfiguration
Die Datei settings.properties anpassen und nach deinen Wünschen anpassen

| setting  |  default | description  |
|---|---|---|
|**General plugin settings**|||
|logLevel|0|Logging to server console higher values means less output 0=all (debug)|
|restartOnUpdate|false|if true, restart flag is set automatically if plugin file has changed on the server|
|sendPluginWelcome|false|-|
|**JavaCord settings**|||
|botEnable|false|Enables usage of DiscordBot|
|botSecure|true|Only Bot owner can use commands if `true`|
|botToken||the token for your bot|
|botLang|en|-|
|botChatChannelName|server-chat|-|
|botAdmins||-|
|**other plugin settings**|||
|allowRestart|false|if true, normal players are allowed to use /ozrestart after they played `restartMinimumTime` seconds on the server|
|restartMinimumTime|86400|player must play at least this time in seconds to use restart feature|
|allowScreenshots|true|-|
|joinDiscord||the code to join discord (not the full url!)|
|restartTimed|false|enable or disable scheduled restart feature|
|restartTimes|00:00|set restart times for scheduled restart use hh:mm to add multiple values split them with a pipe|
|forceRestartAfter|0|force restart after this time (minutes) (0 if you do not want to force restart)|
|**Chat settings**|||
|postChat |  false | if true, chat is posted to the webHook for Chat  |
|webHookChatUrl |   | this is the webHook used for ingame chat  |
|overrideAvatar|true|-|
|**Status settings**|||
|postStatus | false  | if true, server status messages like player login/out is send to the webHook for Status  |
|useServerName | false  | if true, the servername is used as username for status posts  |
|reportStatusEnabled | true  | if true, a message will be posted when the plugin is enabled (server boot)  |
|reportStatusDisabled | true  | if true, a message will be posted when the plugin is disabled (server shutdown)  |
|reportSettingsChanged | true  | if true, a message will be posted if settings.properties has changed  |
|reportJarChanged | true  | if true, a message will be posted if the jar file has changed (plugin update for example) |
|statusEnabledMessage |   | the message that will be posted to discord on plugin enable  |
|statusDisabledMessage |   | the message that will be posted to discord on plugin disable  |
|statusUsername |   | the fixed username to use for status messages  |
|webHookStatusUrl |   | this is the webHook used for status messages  |
|**Support settings**|||
|postSupport|false|-|
|supportScreenshot|true|-|
|webHookSupportUrl||this is the webHook used for support messages  |
|addTeleportCommand|true|if true, a teleport command is added to the support message  |
|**Color & Chat settings**|||
|maxScreenWidth|1920|-|
|colorizeChat|true|-|
|showGroup|false|-|
|colorSupport|[#782d8e]|-|
|colorLocalSelf|[#ddffdd]|-|
|colorLocalAdmin|[#db3208]|-|
|colorLocalOther|[#dddddd]|-|
|colorLocalDiscord|[#ddddff]|-|
|**Badass/Event Tracker settings**|||
|postTrackedEvents|false|-|
|webHookEventUrl||-|
|trackServerLogLevel|100|-|
|trackMountKill|false|-|
|trackNonHostileAnimalKill|false|-|
|trackPickupables|false|-|