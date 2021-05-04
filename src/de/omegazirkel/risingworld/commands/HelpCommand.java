package de.omegazirkel.risingworld.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.omegazirkel.risingworld.DiscordWebHook;
import de.omegazirkel.risingworld.JavaCordBot;
import de.omegazirkel.risingworld.guards.RisingWorldCommandGuard;
import de.omegazirkel.risingworld.tools.I18n;

public class HelpCommand implements CommandExecutor {

    static final String command = "help";
    static final String description = "tbd";

    @Command(aliases = { "?" + command, "/"
            + command }, description = description, async = true, privateMessages = false, usage = "?help", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();

        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        return t.get("DISCORD_HELP", lang);
    }
}
