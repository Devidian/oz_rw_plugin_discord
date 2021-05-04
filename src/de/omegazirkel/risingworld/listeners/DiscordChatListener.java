package de.omegazirkel.risingworld.listeners;

import java.util.Arrays;

import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import de.btobastian.sdcf4j.CommandHandler;
import de.omegazirkel.risingworld.DiscordWebHook;
import de.omegazirkel.risingworld.JavaCordBot;
import de.omegazirkel.risingworld.tools.Colors;
import net.risingworld.api.Server;

public class DiscordChatListener implements MessageCreateListener {
    private static DiscordWebHook pluginInstance = null;

    static final de.omegazirkel.risingworld.tools.Logger log = new de.omegazirkel.risingworld.tools.Logger(
            "[OZ.DP] [JavaCordBot]");
    static final Colors c = Colors.getInstance();

    private final CommandHandler commandHandler;

    public DiscordChatListener(CommandHandler handler) {
        this.commandHandler = handler;
        pluginInstance = JavaCordBot.pluginInstance;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Server server = pluginInstance.getServer();

        log.out("messageCreateEvent", 0);
        String content = event.getMessageContent();
        MessageAuthor author = event.getMessageAuthor();
        boolean isUserNotBot = author.isUser() && !author.isYourself();
        if (!isUserNotBot) {
            return; // Do not react to Bot messages
        }

        boolean messageIsCommand = commandHandler.getCommands().stream()
                .flatMap(command -> Arrays.stream(command.getCommandAnnotation().aliases()))
                .anyMatch(alias -> event.getMessageContent().startsWith(alias));

        boolean isAdmin = author.isBotOwner() || pluginInstance.getBotAdmins().contains(author.getDiscriminatedName());

        if (messageIsCommand) {
            return;
        }
        // Not a command, maybe chat? check channel
        String chName = event.getChannel().asServerChannel().map(ServerChannel::getName).orElse(null);
        if (chName.equalsIgnoreCase(pluginInstance.getBotChatChannelName())) {
            String color = pluginInstance.getColorLocalDiscord();
            String group = "";
            if (isAdmin && pluginInstance.getShowGroupSetting()) {
                color = pluginInstance.getColorLocalAdmin();
                group = " (discord/admin)";
            }
            server.broadcastTextMessage(color + "[LOCAL] " + author.getDiscriminatedName() + group + ": "
                    + pluginInstance.getColorText() + content);
        } else {
            log.out("Unknown message <" + content + "> in channel <" + chName + ">", 0);
        }

    }

}
