package de.omegazirkel.risingworld.commands;

import java.util.Arrays;
import java.util.List;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.omegazirkel.risingworld.DiscordWebHook;
import de.omegazirkel.risingworld.JavaCordBot;
import de.omegazirkel.risingworld.guards.RisingWorldCommandGuard;
import de.omegazirkel.risingworld.tools.I18n;
import net.risingworld.api.Server;
import net.risingworld.api.objects.Player;

public class OnlineCommand implements CommandExecutor {

    static final String command = "online";
    static final String description = "tbd";

    @Command(aliases = { "?"
            + command }, description = description, async = true, privateMessages = false, usage = "?online", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        Server server = plugin.getServer();
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();

        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        int playersOnline = server.getPlayerCount();
        if (playersOnline == 0) {
            return t.get("CMD_OUT_ONLINE_NOBODY", lang);
        }

        List<String> list = Arrays.asList(t.get("CMD_OUT_ONLINE_LIST", lang) + "\n");
        StringBuilder sb = new StringBuilder();
        list.forEach(sb::append);
        server.getAllPlayers().forEach((Player p) -> {
            sb.append(p.getName() + "\n");
        });
        return sb.toString();
    }
}
