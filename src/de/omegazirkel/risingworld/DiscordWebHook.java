/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.omegazirkel.risingworld;

import de.omegazirkel.tools.FileChangeListener;
import de.omegazirkel.tools.PluginChangeWatcher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.risingworld.api.Plugin;
import net.risingworld.api.Server;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerChatEvent;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.events.player.PlayerConnectEvent;
import net.risingworld.api.events.player.PlayerDisconnectEvent;
import net.risingworld.api.events.player.PlayerSpawnEvent;
import net.risingworld.api.objects.Player;
import net.risingworld.api.utils.Vector3f;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.json.simple.JSONObject;

/**
 *
 * @author Maik "Devidian" Laschober
 */
public class DiscordWebHook extends Plugin implements Listener, FileChangeListener {

    static final String pluginVersion = "0.10.0";
    static final String pluginName = "DiscordPlugin";

    static final String colorError = "[#FF0000]";
    static final String colorWarning = "[#808000]";
    static final String colorOkay = "[#00FF00]";
    static final String colorText = "[#EEEEEE]";
    static final String colorCommand = "[#997d4a]";

    static final String colorSupport = "[#782d8e]";
    static final String colorLocalSelf = "[#ddffdd]";
    static final String colorLocalOther = "[#dddddd]";
    static final String colorLocalDiscord = "[#ddddff]";

    // Settings
    static int logLevel = 0;
    static boolean postChat = false;
    static String webHookChatUrl = "";
    static String joinDiscord = "";

    static boolean postSupport = false;
    static String webHookSupportUrl = "";
    static boolean addTeleportCommand = true;

    static boolean postStatus = false;
    static boolean useServerName = false;
    static boolean reportStatusEnabled = false;
    static boolean reportStatusDisabled = false;
    static boolean reportSettingsChanged = true;
    static boolean reportJarChanged = true;
    static String statusUsername = "My Server";
    static String statusEnabledMessage = "My Server is now online";
    static String statusDisabledMessage = "My Server is shutting down";
    static String webHookStatusUrl = "";

    static boolean sendMOTD = false;
    static String motd = "Welcome this server uses Omega-Zirkel Discord Plugin";

    static boolean botEnable = false;
    static boolean botSecure = true;
    static String botToken = "";
    static String botChatChannelName = "server-chat";

    static boolean allowRestart = false;
    static boolean restartOnUpdate = true;
    static int restartMinimumTime = 86400;// (60 * 60 * 24); // 1 Day default

    // END Settings
    // Live properties
    static boolean flagRestart = false;
    static Plugin GlobalIntercom = null;

    @Override
    public void onEnable() {
        // Register event listener
        registerEventListener(this);
        GlobalIntercom = getPluginByName("Omega Zirkel Global Intercom Plugin");
        if (GlobalIntercom != null) {
            log("Global Intercom found! ID: " + GlobalIntercom.getID(), 0);
        }
        this.initSettings();
        if (reportStatusEnabled) {
            Server server = getServer();
            String username = statusUsername;
            if (useServerName) {
                username = server.getName();
            }
            this.sendDiscordMessage(username, statusEnabledMessage, webHookStatusUrl);
        }
        try {

            PluginChangeWatcher WU = new PluginChangeWatcher(this);
            File f = new File(getPath());
            WU.watchDir(f);
            WU.startListening();
        } catch (IOException ex) {
            log(ex.getMessage(), 999);
        }

        if (!botEnable) {
            log("DiscordBot is disabled", 0);
            return;
        }
        Runnable ra = () -> {
            log("DiscordBot is enabled", 0);
            DiscordApi api = new DiscordApiBuilder().setToken(botToken).login().join();
            Server server = getServer();
            api.addMessageCreateListener(event -> {
                String content = event.getMessageContent();
                MessageAuthor author = event.getMessageAuthor();
                TextChannel channel = event.getChannel();
                boolean isUserNotBot = author.isUser() && !author.isYourself();
                if (!isUserNotBot) {
                    return; // Do not react to Bot messages
                }
                boolean isSecureCommand = content.startsWith("/");
                boolean isCommand = content.startsWith("!");
                boolean canExecuteSecureCommands = !botSecure || author.isBotOwner();

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

                            player.sendTextMessage(colorSupport + "[SUPPORT] " + author.getDisplayName() + ": "
                                    + colorText + parts[2]);
                        } else if (content.contentEquals("/restart")) {
                            int playersLeft = server.getPlayerCount();
                            if (playersLeft == 0) {
                                channel.sendMessage("No Player online, executing shutdown");
                                server.shutdown();
                            } else {
                                channel.sendMessage(playersLeft + " Player(s) online, restart flag set");
                                getServer().broadcastTextMessage("[#FF8000]" + author.getDiscriminatedName()
                                        + " set restart flag. Server will shutdown after last player has left the server!");
                                flagRestart = true;
                            }
                        }
                    } else {
                        channel.sendMessage(
                                "You are not allowed to execute secure commands. Type `!help` for more info");
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
                    if(chName.equalsIgnoreCase(botChatChannelName)){
                        server.broadcastTextMessage(colorLocalDiscord+"[LOCAL] "+author.getDiscriminatedName()+": "+colorText+content);
                    }
                }
            });
        };
        ra.run();
    }

    @EventMethod
    public void onPlayerCommand(PlayerCommandEvent event) {
        Player player = event.getPlayer();
        String command = event.getCommand();
        Vector3f pos = player.getPosition();

        String[] cmd = command.split(" ");

        if (cmd[0].equals("/support")) {
            String message = command.substring(9); // remove the first 14 characters

            if (postSupport) {
                String supportMessage = "```" + player.getName() + ": " + message;
                if (addTeleportCommand) {
                    supportMessage += "\nTeleport command:> goto " + pos.x + " " + pos.y + " " + pos.z;
                }
                supportMessage += "```";
                this.sendDiscordMessage("SupportTicket", supportMessage, webHookSupportUrl);
                player.sendTextMessage("[#FFFF00]Your support ticket was sent to Discord!");
            } else {
                player.sendTextMessage("[#FF0000]Discord support not available");
            }
        } else if (cmd[0].equals("/joinDiscord")) {
            if (joinDiscord.isEmpty()) {
                player.sendTextMessage("[#FF0000]/joinDiscord not configured");
            } else {
                player.connectToDiscord("https://discord.gg/" + joinDiscord);
            }
        } else if (cmd[0].equals("/ozrestart")) {
            boolean canTriggerRestart = player.isAdmin()
                    || (allowRestart && player.getTotalPlayTime() > restartMinimumTime && restartMinimumTime > 0);
            if (canTriggerRestart) {
                String username = statusUsername;
                if (useServerName) {
                    Server server = getServer();
                    username = server.getName();
                }
                this.sendDiscordMessage(username,
                        player.getName()
                                + " set restart flag. Server will shutdown after last player has left the server!",
                        webHookStatusUrl);
                getServer().broadcastTextMessage("[#FF8000]" + player.getName()
                        + " set restart flag. Server will shutdown after last player has left the server!");
                flagRestart = true;
            } else {
                player.sendTextMessage("[#FF0000]You are not allowed to trigger Server restart!");
            }
        } else if (true) {
            // log("Player " + player.getName() + " used command: " + cmd[0], 0);
        }

    }

    /**
     *
     * @param event
     */
    @EventMethod
    public void onPlayerChat(PlayerChatEvent event) {

        String message = event.getChatMessage();
        String noColorText = message.replaceFirst("(\\[#[a-fA-F]+\\])", "");
        Boolean processMessage = postChat;

        if (!processMessage) {
            return;
        }

        if (GlobalIntercom != null) {
            processMessage = !((GlobalIntercom) GlobalIntercom).isGIMessage(event);
        }

        if (processMessage) {
            Player player = event.getPlayer();
            this.sendDiscordMessage(player.getName(), noColorText, webHookChatUrl);
            broadcastMessage(player,noColorText);
        }
    }

    /**
     *
     * @param eventPlayer
     * @param noColorText
     */
    private void broadcastMessage(Player eventPlayer,String noColorText) {
		getServer().getAllPlayers().forEach((player) -> {
			String color = colorLocalOther;
			if (player.getUID() == eventPlayer.getUID()) {
				color = colorLocalSelf;
			}

            player.sendTextMessage(color+"[LOCAL] "+player.getName()+": "+colorText+noColorText);
		});
	}

    /**
     *
     * @param event
     */
    @EventMethod
    public void onPlayerSpawn(PlayerSpawnEvent event) {
        if (sendMOTD) {
            Player player = event.getPlayer();
            player.sendTextMessage(motd);
        }
    }

    /**
     *
     * @param event
     */
    @EventMethod
    public void onPlayerConnect(PlayerConnectEvent event) {
        if (postStatus) {
            Player player = event.getPlayer();
            String username = statusUsername;
            if (useServerName) {
                Server server = getServer();
                username = server.getName();
            }
            this.sendDiscordMessage(username, "Player " + player.getName() + " has connected to the server",
                    webHookStatusUrl);
        }
    }

    /**
     *
     * @param event
     */
    @EventMethod
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        if (postStatus) {
            Player player = event.getPlayer();
            Server server = getServer();
            String username = statusUsername;
            if (useServerName) {
                username = server.getName();
            }
            this.sendDiscordMessage(username, "Player " + player.getName() + " has disconnected from the server",
                    webHookStatusUrl);
            if (flagRestart) {
                int playersLeft = server.getPlayerCount() - 1;
                if (playersLeft == 0) {
                    this.sendDiscordMessage(username, "Last player left the server, shutdown now", webHookStatusUrl);
                    server.shutdown();
                } else if (playersLeft > 1) {
                    server.broadcastTextMessage(
                            "[#FF8000]" + playersLeft + " players left, shutdown waiting for all players to leave.");
                }
            }
        }
    }

    /**
     *
     */
    @Override
    public void onDisable() {
        log("OmegaZirkel Discord Plugin deactivated", 10);
        if (reportStatusDisabled) {
            Server server = getServer();
            String username = statusUsername;
            if (useServerName) {
                username = server.getName();
            }
            this.sendDiscordMessage(username, statusDisabledMessage, webHookStatusUrl);
        }
    }

    /**
     *
     * @param username
     * @param text
     */
    private void sendDiscordMessage(String username, String text, String channel) {
        try {
            // Username Validation
            username = username.replace("@", "").replace("@", "").replace(":", "").replace("`", ""); // remove invalid
                                                                                                     // chars

            if (username.length() < 2) {
                username = username + "__";
            }
            if (username.length() > 32) {
                username = username.substring(0, 31);
            }
            JSONObject json = new JSONObject();

            json.put("content", text);
            json.put("username", username);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(channel);
            StringEntity stringObject = new StringEntity(json.toJSONString(), "UTF-8");
            post.setHeader("Content-type", "application/json; charset=UTF-8");
            post.setEntity(stringObject);
            HttpResponse response = httpClient.execute(post);
            int status = response.getStatusLine().getStatusCode();
            if (status != 204) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                log("HTTP Status: " + status + "\nResponse: " + responseString + "\nRequest was: " + stringObject, 0);
            }
        } catch (IOException ex) {
            log("IOException on sendDiscordMessage: " + ex.getMessage(), 100);
        } catch (UnsupportedCharsetException | ParseException ex) {
            log("Exception on sendDiscordMessage: " + ex.getMessage(), 100);
        }
    }

    /**
     *
     */
    private void initSettings() {
        Properties settings = new Properties();
        FileInputStream in;
        try {
            in = new FileInputStream(getPath() + "/settings.properties");
            settings.load(in);
            in.close();
            // fill global values
            logLevel = Integer.parseInt(settings.getProperty("logLevel"));
            // log(settings.getProperty("webHookUrl"),0);
            postChat = settings.getProperty("postChat").contentEquals("true");
            webHookChatUrl = settings.getProperty("webHookChatUrl");
            joinDiscord = settings.getProperty("joinDiscord");

            postStatus = settings.getProperty("postStatus").contentEquals("true");
            reportStatusEnabled = settings.getProperty("reportStatusEnabled").contentEquals("true");
            reportStatusDisabled = settings.getProperty("reportStatusDisabled").contentEquals("true");
            reportSettingsChanged = settings.getProperty("reportSettingsChanged").contentEquals("true");
            reportJarChanged = settings.getProperty("reportJarChanged").contentEquals("true");
            webHookStatusUrl = settings.getProperty("webHookStatusUrl");
            statusUsername = settings.getProperty("statusUsername");
            statusEnabledMessage = settings.getProperty("statusEnabledMessage");
            statusDisabledMessage = settings.getProperty("statusDisabledMessage");
            useServerName = settings.getProperty("useServerName").contentEquals("true");

            postSupport = settings.getProperty("postSupport").contentEquals("true");
            addTeleportCommand = settings.getProperty("addTeleportCommand").contentEquals("true");
            webHookSupportUrl = settings.getProperty("webHookSupportUrl");

            botEnable = settings.getProperty("botEnable").contentEquals("true");
            botSecure = settings.getProperty("botSecure").contentEquals("true");
            botToken = settings.getProperty("botToken");

            // motd settings
            sendMOTD = settings.getProperty("sendMOTD").contentEquals("true");
            motd = settings.getProperty("motd");

            // restart settings
            allowRestart = settings.getProperty("allowRestart").contentEquals("true");
            restartOnUpdate = settings.getProperty("restartOnUpdate").contentEquals("true");
            restartMinimumTime = Integer.parseInt(settings.getProperty("restartMinimumTime"));
            log("OmegaZirkel Discord Plugin is enabled", 10);

            log("Will send chat to Discord: " + String.valueOf(postChat), 10);
            log("Will send status to Discord: " + String.valueOf(postStatus), 10);
            log("Will send support tickets to Discord: " + String.valueOf(postSupport), 10);
            log("Sending motd on login is: " + String.valueOf(sendMOTD), 10);
            log("motd is: " + String.valueOf(motd), 10);

        } catch (IOException ex) {
            log("IOException on initSettings: " + ex.getMessage(), 100);
            // e.printStackTrace();
        } catch (NumberFormatException ex) {
            log("NumberFormatException on initSettings: " + ex.getMessage(), 100);
        } catch (Exception ex) {
            log("Exception on initSettings: " + ex.getMessage(), 100);
        }
    }

    /**
     * log text to server output todo: use output level to filter messages
     *
     * @param text  the message to print in server logs
     * @param level message level higher level means higher priority
     */
    private static void log(String text, int level) {
        if (level >= logLevel) {
            System.out.println("[OZ.DP] " + text);
        }
    }

    /**
     *
     * @param filename
     */
    @Override
    public void onFileChangeEvent(Path file) {
        if (file.getFileName().equals("settings.properties")) {
            log("Settings file was changed, reloading settings now", 10);
            if (reportSettingsChanged) {
                Server server = getServer();
                String username = statusUsername;
                if (useServerName) {
                    username = server.getName();
                }
                this.sendDiscordMessage(username, "settings.properties has changed, reloading", webHookStatusUrl);
            }
            this.initSettings();
        } else if (file.endsWith("jar")) {
            log(file.getFileName() + " file was changed, set restart flag (or restart if no player online)", 10);
            Server server = getServer();
            if (reportJarChanged) {
                String username = statusUsername;
                if (useServerName) {
                    username = server.getName();
                }
                if (restartOnUpdate) {

                    if (server.getPlayerCount() > 0) {
                        this.sendDiscordMessage(username, file.getFileName() + " has changed, restart flag set",
                                webHookStatusUrl);
                        getServer().broadcastTextMessage(
                                "[#FF8000][OZDP] Restart flag was set due to file changes (Plugin update). Server will shutdown after last player has left the server!");
                    } else {
                        this.sendDiscordMessage(username,
                                file.getFileName() + " has changed, restarting server (no players online)",
                                webHookStatusUrl);
                    }

                } else {
                    this.sendDiscordMessage(username, file.getFileName() + " has changed, but restartOnUpdate is false",
                            webHookStatusUrl);
                }
            }
            if (restartOnUpdate) {
                if (server.getPlayerCount() > 0) {
                    flagRestart = true;
                } else {
                    server.shutdown();
                }
            }
        } else {
            log("File changed: <" + file.getFileName() + ">", 0);
        }
    }
}
