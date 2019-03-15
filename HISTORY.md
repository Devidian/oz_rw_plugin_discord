## [Unreleased]

## [0.13.1] - 2019-03-15
## Fixed
- der support webhook war fälschlicherweise der chat-webhook

## [0.13.0] - 2019-03-15
### Changed
- verwendet nun die tools.jar in Version 0.4.1
- textfarben wurden aus `DiscordWebHook` Klasse entfernt, Colors aus tools wird jetzt verwendet
- MOTD entfernt, heisst jetzt Plugin Welcome Message und kann nur noch ein und aus geschaltet werden, definition des textes ist jetzt in den Sprachdateien, damit jeder Spieler die Nachricht in seiner Sprache lesen kann (sofern übersetzt wurde)
- "Plugin geladen" Meldung wird jetzt in onEnabled gefeuert
- in initSettings wird jetzt "Einstellungen geladen" in das serverlog geschrieben
### Added
- wenn `+screen` in eine Nachricht geschrieben wird, wird ein screenshot übermittelt der in Discord angezeigt werden kann (nicht ingame)
- in den `settings.properties` kann eingestellt werden ob screenshots erlaubt sind und welche maximale Auflösung übertragen werden soll (angabe der breite)
- bei support nachrichten wird immer ein screenshot gesendet (kann sich in Zukunft vielleicht noch ändern)

## [0.12.0] - 2019-01-30
### Added
- Befehle die mit `/dp` beginnen hinzugefügt
- neuer Befehl `/dp info` zeigt die Beschreibung des Plugins
- neuer Befehl `/dp help` zeigt alle möglichen Befehle des Plugins
- neuer Befehl `/dp status` zeigt den aktuellen Status des Plugins wie Version und Sprache

### Changed
- die log methode in `DiscordWebHook` wurde durch den Logger aus `tools` ersetzt
- `JavaCordBot` hat nun seinen eigenen Logger aus `tools`
- broadcast Nachrichten werden nun anhand der Spieler-Systemsprache übersetzt
- Befehls-Nachrichten werden nun auch anhand der Spieler-Systemsprache übersetzt
- `/ozrestart` ist jetzt deprecated und wird bald entfernt, verwende `/dp restart` statt dessen
- Alle Nachrichten haben nun ein Plugin-prefix

## [0.11.5] - 2019-01-27
### Changed
- Die plugin-übergreifende `tools.jar` muss nun unter `/plugins/shared/lib/tools.jar` zur Verfügung stehen

## [0.11.4] - 2019-01-26
### Changed
- tools.jar auf 0.2.0 aktualisiert

### Version 0.11.3
- Behoben: GI prefix `#%` entfernt und leere Nachrichten ignorieren
- Hinzugefügt: Weitere Textbausteine sind nun übersetzt

### Version 0.11.2
- Hinzugefügt: in den settings können jetzt weitere Discord-Admins hinzugefügt werden, Beispiel: `botAdmins=Devidian#1334,SomeOneElse#1337`
- Hinzugefügt: weitere übersetzungen von Textausgaben

### Version 0.11.1
- Behoben: das neue Tools Plugin wird nicht immer zuerst geladen, daher ist dies jetzt ein normales jar das unter lib beigefügt wird.

### Version 0.11.0
- Geändert: alle tools wurden entfernt und in ein eigenes jar/plugin gepackt, damit sie auch in anderen Plugins verwendet werden können ohne copy&paste

### Version 0.10.3
- Geändert: überschreiben der avatar_url ist jetzt  optional, kann in den settings geändert werden mit `overrideAvatar=true|false`
- Behoben: der benutzername bei lokalem chat war immer der eigene
- Hinzugefügt: Einstellung für den `botChatChannelName`

### Version 0.10.2
- Neuer Discord Befehl: `!version` Zeigt die aktuelle Plugin Version an
- Neuer Discord Befehl: `/kick` um Spieler zu kicken
- Neuer Discord Befehl: `/ban` um Spieler zu bannen
- Neuer Discord Befehl: `/group` um Spieler einer Gruppe zuzuweisen
- Neuer Discord Befehl: `/bc` für hervorgehobene Nachrichten an alle online-spieler
- Neuer Discord Befehl: `/yell` für hervorgehobene Nachrichten an alle online-spieler
- **Für korrekte Syntax bitte !help verwenden**
- Behoben: properties Dateien wurden nicht korrekt mit UTF8 geladen
- Neu: i18n Klasse zum übersetzen (Standard: en) kann in settings über `botLang=` geändert werden. Funktioniert aktuell nur bei !help, wird in weiteren Versionen weiter ausgebaut. Auch aktuell nur `de` oder `en` möglich

### Version 0.10.1
- Behoben: Ingame Chat Nachricht wurde doppelt angezeigt
- Hinzugefügt: `avatar_url` zur webhook message
- Geändert: JavaCord in eigene Klasse verschoben
- Behoben: JavaCord listener wird beim shutdown correct entfernt
- Behoben: der automatische update prozess war immer noch kaputt, geht jetzt wieder

### Version 0.10.0
- Neu: Lokaler Chat hat jetzt das Präfix `[LOCAL]`
- Neu: Wenn der Discord-Bot in einem Discord Kanal `server-chat` Mitglied ist, sendet er text der kein Befeh ist ins Spiel
- Neu: 3 verschiedene Farben für `[LOCAL]`
  - Für Text von anderen
  - Für Text aus Discord
  - Für Text den man selber geschrieben hat

### Version 0.9.2
- Geändert: nur Befehle die mit / beginnen sind als "sicher" markiert, Befehle die mit ! beginnen kann jeder ausführen.
- Behoben: GlobalIntercom Meldungen werden nicht mehr als chat über den WebHook gesendet wenn GlobalIntercom installiert ist.
- **WICHTIG: Wenn GlobalIntercom installiert ist, dann muss die Version >= 0.7.2 sein**

### Version 0.9.1
- Neuer Discord Befehl: `/restart` setzt das restart flag falls Spieler online sind oder macht einen shutdown falls nicht.
- Neuer Discord Befehl: `!online` Zeigt eine Liste mit Spielern die online sind
- Neuer Discord Befehl: `!help` Zeigt eine Liste mit Kommandos die über Discord verfügbar sind
- Behoben: Plugin update Überwachung war fehlerhaft

### Version 0.9.0
- Neu: JavaCord integriert. Jetzt kann optional ein DiscordBot token hinterlegt werden. Über diesen kann man dann Befehle von Discord an den Server senden (in dieser Version nur /support)
- Neu: Discord Befehl `/support SPIELERNAME TEXT...` um einem Spieler auf dem Server zu schreiben
- Neue Einstellung: `botEnable` aktiviert den DiscordBot wenn true (standard ist false)
- Neue Einstellung: `botSecure` nur der Besitzer des Bots kann Befehle ausführen wenn true (standard)
- Neue Einstellung: `botToken` der Token des Bots

### Version 0.8.3
- Project konvertiert zu Maven. Jetzt kann es auch in vscode weiterentwickelt werden!

### Version 0.8.2
- Schlechschreibfehler

### Version 0.8.1
- History und Readme gibts jetzt auch auf englisch
- Fix: chat Nachrichten die mit # beginnen werden nicht in discord geschrieben (OZGI plugin)

### Version 0.8.0
- Neu: Spieler können jetzt mit /joinDiscord dem Discord des Servers beitreten sofern es konfiguriert wurde

### Version 0.7.1:
- Projekt bereinigt für Github

### Version 0.7.0:
- Neu: Automatisches erkennen von neuen settings und neu einlesen dieser ohne Server-Neustart durchführen zu müssen.
- Neu: Automatisches erkennen von neuen Jar files (in der Regel plugin update) und optional automatischer server shutdown (restart)

### Version 0.6.0:
- Neu: Server restart flag kann jetzt optional von Spielern mit einer mindest-spiel-zeit (auch einstellbar) aktiviert werden
- Fix: ​Support tickets enthalten jetzt Welt-Koordinaten inclusive dem teleport Befehl zu diesen Koordinaten. Nützlich damit Spieler Fehler in der Welt reporten können und man als Admin direkt zu den gemeldeten Koordinaten springen kann.

### Version 0.5.0:
- Fix: nachrichten mit umlauten können jetzt gesendet werden, codierung wurde auf UTF8 korrigiert
- Neu: mit /ozrestart kann ein Admin ein restart flag setzen, es lässt den server herunter fahren, sobald der letzte Spieler den Server verlassen hat. ACHTUNG: nur benutzen wenn ihr auch ein automatisches startscript nutzt!

### Version 0.4.0:
- Für jeden Kanal (Chat,Status,Support) kann jetzt ein eigener webHook hinterlegt werden
- für den Status Kanal lässt sich der Username in den Settings ändern
- Optional kann automatisch der Servername als Username für Statusmeldungen benutzt werden (Begrenzt auf 32 Zeichen und ohne folgende Zeichen: @#:`)

### Version 0.3.0:
- Nachricht des Tages / motd kann in den settings eingestellt / (de)aktiviert werden und wird beim Spawn an den Spieler gesendet (um z.b. auf den Discord Server hinzuweisen)
- ein zweiter webHook kann konfiguriert werden für support tickets die per `/support [text]` abgesendet werden. Der Spieler erhält die Meldung das die Nachricht an Discord verschickt wurde

### Version 0.2.0:
- Neu: Zeigt nun auch logins und logouts an
- Neu: Anzeige von chat / logins kann über settings.properties konfiguriert werden

### Version 0.1.0:
- Initiales Plugin nur chat wird gepostet
