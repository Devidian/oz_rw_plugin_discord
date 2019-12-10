package de.omegazirkel.risingworld;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.listener.GloballyAttachableListener;
import org.javacord.api.listener.message.MessageCreateListener;

import de.omegazirkel.risingworld.tools.Colors;
import de.omegazirkel.risingworld.tools.I18n;
import net.risingworld.api.Server;
// import net.risingworld.api.database.Database;
import net.risingworld.api.database.WorldDatabase;
import net.risingworld.api.objects.Player;
import net.risingworld.api.objects.Weather;
import net.risingworld.api.objects.Time.Unit;

public class JavaCordBot implements Runnable {

    private static DiscordWebHook pluginInstance = null;
    private static DiscordApi api = null;
    private static boolean running = false;

    static final de.omegazirkel.risingworld.tools.Logger log = new de.omegazirkel.risingworld.tools.Logger(
            "[OZ.DP] [JavaCordBot]");
    static final Colors c = Colors.getInstance();
    private static I18n t = null;

    private MessageCreateListener messageCreateListener = null;

    public JavaCordBot(final DiscordWebHook plugin) {
        pluginInstance = plugin;
		t = new I18n(plugin);
		Thread.currentThread().setName("DP.JavaCordBot");
    }

    public void disconnect() {
        log.out("DiscordBot is now disconnecting", 0);
        api.getListeners().forEach((GloballyAttachableListener entry, List<Class<GloballyAttachableListener>> list) -> {
            api.removeListener(entry);
        });
        api.disconnect();
    }

    @Override
    public void run() {
        if (running) {
            return; // do not execute more than once
        }
        String lang = pluginInstance.getBotLanguage();
        running = true;
        log.out("DiscordBot is now running", 0);
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
            boolean isCommand = content.startsWith("/") || content.startsWith("?");
            boolean canExecuteSecureCommands = !pluginInstance.getBotSecure() || author.isBotOwner()
                    || pluginInstance.getBotAdmins().contains(author.getDiscriminatedName());

            if (isCommand) {
                String[] parts = content.split(" ", 3);
                String command = parts[0].substring(1).toLowerCase(); // remove start sequence
                if (!DiscordWebHook.discordCommands.containsKey(command)) {
                    // unknown command
                    event.addReactionToMessage("⁉");
                } else {
                    Short commandLevel = DiscordWebHook.discordCommands.get(command);
                    if (commandLevel == 0) {
                        // command disabled
                        channel.sendMessage(t.get("CMD_ERR_DISABLED", lang).replace("PH_CMD", command));
                        event.addReactionToMessage("✋");
                    } else if (commandLevel > 1 && !canExecuteSecureCommands) {
                        // command for admins, user not allowed
                        channel.sendMessage(t.get("CMD_ERR_ADMIN_ONLY", lang).replace("PH_CMD", command));
                        event.addReactionToMessage("✋");
                    } else {
                        // commandLevel is 1 or user canExecuteSecureCommands
                        if (command.equalsIgnoreCase("support")) {
                            if (parts.length < 3) {
                                channel.sendMessage(t.get("CMD_ERR_SUPPORT_ARGUMENTS", lang));
                                return;
                            }
                            String playerName = parts[1];
                            Player player = server.getPlayer(playerName);
                            if (player == null) {
                                channel.sendMessage(
                                        t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName));
                                return;
                            }

                            player.sendTextMessage(pluginInstance.getColorSupport() + "[SUPPORT] "
                                    + author.getDisplayName() + ": " + pluginInstance.getColorText() + parts[2]);
                            event.addReactionToMessage("✔");
                        } else if (command.equalsIgnoreCase("bc")) {
                            if (parts.length < 3) {
                                channel.sendMessage(t.get("CMD_ERR_BC_ARGUMENTS", lang));
                                return;
                            }
                            String type = parts[1];

                            server.broadcastTextMessage(pluginInstance.getColorSupport() + "[" + type + "] "
                                    + author.getDisplayName() + ": " + pluginInstance.getColorText() + parts[2]);
                            event.addReactionToMessage("✔");
                        } else if (command.equalsIgnoreCase("yell")) {
                            if (parts.length < 2) {
                                channel.sendMessage(t.get("CMD_ERR_YELL_ARGUMENTS", lang));
                                return;
                            }
                            server.broadcastYellMessage(parts[1]);
                            event.addReactionToMessage("✔");
                        } else if (command.equalsIgnoreCase("kick")) {
                            if (parts.length < 2) {
                                channel.sendMessage(t.get("CMD_ERR_KICK_ARGUMENTS", lang));
                                return;
                            }
                            String playerName = parts[1];
                            String reason = parts.length > 2 ? parts[2] : null;
                            Player player = server.getPlayer(playerName);
                            if (player == null) {
                                channel.sendMessage(
                                        t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName));
                                return;
                            }
                            player.kick(reason);
                            channel.sendMessage("Player " + playerName + " kicked!");
                            pluginInstance.getServer().getAllPlayers().forEach((p) -> {
                                String l = p.getSystemLanguage();
                                p.sendTextMessage(c.warning + DiscordWebHook.pluginName + ":>" + c.text
                                        + t.get("BC_KICKED", l).replace("PH_PLAYER", playerName)
                                                .replace("PH_DISCORDUSER", author.getDiscriminatedName())
                                                .replace("PH_REASON", reason));
                            });
                            event.addReactionToMessage("✔");
                        } else if (command.equalsIgnoreCase("ban")) {
                            if (parts.length < 2) {
                                channel.sendMessage(t.get("CMD_ERR_BAN_ARGUMENTS", lang));
                                return;
                            }
                            String playerName = parts[1];
                            String reason = parts.length > 2 ? parts[2] : null;
                            Player player = server.getPlayer(playerName);
                            if (player == null) {
                                channel.sendMessage(
                                        t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName));
                                return;
                            }
                            // TODO: add duration
                            player.ban(reason);
                            channel.sendMessage("Player " + playerName + " banned!");
                            pluginInstance.getServer().getAllPlayers().forEach((p) -> {
                                String l = p.getSystemLanguage();
                                p.sendTextMessage(c.warning + DiscordWebHook.pluginName + ":>" + c.text
                                        + t.get("BC_BANNED", l).replace("PH_PLAYER", playerName)
                                                .replace("PH_DISCORDUSER", author.getDiscriminatedName())
                                                .replace("PH_REASON", reason));
                            });
                            event.addReactionToMessage("✔");
                        } else if (command.equalsIgnoreCase("unban")) {
                            if (parts.length < 2) {
                                channel.sendMessage(
                                        t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD", "/unban [UID]"));
                                return;
                            }
                            try {
                                long uid = Long.parseLong(parts[1]);
                                server.unbanPlayer(uid);
                            } catch (Exception e) {
                                event.addReactionToMessage("👎");
                            }
                        } else if (command.equalsIgnoreCase("group")) {
                            if (parts.length < 3) {
                                channel.sendMessage(t.get("CMD_ERR_GROUP_ARGUMENTS", lang));
                                return;
                            }
                            String playerName = parts[1];
                            String group = parts[2];
                            Player player = server.getPlayer(playerName);
                            if (player == null) {
                                channel.sendMessage(
                                        t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName));
                                return;
                            }
                            player.setPermissionGroup(group);

                            channel.sendMessage(t.get("CMD_OUT_GROUP", lang).replace("PH_PLAYER", playerName)
                                    .replace("PH_GROUP", group));
                            pluginInstance.getServer().getAllPlayers().forEach((p) -> {
                                String l = p.getSystemLanguage();
                                p.sendTextMessage(c.warning + DiscordWebHook.pluginName + ":>" + c.text
                                        + t.get("BC_GROUP", l).replace("PH_DISCORDUSER", author.getDiscriminatedName())
                                                .replace("PH_PLAYER", playerName).replace("PH_GROUP", group));
                            });
                            event.addReactionToMessage("✔");
                        } else if (command.equalsIgnoreCase("restart")) {
                            int playersLeft = server.getPlayerCount();
                            if (playersLeft == 0) {
                                channel.sendMessage(t.get("CMD_OUT_RESTART_NOW", lang));
                                event.addReactionToMessage("✔");
                                server.shutdown();
                            } else {
                                channel.sendMessage(
                                        t.get("CMD_OUT_RESTART_DELAY", lang).replace("PH_PLAYERS", playersLeft + ""));
                                pluginInstance.getServer().getAllPlayers().forEach((p) -> {
                                    String l = p.getSystemLanguage();
                                    p.sendTextMessage(
                                            c.warning + DiscordWebHook.pluginName + ":>" + c.text + t.get("BC_GROUP", l)
                                                    .replace("PH_DISCORDUSER", author.getDiscriminatedName()));
                                });
                                pluginInstance.setFlagRestart(true);
                                event.addReactionToMessage("✔");
                            }
                        } else if (command.equalsIgnoreCase("setweather")) {
                            if (parts.length < 2) {
                                channel.sendMessage(
                                        t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD", "/weather [Weather]"));
                                return;
                            }
                            String next = parts[1];
                            try {
                                Weather newWeather = Weather.valueOf(next);
                                server.setWeather(newWeather, false);
                                event.addReactionToMessage("✔");

                            } catch (IllegalArgumentException e) {
                                StringBuilder sb = new StringBuilder();
                                for (Weather w : Weather.values()) {
                                    sb.append(w.toString() + "\n");
                                }
                                channel.sendMessage(t.get("CMD_ERR_ILLEGAL_ARGUMENTS", lang)
                                        .replace("PH_CMD", "/setweather [Weather]").replace("PH_ARGUMENT", "Weather")
                                        .replace("PH_ARGS_AVAILABLE", sb.toString()));
                            } catch (NullPointerException e) {
                                log.out(e.getMessage());
                            }
                        } else if (command.equalsIgnoreCase("settime")) {
                            if (parts.length < 3) {
                                channel.sendMessage(t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD",
                                        "/settime [HOUR] [MINUTE]"));
                                return;
                            }
                            try {
                                int hour = Integer.parseInt(parts[1]);
                                int minute = Integer.parseInt(parts[1]);
                                server.setGameTime(hour, minute);
                                event.addReactionToMessage("✔");
                            } catch (Exception e) {
                                event.addReactionToMessage("👎");
                            }
                        } else if (command.equalsIgnoreCase("sethealth")) {
                            if (parts.length < 3) {
                                channel.sendMessage(t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD",
                                        "/sethealth [PLAYER] [VALUE]"));
                                return;
                            }
                            try {
                                String playerName = parts[1];
                                int value = Integer.parseInt(parts[2]);
                                Player player = server.getPlayer(playerName);
                                if (player == null) {
                                    channel.sendMessage(
                                            t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName));
                                    return;
                                }
                                // player.getHealth();
                                player.setHealth(value);
                                event.addReactionToMessage("✔");
                            } catch (Exception e) {
                                event.addReactionToMessage("👎");
                            }
                        } else if (command.equalsIgnoreCase("sethunger")) {
                            if (parts.length < 3) {
                                channel.sendMessage(t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD",
                                        "/sethunger [PLAYER] [VALUE]"));
                                return;
                            }
                            try {
                                String playerName = parts[1];
                                int value = Integer.parseInt(parts[2]);
                                Player player = server.getPlayer(playerName);
                                if (player == null) {
                                    channel.sendMessage(
                                            t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName));
                                    return;
                                }
                                player.setHunger(value);
                                event.addReactionToMessage("✔");
                            } catch (Exception e) {
                                event.addReactionToMessage("👎");
                            }
                        } else if (command.equalsIgnoreCase("setthirst")) {
                            if (parts.length < 3) {
                                channel.sendMessage(t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD",
                                        "/setthirst [PLAYER] [VALUE]"));
                                return;
                            }
                            try {
                                String playerName = parts[1];
                                int value = Integer.parseInt(parts[2]);
                                Player player = server.getPlayer(playerName);
                                if (player == null) {
                                    channel.sendMessage(
                                            t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName));
                                    return;
                                }
                                player.setThirst(value);
                                event.addReactionToMessage("✔");
                            } catch (Exception e) {
                                event.addReactionToMessage("👎");
                            }
                        } else if (command.equalsIgnoreCase("mkadmin")) {
                            if (parts.length < 2) {
                                channel.sendMessage(
                                        t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD", "/mkadmin [PLAYER]"));
                                return;
                            }
                            try {
                                String playerName = parts[1];
                                Player player = server.getPlayer(playerName);
                                if (player == null) {
                                    channel.sendMessage(
                                            t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName));
                                    return;
                                }
                                player.setAdmin(true);
                                event.addReactionToMessage("✔");
                            } catch (Exception e) {
                                event.addReactionToMessage("👎");
                            }
                        } else if (command.equalsIgnoreCase("unadmin")) {
                            if (parts.length < 2) {
                                channel.sendMessage(
                                        t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD", "/unadmin [PLAYER]"));
                                return;
                            }
                            try {
                                String playerName = parts[1];
                                Player player = server.getPlayer(playerName);
                                if (player == null) {
                                    channel.sendMessage(
                                            t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", playerName));
                                    return;
                                }
                                player.setAdmin(false);
                                event.addReactionToMessage("✔");
                            } catch (Exception e) {
                                event.addReactionToMessage("👎");
                            }
                        } else if (command.equalsIgnoreCase("tptp")) {
                            if (parts.length < 3) {
                                channel.sendMessage(t.get("CMD_ERR_ARGUMENT_LENGTH", lang).replace("PH_CMD",
                                        "/tptp [PLAYERNAME] [PLAYERNAME]"));
                                return;
                            }
                            try {
                                String player1Name = parts[1];
                                String player2Name = parts[2];
                                Player player = server.getPlayer(player1Name);
                                Player targetPlayer = server.getPlayer(player2Name);
                                if (player == null) {
                                    channel.sendMessage(
                                            t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", player1Name));
                                    return;
                                }
                                if (targetPlayer == null) {
                                    channel.sendMessage(
                                            t.get("CMD_ERR_PLAYER_OFFLINE", lang).replace("PH_PLAYER", player2Name));
                                    return;
                                }
                                player.setPosition(targetPlayer.getPosition());
                                event.addReactionToMessage("✔");
                            } catch (Exception e) {
                                event.addReactionToMessage("👎");
                            }
                        } else if (command.equalsIgnoreCase("help")) {
                            channel.sendMessage(t.get("DISCORD_HELP", lang));
                        } else if (command.equalsIgnoreCase("online")) {
                            int playersOnline = server.getPlayerCount();
                            if (playersOnline == 0) {
                                channel.sendMessage(t.get("CMD_OUT_ONLINE_NOBODY", lang));
                            } else {
                                List<String> list = Arrays.asList(t.get("CMD_OUT_ONLINE_LIST", lang) + "\n");
                                StringBuilder sb = new StringBuilder();
                                list.forEach(sb::append);
                                server.getAllPlayers().forEach((Player p) -> {
                                    sb.append(p.getName() + "\n");
                                });
                                channel.sendMessage(sb.toString());
                            }
                        } else if (command.equalsIgnoreCase("version")) {
                            channel.sendMessage("Plugin version: " + DiscordWebHook.pluginVersion);
                        } else if (command.equalsIgnoreCase("weather")) {
                            channel.sendMessage("Current weather: " + server.getCurrentWeather() + "\nNext weather: "
                                    + server.getNextWeather());
                        } else if (command.equalsIgnoreCase("time")) {
                            channel.sendMessage("Current time: " + server.getGameTime(Unit.Hours) + ":"
                                    + server.getGameTime(Unit.Minutes));
                        } else if (command.equalsIgnoreCase("banned")) {
                            WorldDatabase db = pluginInstance.getWorldDatabase();
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
                                channel.sendMessage(sb.toString());
                            } catch (SQLException e) {
                                log.out(e.getMessage());
                            }
                        } else {
                            log.out("Command error for " + command + " on level " + commandLevel, 999);
                        }
                    }
                }

            } else {
                // Not a command, maybe chat? check channel
                String chName = event.getChannel().asServerChannel().map(ServerChannel::getName).orElse(null);
                if (chName.equalsIgnoreCase(pluginInstance.getBotChatChannelName())) {
                    String color = pluginInstance.getColorLocalDiscord();
                    String group = "";
                    if (canExecuteSecureCommands && pluginInstance.getShowGroupSetting()) {
                        color = pluginInstance.getColorLocalAdmin();
                        group = " (discord/admin)";
                    }
                    server.broadcastTextMessage(color + "[LOCAL] " + author.getDiscriminatedName() + group + ": "
                            + pluginInstance.getColorText() + content);
                } else {
                    log.out("Unknown message <" + content + "> in channel <" + chName + ">", 0);
                }
            }
        };
        api.addMessageCreateListener(messageCreateListener);
        api.addLostConnectionListener(event -> {
            log.out("Lost connection to Discord", 911);
        });
        api.addReconnectListener(event -> {
            log.out("Reconnect", 100);
        });
        api.addServerBecomesUnavailableListener(event -> {
            log.out("Server becomes unavailable...", 100);
        });
        api.addServerBecomesAvailableListener(event -> {
            log.out("Server becomes available...", 100);
        });
    }

}
