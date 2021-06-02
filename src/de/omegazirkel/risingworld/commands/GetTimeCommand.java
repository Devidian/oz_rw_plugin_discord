package de.omegazirkel.risingworld.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.omegazirkel.risingworld.DiscordWebHook;
import de.omegazirkel.risingworld.JavaCordBot;
import de.omegazirkel.risingworld.guards.RisingWorldCommandGuard;
import net.risingworld.api.Server;
import net.risingworld.api.objects.Time.Unit;

public class GetTimeCommand implements CommandExecutor {

    static final String command = "time";
    static final String fullCommand = "/" + command;
    static final String altCommand = "?" + command;
    static final String description = "tbd";

    @Command(aliases = { altCommand, fullCommand }, description = description, async = true, privateMessages = false, usage = "?time", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        return "Current time: " + server.getGameTime(Unit.Hours) + ":" + server.getGameTime(Unit.Minutes);
    }
}
