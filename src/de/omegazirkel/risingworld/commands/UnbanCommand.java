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

public class UnbanCommand implements CommandExecutor {

    static final String command = "unban";
    static final String fullCommand = "/" + command;
    static final String description = "tbd";

    @Command(aliases = {
            fullCommand }, description = description, async = true, privateMessages = false, usage = "/unban [name]", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message, MessageAuthor author, String cmd, String playerName) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        if (playerName == null) {
            return t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD", "/unban [name]");
        }
        try {
            Server.unbanPlayer(playerName);
            message.addReaction("✔");
        } catch (Exception e) {
            message.addReaction("👎");
        }

        return null;
    }
}
