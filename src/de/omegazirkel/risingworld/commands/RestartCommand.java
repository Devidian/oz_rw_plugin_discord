package de.omegazirkel.risingworld.commands;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.omegazirkel.risingworld.DiscordWebHook;
import de.omegazirkel.risingworld.JavaCordBot;
import de.omegazirkel.risingworld.guards.RisingWorldCommandGuard;
import de.omegazirkel.risingworld.tools.Colors;
import de.omegazirkel.risingworld.tools.I18n;
import net.risingworld.api.Server;

public class RestartCommand implements CommandExecutor {

    static final String command = "restart";
    static final String fullCommand = "/" + command;
    static final String description = "tbd";
    static final Colors c = Colors.getInstance();

    @Command(aliases = {
            fullCommand }, description = description, async = true, privateMessages = false, usage = "/restart", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message, MessageAuthor author) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }
        int playersLeft = server.getPlayerCount();
        if (playersLeft == 0) {
            message.addReaction("✔");
            server.shutdown();
            return t.get("CMD_OUT_RESTART_NOW", lang);
        } else {

            server.getAllPlayers().forEach((p) -> {
                String l = p.getSystemLanguage();
                p.sendTextMessage(c.warning + DiscordWebHook.pluginName + ":>" + c.text
                        + t.get("BC_GROUP", l).replace("PH_DISCORDUSER", author.getDiscriminatedName()));
            });
            plugin.setFlagRestart(true);
            message.addReaction("✔");
            return t.get("CMD_OUT_RESTART_DELAY", lang).replace("PH_PLAYERS", playersLeft + "");
        }
    }
}
