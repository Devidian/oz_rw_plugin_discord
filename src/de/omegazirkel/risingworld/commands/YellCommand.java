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

public class YellCommand implements CommandExecutor {

    static final String command = "yell";
    static final String fullCommand = "/" + command;
    static final String description = "tbd";

    @Command(aliases = {
            fullCommand }, description = description, async = true, privateMessages = false, usage = "/yell [text]", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message, MessageAuthor author, String cmd,
            String content) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        if (content == null) {
            return t.get("CMD_ERR_YELL_ARGUMENTS", lang);
        }

        // content may be only the first word so we have to fetch the whole text from
        // the message excluding the first 2 parts
        String response = message.getContent().split(" ", 2)[1];

        server.broadcastYellMessage(response);
        message.addReaction("✔");

        return null;
    }
}
