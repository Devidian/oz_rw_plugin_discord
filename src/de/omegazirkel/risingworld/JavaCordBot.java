package de.omegazirkel.risingworld;

import java.util.Arrays;
import java.util.List;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.listener.message.MessageCreateListener;

import net.risingworld.api.Server;
import net.risingworld.api.objects.Player;

public class JavaCordBot implements Runnable {

    private static DiscordWebHook pluginInstance = null;
    private static DiscordApi api = null;
    private static boolean running = false;

    private MessageCreateListener messageCreateListener = null;

    public JavaCordBot(final DiscordWebHook plugin) {
        pluginInstance = plugin;
    }
    
    public void disconnect() {
        DiscordWebHook.log("DiscordBot is now disconnecting", 0);
        api.removeListener(messageCreateListener);
        api.disconnect();
    }
    
    @Override
    public void run() {
        if(running){
            return; // do not execute more than once
        }
        running = true;
        DiscordWebHook.log("DiscordBot is now running", 0);
        api = new DiscordApiBuilder().setToken(pluginInstance.getBotToken()).login().join();
        Server server = pluginInstance.getServer();
        messageCreateListener = event -> {
            String content = event.getMessageContent();
            MessageAuthor author = event.getMessageAuthor();
            TextChannel channel = event.getChannel();
            boolean isUserNotBot = author.isUser() && !author.isYourself();
            if (!isUserNotBot) {
                return; // Do not react to Bot messages
            }
            boolean isSecureCommand = content.startsWith("/");
            boolean isCommand = content.startsWith("!");
            boolean canExecuteSecureCommands = !pluginInstance.getBotSecure() || author.isBotOwner();

            if (isSecureCommand) {
                if (canExecuteSecureCommands) {
                    if (content.startsWith("/support")) {
                        String[] parts = content.split(" ", 3);
                        if (parts.length < 3) {
                            channel.sendMessage("Wrong number of Arguments, use `/support PLAYERNAME TEXT...`");
                            return;
                        }
                        String playerName = parts[1];
                        Player player = server.getPlayer(playerName);
                        if (player == null) {
                            channel.sendMessage("Player with name " + playerName + " not online.");
                            return;
                        }

                        player.sendTextMessage(pluginInstance.getColorSupport() + "[SUPPORT] " + author.getDisplayName()
                                + ": " + pluginInstance.getColorText() + parts[2]);
                    } else if (content.contentEquals("/restart")) {
                        int playersLeft = server.getPlayerCount();
                        if (playersLeft == 0) {
                            channel.sendMessage("No Player online, executing shutdown");
                            server.shutdown();
                        } else {
                            channel.sendMessage(playersLeft + " Player(s) online, restart flag set");
                            pluginInstance.getServer().broadcastTextMessage("[#FF8000]" + author.getDiscriminatedName()
                                    + " set restart flag. Server will shutdown after last player has left the server!");
                            pluginInstance.setFlagRestart(true);
                        }
                    }
                } else {
                    channel.sendMessage("You are not allowed to execute secure commands. Type `!help` for more info");
                }
            } else if (isCommand) {
                if (content.contentEquals("!help")) {
                    channel.sendMessage("Currently available commands:\n```\n"
                            + "!help                         | shows this message\n"
                            + "!online                       | shows a list of players that are currently online\n"
                            + "SECURE COMMANDS:\n"
                            + "/restart                      | sets restart flag if there are any players online or executes shutdown server\n"
                            + "/support [PLAYERNAME] [TEXT]  | send a TEXT to PLAYERNAME as [SUPPORT] message\n"
                            + "```");
                } else if (content.contentEquals("!online")) {
                    int playersOnline = server.getPlayerCount();
                    if (playersOnline == 0) {
                        channel.sendMessage("No Players online");
                    } else {
                        List<String> list = Arrays.asList("Currently online:\n");
                        StringBuilder sb = new StringBuilder();
                        list.forEach(sb::append);
                        server.getAllPlayers().forEach((Player p) -> {
                            sb.append(p.getName() + "\n");
                        });
                        channel.sendMessage(sb.toString());
                    }
                }
            } else {
                // Not a (secure) command, maybe chat? check channel
                String chName = event.getChannel().asServerChannel().map(ServerChannel::getName).orElse(null);
                if (chName.equalsIgnoreCase(pluginInstance.getBotChatChannelName())) {
                    server.broadcastTextMessage(pluginInstance.getColorLocalDiscord() + "[LOCAL] "
                            + author.getDiscriminatedName() + ": " + pluginInstance.getColorText() + content);
                }
            }
        };
        api.addMessageCreateListener(messageCreateListener);
    }

}