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

public class TeleportToPlayerCommand implements CommandExecutor {

    static final String command = "tptp";
    static final String description = "tbd";

    @Command(aliases = { "/"
            + command }, description = description, async = true, privateMessages = false, usage = "/tptp [playerName] [playerName]", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message, MessageAuthor author, String playerNameA, String playerNameB) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        if (playerNameA == null || playerNameB == null) {
            return t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD", "/tptp [PLAYERNAME] [PLAYERNAME]");
        }

        Player player = server.getPlayer(playerNameA);
        Player targetPlayer = server.getPlayer(playerNameB);

        if (player == null) {
            return t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerNameA);
        }
        if (targetPlayer == null) {
            return t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerNameA);
        }

        try {
            player.setPosition(targetPlayer.getPosition());
            message.addReaction("✔");
        } catch (Exception e) {
            message.addReaction("👎");
        }

        return null;
    }
}
