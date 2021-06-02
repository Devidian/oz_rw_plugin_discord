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
import net.risingworld.api.objects.Player;

public class BanCommand implements CommandExecutor {

    static final String command = "ban";
    static final String fullCommand = "/" + command;
    static final String description = "tbd";
    static final Colors c = Colors.getInstance();

    @Command(aliases = { fullCommand }, description = description, async = true, privateMessages = false, usage = "/ban [playername] [reason?]", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message, MessageAuthor author, String cmd,
            String playerName, String reason) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        if (playerName == null) {
            return t.get("CMD_ERR_BAN_ARGUMENTS", lang);
        }
        Player player = server.getPlayer(playerName);

        if (player == null) {
            return t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName);
        }

        // TODO: add duration
        player.ban(reason);
        server.getAllPlayers().forEach((p) -> {
            String l = p.getSystemLanguage();
            p.sendTextMessage(c.warning + DiscordWebHook.pluginName + ":>" + c.text
                    + t.get("BC_BANNED", l).replace("PH_PLAYER", playerName)
                            .replace("PH_DISCORDUSER", author.getDiscriminatedName()).replace("PH_REASON", reason));
        });
        message.addReaction("✔");

        return "Player " + playerName + " banned!";
    }
}
