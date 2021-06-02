package de.omegazirkel.risingworld.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.omegazirkel.risingworld.DiscordWebHook;
import de.omegazirkel.risingworld.JavaCordBot;
import de.omegazirkel.risingworld.guards.RisingWorldCommandGuard;
import de.omegazirkel.risingworld.tools.I18n;
import net.risingworld.api.Server;
import net.risingworld.api.objects.Weather;

public class SetWeatherCommand implements CommandExecutor {

    static final String command = "setweather";
    static final String fullCommand = "/" + command;
    static final String description = "tbd";

    static final de.omegazirkel.risingworld.tools.Logger log = new de.omegazirkel.risingworld.tools.Logger(
            "[OZ.DP] [JavaCordBot.Commands.SetWeatherCommand]");

    @Command(aliases = {
            fullCommand }, description = description, async = true, privateMessages = false, usage = "/setweather [weather]", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message, MessageAuthor author, String cmd,
            String weather) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        if (weather == null) {
            return t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD", "/setweather [Weather]");
        }

        try {
            Weather newWeather = Weather.valueOf(weather);
            server.setWeather(newWeather, false);
            message.addReaction("✔");
        } catch (IllegalArgumentException e) {
            StringBuilder sb = new StringBuilder();
            for (Weather w : Weather.values()) {
                sb.append(w.toString() + "\n");
            }
            return t.get("CMD_ERR_ILLEGAL_ARGUMENTS", lang).replace("PH_CMD", "/setweather [Weather]")
                    .replace("PH_ARGUMENT", "Weather").replace("PH_ARGS_AVAILABLE", sb.toString());
        } catch (NullPointerException e) {
            log.out(e.getMessage());
            message.addReaction("👎");
        }

        return null;
    }
}
