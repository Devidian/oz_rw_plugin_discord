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
import org.json.simple.JSONObject;

/**
 *
 * @author Maik "Devidian" Laschober
 */
public class DiscordWebHook extends Plugin implements Listener, FileChangeListener {

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

    static boolean allowRestart = false;
    static boolean restartOnUpdate = true;
    static int restartMinimumTime = 86400;//(60 * 60 * 24); // 1 Day default

    // END Settings
    // Live properties
    static boolean flagRestart = false;

    @Override
    public void onEnable() {
        //Register event listener
        registerEventListener(this);
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
            this.log(ex.getMessage(), 999);
        }
    }

    @EventMethod
    public void onPlayerCommand(PlayerCommandEvent event) {
        Player player = event.getPlayer();
        String command = event.getCommand();
        Vector3f pos = player.getPosition();

        String[] cmd = command.split(" ");

        if (cmd[0].equals("/support")) {
            String message = command.substring(9);  //remove the first 14 characters

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
            boolean canTriggerRestart = player.isAdmin() || (allowRestart && player.getTotalPlayTime() > restartMinimumTime && restartMinimumTime > 0);
            if (canTriggerRestart) {
                String username = statusUsername;
                if (useServerName) {
                    Server server = getServer();
                    username = server.getName();
                }
                this.sendDiscordMessage(username, player.getName() + " set restart flag. Server will shutdown after last player has left the server!", webHookStatusUrl);
                getServer().broadcastTextMessage("[#FF8000]" + player.getName() + " set restart flag. Server will shutdown after last player has left the server!");
                flagRestart = true;
            } else {
                player.sendTextMessage("[#FF0000]You are not allowed to trigger Server restart!");
            }
        } else if (true) {
//            this.log("Player " + player.getName() + " used command: " + cmd[0], 0);
        }

    }

    /**
     *
     * @param event
     */
    @EventMethod
    public void onPlayerChat(PlayerChatEvent event) {
        String message = event.getChatMessage();
        
        // better would be checking if ozgi is installed too
        if (postChat && !message.startsWith("#")) {
            Player player = event.getPlayer();

            String noColorText = message.replaceFirst("(\\[#[a-fA-F]+\\])", "");
            this.sendDiscordMessage(player.getName(), noColorText, webHookChatUrl);
        }

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
            this.sendDiscordMessage(username, "Player " + player.getName() + " has connected to the server", webHookStatusUrl);
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
            this.sendDiscordMessage(username, "Player " + player.getName() + " has disconnected from the server", webHookStatusUrl);
            if (flagRestart) {
                int playersLeft = server.getPlayerCount() - 1;
                if (playersLeft == 0) {
                    this.sendDiscordMessage(username, "Last player left the server, shutdown now", webHookStatusUrl);
                    server.shutdown();
                } else if (playersLeft > 1) {
                    server.broadcastTextMessage("[#FF8000]" + playersLeft + " players left, shutdown waiting for all players to leave.");
                }
            }
        }
    }

    /**
     *
     */
    @Override
    public void onDisable() {
        this.log("OmegaZirkel Discord Plugin deactivated", 10);
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
            username = username.replace("@", "").replace("@", "").replace(":", "").replace("`", ""); // remove invalid chars

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
                this.log("HTTP Status: " + status + "\nResponse: " + responseString + "\nRequest was: " + stringObject, 0);
            }
        } catch (IOException ex) {
            this.log("IOException on sendDiscordMessage: " + ex.getMessage(), 100);
        } catch (UnsupportedCharsetException | ParseException ex) {
            this.log("Exception on sendDiscordMessage: " + ex.getMessage(), 100);
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
//            this.log(settings.getProperty("webHookUrl"),0);
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

            // motd settings
            sendMOTD = settings.getProperty("sendMOTD").contentEquals("true");
            motd = settings.getProperty("motd");

            // restart settings
            allowRestart = settings.getProperty("allowRestart").contentEquals("true");
            restartOnUpdate = settings.getProperty("restartOnUpdate").contentEquals("true");
            restartMinimumTime = Integer.parseInt(settings.getProperty("restartMinimumTime"));
            this.log("OmegaZirkel Discord Plugin is enabled", 10);

            this.log("Will send chat to Discord: " + String.valueOf(postChat), 10);
            this.log("Will send status to Discord: " + String.valueOf(postStatus), 10);
            this.log("Will send support tickets to Discord: " + String.valueOf(postSupport), 10);
            this.log("Sending motd on login is: " + String.valueOf(sendMOTD), 10);
            this.log("motd is: " + String.valueOf(motd), 10);

        } catch (IOException ex) {
            this.log("IOException on initSettings: " + ex.getMessage(), 100);
//            e.printStackTrace();
        } catch (NumberFormatException ex) {
            this.log("NumberFormatException on initSettings: " + ex.getMessage(), 100);
        } catch (Exception ex) {
            this.log("Exception on initSettings: " + ex.getMessage(), 100);
        }
    }

    /**
     * log text to server output todo: use output level to filter messages
     *
     * @param text the message to print in server logs
     * @param level message level higher level means higher priority
     */
    private void log(String text, int level) {
        if (level >= logLevel) {
            System.out.println("[OZDP] " + text);
        }
    }

    /**
     *
     * @param filename
     */
    @Override
    public void onFileChangeEvent(String filename) {
        if (filename.equals("settings.properties")) {
            this.log("Settings file was changed, reloading settings now", 10);
            if (reportSettingsChanged) {
                Server server = getServer();
                String username = statusUsername;
                if (useServerName) {
                    username = server.getName();
                }
                this.sendDiscordMessage(username, "settings.properties has changed, reloading", webHookStatusUrl);
            }
            this.initSettings();
        } else if (filename.endsWith("jar")) {
            this.log(filename + " file was changed, set restart flag (or restart if no player online)", 10);
            Server server = getServer();
            if (reportJarChanged) {
                String username = statusUsername;
                if (useServerName) {
                    username = server.getName();
                }
                if (restartOnUpdate) {

                    if (server.getPlayerCount() > 0) {
                        this.sendDiscordMessage(username, filename + " has changed, restart flag set", webHookStatusUrl);
                        getServer().broadcastTextMessage("[#FF8000][OZDP] Restart flag was set due to file changes (Plugin update). Server will shutdown after last player has left the server!");
                    } else {
                        this.sendDiscordMessage(username, filename + " has changed, restarting server (no players online)", webHookStatusUrl);
                    }

                } else {
                    this.sendDiscordMessage(username, filename + " has changed, but restartOnUpdate is false", webHookStatusUrl);
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
            this.log("File changed: " + filename, 0);
        }
    }
}
