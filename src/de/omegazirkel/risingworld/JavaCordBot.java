package de.omegazirkel.risingworld;

import java.util.Arrays;
import java.util.List;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.listener.message.MessageCreateListener;

import de.omegazirkel.tools.I18n;
import net.risingworld.api.Server;
import net.risingworld.api.objects.Player;

public class JavaCordBot implements Runnable {

    private static DiscordWebHook pluginInstance = null;
    private static DiscordApi api = null;
    private static boolean running = false;
    private static I18n t = null;

    private MessageCreateListener messageCreateListener = null;

    public JavaCordBot(final DiscordWebHook plugin) {
        pluginInstance = plugin;
        t = new I18n(plugin);
    }

    public void disconnect() {
        DiscordWebHook.log("DiscordBot is now disconnecting", 0);
        api.removeListener(messageCreateListener);
        api.disconnect();
    }

    @Override
    public void run() {
        if (running) {
            return; // do not execute more than once
        }
        String lang = pluginInstance.getBotLanguage();
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
                    } else if (content.startsWith("/bc")) {
                        String[] parts = content.split(" ", 3);
                        if (parts.length < 3) {
                            channel.sendMessage("Wrong number of Arguments, use `/bc TYPE TEXT...`");
                            return;
                        }
                        String type = parts[1];

                        server.broadcastTextMessage(pluginInstance.getColorSupport() + "[" + type + "] "
                                + author.getDisplayName() + ": " + pluginInstance.getColorText() + parts[2]);
                    } else if (content.startsWith("/yell")) {
                        String[] parts = content.split(" ", 2);
                        if (parts.length < 2) {
                            channel.sendMessage("Wrong number of Arguments, use `/yell TEXT...`");
                            return;
                        }

                        server.broadcastYellMessage(parts[1]);
                    } else if (content.startsWith("/kick")) {
                        String[] parts = content.split(" ", 3);
                        if (parts.length < 2) {
                            channel.sendMessage("Wrong number of Arguments, use `/kick PLAYERNAME [reason?]`");
                            return;
                        }
                        String playerName = parts[1];
                        String reason = parts.length > 2 ? parts[2] : null;
                        Player player = server.getPlayer(playerName);
                        if (player == null) {
                            channel.sendMessage("Player with name " + playerName + " not online.");
                            return;
                        }
                        player.kick(reason);
                        channel.sendMessage("Player " + playerName + " kicked!");
                        pluginInstance.getServer().broadcastTextMessage("[#FF8000]" + author.getDiscriminatedName()
                                + " kicked player " + playerName + " reason: " + reason);
                    } else if (content.startsWith("/ban")) {
                        String[] parts = content.split(" ", 3);
                        if (parts.length < 2) {
                            channel.sendMessage("Wrong number of Arguments, use `/ban PLAYERNAME [reason?]`");
                            return;
                        }
                        String playerName = parts[1];
                        String reason = parts.length > 2 ? parts[2] : null;
                        Player player = server.getPlayer(playerName);
                        if (player == null) {
                            channel.sendMessage("Player with name " + playerName + " not online.");
                            return;
                        }
                        player.ban(reason);
                        channel.sendMessage("Player " + playerName + " banned!");
                        pluginInstance.getServer().broadcastTextMessage("[#FF8000]" + author.getDiscriminatedName()
                                + " banned player " + playerName + " reason: " + reason);
                    } else if (content.startsWith("/group")) {
                        String[] parts = content.split(" ", 3);
                        if (parts.length < 3) {
                            channel.sendMessage("Wrong number of Arguments, use `/group PLAYERNAME GROUP`");
                            return;
                        }
                        String playerName = parts[1];
                        String group = parts[2];
                        Player player = server.getPlayer(playerName);
                        if (player == null) {
                            channel.sendMessage("Player with name " + playerName + " not online.");
                            return;
                        }
                        player.setPermissionGroup(group);

                        channel.sendMessage("Player " + playerName + " Permission group is set to " + group);
                        pluginInstance.getServer().broadcastTextMessage("[#FF8000]" + author.getDiscriminatedName()
                                + " changed permission-group of " + playerName + " to " + group);

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
                    channel.sendMessage(t.get("CMD_HELP_TITLE", lang) + ":\n```\n" + "!help                         | "
                            + t.get("CMD_HELP_DESC_HELP", lang) + "\n" + "!version                      | "
                            + t.get("CMD_HELP_DESC_VERSION", lang) + "\n" + "!online                       | "
                            + t.get("CMD_HELP_DESC_ONLINE", lang) + "\n" + t.get("CMD_HELP_SECURE", lang) + ":\n"
                            + "/restart                      | " + t.get("CMD_HELP_DESC_RESTART", lang) + "\n"
                            + "/support [PLAYERNAME] [TEXT]  | " + t.get("CMD_HELP_DESC_SUPPORT", lang) + "\n"
                            + "/kick [PLAYERNAME] [REASON?]  | " + t.get("CMD_HELP_DESC_KICK", lang) + "\n"
                            + "/ban [PLAYERNAME] [REASON?]   | " + t.get("CMD_HELP_DESC_BAN", lang) + "\n"
                            + "/group [PLAYERNAME] [GROUP]   | " + t.get("CMD_HELP_DESC_GROUP", lang) + "\n"
                            + "/yell [TEXT]                  | " + t.get("CMD_HELP_DESC_YELL", lang) + "\n"
                            + "/bc [TYPE] [TEXT]             | " + t.get("CMD_HELP_DESC_BC", lang) + "\n" + "```");
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
                } else if (content.contentEquals("!version")) {
                    channel.sendMessage("Plugin version: " + DiscordWebHook.pluginVersion);
                }
            } else {
                // Not a (secure) command, maybe chat? check channel
                String chName = event.getChannel().asServerChannel().map(ServerChannel::getName).orElse(null);
                if (chName.equalsIgnoreCase(pluginInstance.getBotChatChannelName())) {
                    server.broadcastTextMessage(pluginInstance.getColorLocalDiscord() + "[LOCAL] "
                            + author.getDiscriminatedName() + ": " + pluginInstance.getColorText() + content);
                } else {
                    DiscordWebHook.log("Unknown message in channel <" + chName + ">", 0);
                }
            }
        };
        api.addMessageCreateListener(messageCreateListener);
    }

}