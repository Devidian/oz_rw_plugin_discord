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
    static final String description = "tbd";

    @Command(aliases = { "/"
            + command }, description = description, async = true, privateMessages = false, usage = "/unban [uid]", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message, MessageAuthor author, String uidRaw) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        if (uidRaw == null) {
            return t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD", "/unban [UID]");
        }
        try {
            long uid = Long.parseLong(uidRaw);
            server.unbanPlayer(uid);
            message.addReaction("✔");
        } catch (Exception e) {
            message.addReaction("👎");
        }

        return null;
    }
}
