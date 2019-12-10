## [Unreleased]

## [0.15.1] - 2019-12-10
### Fixed
- updated dependencies

## [0.15.0] - 2019-05-10
### Added
- DiscordBot now send reaction if command was executed successfully
- New tracking of `badass` actions with new webhook url
    - tracking mount kills
    - tracking non hostile animal kills
    - tracking pickup of items like workbenches
- Settings for `Badass Tracker`
    - `postTrackedEvents` enables or disables this feature
    - `webHookTrackUrl` set the webhook where the messages will be posted to
    - `trackServerLogLevel` set the log level to send messages into server log files
    - `trackMountKill` enable or disable tracking of killed mounts
    - `trackNonHostileAnimalKill` enable or disable tracking of non hostile animals
    - `trackPickupCraftingStation` enable or disable tracking of pickup non related stuff
- Public API for Status messages
- Restart scheduler
- Settings for `Restart scheduler`
    - `restartTimed` enables or disables this feature
    - `restartTimes` times to restart (hh:mm), multiple seperated by `|`
    - `forceRestartAfter` minutes after restart is forced and players kicked (0 to disable forced-restart)
- New Discord `informational` commands
    - `?weather` shows the current ingame weather
    - `?time` shows the current ingame time
    - `?banned` shows a list of banned players with name, UID and reason
- New Discord `admin` commands
    - `/unban [UID]` unban a player by his uid
    - `/tptp [PLAYER_A] [PLAYER_B]` teleport Player A to Player B if both are online
    - `/mkadmin [PLAYER]` grant player admin status
    - `/unadmin [PLAYER]` revoke player admin status
    - `/setweather [Weather]` set weather on the server
    - `/settime [HOUR] [MINUTE]` set current ingame time
    - `/sethealth [PLAYER] [VALUE]` set players health to value
    - `/sethunger [PLAYER] [VALUE]` set players hunger to value
    - `/setthirst [PLAYER] [VALUE]` set players thirst to value
- New Settings
    - `restartAdminOnly` if set to true only admins can trigger ingame restart (default=true)
    - `botCMD*` for discord bot commands. Will disable(0), enable for all(1) or enable just for admins(2)
### Fixed
- Exception thrown when typeing `/gi` without parameter
- JavaCordBot removes now all listener on disconnect
- `noColorText` regexp was missing numbers
### Changed
- default language of *.md files is now English
- informational command prefix is now `?` instead of `!` see `?help` in discord or README
- setting `allowRestart` will disable ingame restart command for all now if set to false
### Updated
- Maven dependencies (JavaCord 3.0.1 -> 3.0.4)

## [0.14.0] - 2019-03-20
### Added
- new color `colorLocalAdmin` for local admin color (Player.isAdmin)
- new flag `colorizeChat` to deactivate local chat manipulation
- new flag `showGroup` to show permission groups behind player names
### Changed
- all colors moved to `settings.properties` if someone likes to change them

## [0.13.3] - 2019-03-18
### Added
- some listener added to JavaCordBot to log lost connections.
- new property in `settings.properties` => `supportScreenshot` to enable or disable auto-screenshot in support messages
### Fixed
- the exception that caused crash before 0.13.2 is now fixed

## [0.13.2] - 2019-03-18
### Fixed
- An exception that caused a crash on update should now be catched
### Added
- missing German translations were added

## [0.13.1] - 2019-03-15
### Fixed
- support webhook was incorrectly chat webhook

## [0.13.0] - 2019-03-15
### Changed
- now using tools.jar in version 0.4.1
- text colors were removed from `DiscordWebHook` class, now Colors from tools will be used
- motd removed, its now called `plugin welcome message` and can only be turned on/off in the settings. The text definition has moved to the language files so every user can read the plugin welcome in his language (so far translated)
- Plugin enabled message now triggert in onEnabled
- initSettings now sends settings loaded message to serverlog
### Added
- if you write `+sceen` into a chat message, a screenshot will be uploaded that can be viewed in discord (not ingame)
- in the `settings.properties` you can set screenshots on/off and the maximum screen resolution (width of the image)
- support messages will always have a screenshot attached (may change in future)

## [0.12.0] - 2019-01-30
### Added
- added commands starting with `/dp`
- new command `/dp info` shows description for this plugin
- new command `/dp help` shows usage help for this plugin
- new command `/dp status` shows current status of the plugin, like version and language

### Changed
- replaced log method in `DiscordWebHook` class with Logger from `tools`
- `JavaCordBot` class now has its own Logger from `tools`
- broadcast messages are now translated by player system language
- command messages are now translated by player system language
- `/ozrestart` is now deprecated and will be removed in a later version use `/dp restart` instead
- all messages now have a plugin prefix

## [0.11.5] - 2019-01-27
### Changed
- the plugin-shared lib `tools.jar` must now be placed into `/plugins/shared/lib/tools.jar`

## [0.11.4] - 2019-01-26
### Changed
- tools.jar updated to 0.2.0

### Version 0.11.3
- fixed: remove #% prefix, ignore empty messages
- added: more text-output translations

### Version 0.11.2
- added: in the settings you can now add other Discord User that are allowed to execute secure commands, example: `botAdmins=Devidian#1334,SomeOneElse#1337`
- added: more text-output translations

### Version 0.11.1
- fixed: as the new tools plugin is not loaded in first place it is now converted to a normal jar that comes within the lib folder.

### Version 0.11.0
- changed: removed all tools and moved them into a new plugin, so they can used in other plugins without copy&paste

### Version 0.10.3
- changed: override of avatar_url is now  optional, can be changed in settings file with `overrideAvatar=true|false`
- fixed: local chat always displayed your own name
- added: settings for `botChatChannelName`

### Version 0.10.2
- New Discord command: `!version` shows current plugin version
- New Discord command: `/kick` to kick a player
- New Discord command: `/ban` to ban a player
- New Discord command: `/group` to change player group
- New Discord command: `/bc` to broadcast a message to all player
- New Discord command: `/yell` to yell a message to all player
- **TYPE !help for correct syntax of new commands**
- fixed: properties files were not correctly loaded with UTF8
- New: i18n class for translation (default: en) can be changed in settings with `botLang=`. Currently only works with `!help`, will be expanded in further updates. Currently only `de` and `en` are available (translators for other languages are welcome!)

### Version 0.10.1
- Fixed: Ingame chat message was displayed twice
- added: `avatar_url` to webhook message
- changed: moved JavaCord to its own class
- fixed: JavaCord listener will now be correctly removed on plugin disable (shutdown)
- fixed: automatic update (restart) was still broken, should work now - finally

### Version 0.10.0
- New: local chat has now a prefix `[LOCAL]`
- New: It the Discord-Bot is member of a Discord channel named `server-chat`, it will send all messages that are not commands to the ingame chat
- New: 3 different prefix colors for `[LOCAL]`
  - chat from other (ingame) player
  - chat from discord
  - your own chat messages

### Version 0.9.2
- Changed: Only commands starting with / are secure, all commands starting with ! can now executed by everyone else
- Fixed: GlobalIntercom Messages will now filtered and not send to Discord through the webhook
- **IMPORTANT: If GlobalIntercom is installed, the version must be >= 0.7.2 in order to work**

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
