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
