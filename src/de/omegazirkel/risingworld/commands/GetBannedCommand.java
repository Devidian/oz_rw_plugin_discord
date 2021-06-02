package de.omegazirkel.risingworld.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import net.risingworld.api.database.WorldDatabase;

public class GetBannedCommand implements CommandExecutor {

    static final String command = "banned";
    static final String fullCommand = "/" + command;
    static final String altCommand = "?" + command;
    static final String description = "tbd";

    static final de.omegazirkel.risingworld.tools.Logger log = new de.omegazirkel.risingworld.tools.Logger(
            "[OZ.DP] [JavaCordBot.Commands.GetBannedCommand]");

    @Command(aliases = { altCommand,
            fullCommand }, description = description, async = true, privateMessages = false, usage = "?banned", showInHelpPage = true)
    public String onCommand(TextChannel channel, Message message) {
        DiscordWebHook plugin = JavaCordBot.pluginInstance;
        String lang = plugin.getBotLanguage();
        I18n t = plugin.getTranslator();
        if (!RisingWorldCommandGuard.canUseCommand(command, message)) {
            return null;
        }

        WorldDatabase db = plugin.getWorldDatabase();
        try (ResultSet result = db.executeQuery("SELECT * FROM `Banlist`")) {
            List<String> list = Arrays.asList(t.get("CMD_OUT_BANNED_LIST", lang) + "\n");
            StringBuilder sb = new StringBuilder();
            list.forEach(sb::append);
            while (result.next()) {
                String name = result.getString("Playername");
                String reason = result.getString("Reason");
                long UID = result.getLong("UID");
                sb.append(name + " ( " + UID + " ) banned for: " + reason + "\n");
            }
            return sb.toString();
        } catch (SQLException e) {
            log.out(e.getMessage());
            message.addReaction("👎");
            return null;
        }
    }
}
