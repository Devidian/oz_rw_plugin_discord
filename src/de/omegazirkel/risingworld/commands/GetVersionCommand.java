package de.omegazirkel.risingworld.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.omegazirkel.risingworld.DiscordWebHook;
import de.omegazirkel.risingworld.guards.RisingWorldCommandGuard;

public class GetVersionCommand implements CommandExecutor {

    static final String command = "version";
    static final String fullCommand = "/" + command;
    static final String altCommand = "?" + command;
    static final String description = "tbd";

    @Command(aliases = { altCommand, fullCommand }, description = description, async = true, privateMessages = false, usage = "?version", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message) {

        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        return "Plugin version: " + DiscordWebHook.pluginVersion;
    }
}
