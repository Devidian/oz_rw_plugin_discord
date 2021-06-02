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

public class GroupCommand implements CommandExecutor {

    static final String command = "group";
    static final String fullCommand = "/" + command;
    static final String description = "tbd";
    static final Colors c = Colors.getInstance();

    @Command(aliases = { fullCommand }, description = description, async = true, privateMessages = false, usage = "/group [playername] [groupname]", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message, MessageAuthor author, String cmd,
            String playerName, String groupName) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        // CHECK can group have spaces?
        // String extendedGroupName = message.getContent().split(" ", 3)[2];
        if (groupName == null || playerName == null) {
            return t.get("CMD_ERR_GROUP_ARGUMENTS", lang);
        }

        Player player = server.getPlayer(playerName);
        if (player == null) {
            return t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName);
        }
        player.setPermissionGroup(groupName);

        server.getAllPlayers().forEach((p) -> {
            String l = p.getSystemLanguage();
            p.sendTextMessage(c.warning + DiscordWebHook.pluginName + ":>" + c.text
                    + t.get("BC_GROUP", l).replace("PH_DISCORDUSER", author.getDiscriminatedName())
                            .replace("PH_PLAYER", playerName).replace("PH_GROUP", groupName));
        });
        message.addReaction("✔");

        return t.get("CMD_OUT_GROUP", lang).replace("PH_PLAYER", playerName).replace("PH_GROUP", groupName);
    }
}
