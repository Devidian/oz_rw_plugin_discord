package de.omegazirkel.risingworld.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.omegazirkel.risingworld.DiscordWebHook;
import de.omegazirkel.risingworld.JavaCordBot;
import de.omegazirkel.risingworld.guards.RisingWorldCommandGuard;
import net.risingworld.api.Server;

public class GetWeatherCommand implements CommandExecutor {

    static final String command = "weather";
    static final String description = "tbd";

    @Command(aliases = { "?" + command, "/"
            + command }, description = description, async = true, privateMessages = false, usage = "?weather", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        return "Current weather: " + server.getCurrentWeather() + "\nNext weather: "
        + server.getNextWeather();
    }
}
