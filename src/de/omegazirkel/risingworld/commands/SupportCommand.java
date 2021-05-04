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
import net.risingworld.api.objects.Player;

public class SupportCommand implements CommandExecutor {

    static final String command = "support";
    static final String description = "tbd";

    @Command(aliases = { "/"
            + command }, description = description, async = true, privateMessages = false, usage = "/support [playerName] [text]", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message, MessageAuthor author, String playerName,
            String content) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        if (content == null || playerName == null) {
            return t.get("CMD_ERR_SUPPORT_ARGUMENTS", lang);
        }

        Player player = server.getPlayer(playerName);
        if (player == null) {
            return t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName);
        }

        // content may be only the first word so we have to fetch the whole text from
        // the message excluding the first 2 parts
        String response = message.getContent().split(" ", 3)[2];

        player.sendTextMessage(plugin.getColorSupport() + "[SUPPORT] " + author.getDisplayName() + ": "
                + plugin.getColorText() + response);
        message.addReaction("✔");

        return null;
    }
}
