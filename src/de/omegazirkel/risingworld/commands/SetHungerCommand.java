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

public class SetHungerCommand implements CommandExecutor {

    static final String command = "sethunger";
    static final String description = "tbd";

    @Command(aliases = { "/"
            + command }, description = description, async = true, privateMessages = false, usage = "/sethunger [playerName] [value=100]", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message, MessageAuthor author, String playerName,
            String valueInt) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        if (playerName == null) {
            return t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD", "/sethunger [PLAYER] [VALUE=100]");
        }
        if (valueInt == null) {
            valueInt = "100";
        }

        Player player = server.getPlayer(playerName);

        if (player == null) {
            return t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName);
        }

        try {
            int value = Integer.parseInt(valueInt);
            player.setHunger(value);
            message.addReaction("✔");
        } catch (Exception e) {
            message.addReaction("👎");
        }

        return null;
    }
}
