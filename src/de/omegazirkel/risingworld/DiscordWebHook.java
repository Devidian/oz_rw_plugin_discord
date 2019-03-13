/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.omegazirkel.risingworld;

import de.omegazirkel.risingworld.tools.Colors;
import de.omegazirkel.risingworld.tools.FileChangeListener;
import de.omegazirkel.risingworld.tools.I18n;
import de.omegazirkel.risingworld.tools.PluginChangeWatcher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Path;
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

    static final String pluginVersion = "0.13.0-SNAPSHOT";
    static final String pluginName = "DiscordPlugin";

    static final de.omegazirkel.risingworld.tools.Logger log = new de.omegazirkel.risingworld.tools.Logger("[OZ.DP]");
	static final Colors c = Colors.getInstance();
    private static I18n t = null;

    static final String colorSupport = "[#782d8e]";
    static final String colorLocalSelf = "[#ddffdd]";
    static final String colorLocalOther = "[#dddddd]";
    static final String colorLocalDiscord = "[#ddddff]";

    // Settings
    static int logLevel = 0;
    static boolean postChat = false;
    static String webHookChatUrl = "";
    static String joinDiscord = "";
    static boolean overrideAvatar = true;

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

    static boolean sendPluginWelcome = false;

    static boolean botEnable = false;
    static boolean botSecure = true;
    static String botToken = "";
    static String botAdmins = "";
    static String botLang = "en";
    static String botChatChannelName = "server-chat";

    static boolean allowRestart = false;
    static boolean restartOnUpdate = true;
    static int restartMinimumTime = 86400;// (60 * 60 * 24); // 1 Day default

    // END Settings
    // Live properties
    static boolean flagRestart = false;
    static Plugin GlobalIntercom = null;
    static JavaCordBot DiscordBot = null;

    // getter
    public String getBotToken() {
        return botToken;
    }

    public String getBotAdmins() {
        return botAdmins;
    }

    public String getBotLanguage() {
        return botLang;
    }

    public boolean getBotSecure() {
        return botSecure;
    }

    public boolean getOverrideAvatar() {
        return overrideAvatar;
    }

    public String getColorSupport() {
        return colorSupport;
    }

    public String getColorText() {
        return c.text;
    }

    public void setFlagRestart(boolean value) {
        flagRestart = value;
    }

    public String getBotChatChannelName() {
        return botChatChannelName;
    }

    public String getColorLocalDiscord() {
        return colorLocalDiscord;
    }

    @Override
    public void onEnable() {
        // Register event listener
        t = new I18n(this);
        registerEventListener(this);
        GlobalIntercom = getPluginByName("Omega Zirkel Global Intercom Plugin");
        if (GlobalIntercom != null) {
            log.out("Global Intercom found! ID: " + GlobalIntercom.getID(), 0);
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
            log.out(ex.getMessage(), 999);
        }

        if (!botEnable) {
            log.out("DiscordBot is disabled", 0);
            return;
        }
        DiscordBot = new JavaCordBot(this);
        DiscordBot.run();
		log.out(pluginName + " Plugin is enabled", 10);
    }

    @EventMethod
    public void onPlayerCommand(PlayerCommandEvent event) {
        Player player = event.getPlayer();
        String lang = player.getSystemLanguage();
        String commandLine = event.getCommand();
        Vector3f pos = player.getPosition();

        String[] cmdParts = commandLine.split(" ", 2);
        String command = cmdParts[0];

        if (command.equals("/dp")) {
            String option = cmdParts[1];

            switch (option) {
            case "restart":
                boolean canTriggerRestart = player.isAdmin()
                        || (allowRestart && player.getTotalPlayTime() > restartMinimumTime && restartMinimumTime > 0);
                if (canTriggerRestart) {
                    String username = statusUsername;
                    if (useServerName) {
                        Server server = getServer();
                        username = server.getName();
                    }
                    String msgDC = t.get("DC_SHUTDOWN", botLang).replace("PH_PLAYER", player.getName());
                    this.sendDiscordMessage(username, msgDC, webHookStatusUrl);
                    this.broadcastMessage("BC_SHUTDOWN", player.getName());
                    flagRestart = true;
                } else {
                    player.sendTextMessage(
                            c.error + pluginName + ":>" + c.text + t.get("CMD_RESTART_NOTALLOWED", lang));
                }
                break;
            case "info":
                String infoMessage = t.get("CMD_INFO", lang)
                        .replace("PH_CMD_SUPPORT", c.command + "/support TEXT" + c.text)
                        .replace("PH_CMD_HELP", c.command + "/dp help" + c.text);
                player.sendTextMessage(c.okay + pluginName + ":> " + c.text + infoMessage);
                break;
            case "help":
                String helpMessage = t.get("CMD_HELP", lang)
                        .replace("PH_CMD_SUPPORT", c.command + "/support TEXT" + c.text)
                        .replace("PH_CMD_HELP", c.command + "/dp help" + c.text)
                        .replace("PH_CMD_RESTART", c.command + "/dp restart" + c.text)
                        .replace("PH_CMD_INFO", c.command + "/dp info" + c.text)
                        .replace("PH_CMD_STATUS", c.command + "/dp status" + c.text)
                        .replace("PH_CMD_JOIN", c.command + "/joinDiscord" + c.text);
                player.sendTextMessage(c.okay + pluginName + ":> " + c.text + helpMessage);
                break;
            case "status":
                String statusMessage = t.get("CMD_STATUS", lang)
                        .replace("PH_VERSION", c.okay + pluginVersion + c.text)
                        .replace("PH_LANGUAGE",
                                colorLocalSelf + player.getLanguage() + " / " + player.getSystemLanguage() + c.text)
                        .replace("PH_USEDLANG", colorLocalOther + t.getLanguageUsed(lang) + c.text)
                        .replace("PH_LANG_AVAILABLE", c.okay + t.getLanguageAvailable() + c.text);
                player.sendTextMessage(c.okay + pluginName + ":> " + c.text + statusMessage);
                break;
            default:
                break;
            }

        } else if (command.equals("/support")) {
            if (cmdParts.length < 2) {
                return;
            }
            String message = cmdParts[1];

            if (postSupport) {
                String supportMessage = "```" + player.getName() + ": " + message;
                if (addTeleportCommand) {
                    supportMessage += "\nTeleport command:> goto " + pos.x + " " + pos.y + " " + pos.z;
                }
                supportMessage += "```";
                this.sendDiscordMessage("SupportTicket", supportMessage, webHookSupportUrl);
                player.sendTextMessage(c.okay + pluginName + ":>" + c.text + t.get("SUPPORT_SUCCESS", lang));
            } else {
                player.sendTextMessage(
                        c.error + pluginName + ":>" + c.text + t.get("SUPPORT_NOTAVAILABLE", lang));
            }
        } else if (command.equals("/joinDiscord")) {
            if (joinDiscord.isEmpty()) {
                player.sendTextMessage(c.error + pluginName + ":>" + c.text + t.get("CMD_JOINDISCORD_NA", lang));
            } else {
                player.connectToDiscord("https://discord.gg/" + joinDiscord);
            }
        } else if (command.equals("/ozrestart")) {
            player.sendTextMessage(c.error + pluginName + ":>" + c.text
                    + t.get("CMD_ERR_DEPRECATED", lang).replace("PH_NEWCMD", c.command + "/dp restart" + c.text));
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
            if (noColorText.startsWith("#%")) {
                noColorText = noColorText.substring(2);
            }
        }

        if (processMessage && noColorText.trim().length() > 0) {
            Player player = event.getPlayer();
            this.sendDiscordMessage(player.getName(), noColorText, webHookChatUrl);
            broadcastChatMessage(player, noColorText);
            event.setCancelled(true);
        }
    }

    /**
     *
     * @param eventPlayer
     * @param noColorText
     */
    private void broadcastChatMessage(Player eventPlayer, String noColorText) {
        getServer().getAllPlayers().forEach((player) -> {
            String color = colorLocalOther;
            if (player.getUID() == eventPlayer.getUID()) {
                color = colorLocalSelf;
            }

            player.sendTextMessage(color + "[LOCAL] " + eventPlayer.getName() + ": " + c.text + noColorText);
        });
    }

    /**
     *
     * @param event
     */
    @EventMethod
    public void onPlayerSpawn(PlayerSpawnEvent event) {
        if (sendPluginWelcome) {
            Player player = event.getPlayer();
			String lang = player.getSystemLanguage();
			player.sendTextMessage(t.get("MSG_PLUGIN_WELCOME", lang));
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
            this.sendDiscordMessage(username,
                    t.get("DC_PLAYER_CONNECTED", botLang).replace("PH_PLAYER", player.getName()), webHookStatusUrl);
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
            this.sendDiscordMessage(username,
                    t.get("DC_PLAYER_DISCONNECTED", botLang).replace("PH_PLAYER", player.getName()), webHookStatusUrl);
            if (flagRestart) {
                int playersLeft = server.getPlayerCount() - 1;
                if (playersLeft == 0) {
                    this.sendDiscordMessage(username, t.get("RESTART_PLAYER_LAST", botLang), webHookStatusUrl);
                    server.shutdown();
                } else if (playersLeft > 1) {
                    this.broadcastMessage("BC_PLAYER_REMAIN", playersLeft);
                }
            }
        }
    }

    /**
     *
     */
    @Override
    public void onDisable() {
        log.out("OmegaZirkel Discord Plugin deactivated", 10);
        if (reportStatusDisabled) {
            Server server = getServer();
            String username = statusUsername;
            if (useServerName) {
                username = server.getName();
            }
            this.sendDiscordMessage(username, statusDisabledMessage, webHookStatusUrl);
        }
        if (botEnable) {
            DiscordBot.disconnect();
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
            if (overrideAvatar) {
                String avatar_url = "https://api.adorable.io/avatars/128/" + username.replace(" ", "%20");
                json.put("avatar_url", avatar_url);
            }

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
                log.out("HTTP Status: " + status + "\nResponse: " + responseString + "\nRequest was: " + stringObject,
                        0);
            }
        } catch (IOException ex) {
            log.out("IOException on sendDiscordMessage: " + ex.getMessage(), 100);
        } catch (UnsupportedCharsetException | ParseException ex) {
            log.out("Exception on sendDiscordMessage: " + ex.getMessage(), 100);
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
            settings.load(new InputStreamReader(in, "UTF8"));
            in.close();
            // fill global values
            logLevel = Integer.parseInt(settings.getProperty("logLevel"));
            // log.out(settings.getProperty("webHookUrl"),0);
            postChat = settings.getProperty("postChat", "false").contentEquals("true");
            webHookChatUrl = settings.getProperty("webHookChatUrl");
            joinDiscord = settings.getProperty("joinDiscord");
            overrideAvatar = settings.getProperty("overrideAvatar", "true").contentEquals("true");

            postStatus = settings.getProperty("postStatus", "false").contentEquals("true");
            reportStatusEnabled = settings.getProperty("reportStatusEnabled", "true").contentEquals("true");
            reportStatusDisabled = settings.getProperty("reportStatusDisabled", "true").contentEquals("true");
            reportSettingsChanged = settings.getProperty("reportSettingsChanged", "true").contentEquals("true");
            reportJarChanged = settings.getProperty("reportJarChanged", "true").contentEquals("true");
            webHookStatusUrl = settings.getProperty("webHookStatusUrl");
            statusUsername = settings.getProperty("statusUsername");
            statusEnabledMessage = settings.getProperty("statusEnabledMessage");
            statusDisabledMessage = settings.getProperty("statusDisabledMessage");
            useServerName = settings.getProperty("useServerName", "false").contentEquals("true");

            postSupport = settings.getProperty("postSupport", "false").contentEquals("true");
            addTeleportCommand = settings.getProperty("addTeleportCommand", "true").contentEquals("true");
            webHookSupportUrl = settings.getProperty("webHookSupportUrl");

            botChatChannelName = settings.getProperty("botChatChannelName", "server-chat");
            botEnable = settings.getProperty("botEnable", "false").contentEquals("true");
            botSecure = settings.getProperty("botSecure", "true").contentEquals("true");
            botToken = settings.getProperty("botToken", "");
            botLang = settings.getProperty("botLang", "en");
            botAdmins = settings.getProperty("botAdmins", "");

            // motd settings
            sendPluginWelcome = settings.getProperty("sendPluginWelcome", "true").contentEquals("true");

            // restart settings
            allowRestart = settings.getProperty("allowRestart").contentEquals("true");
            restartOnUpdate = settings.getProperty("restartOnUpdate").contentEquals("true");
            restartMinimumTime = Integer.parseInt(settings.getProperty("restartMinimumTime"));
            log.out(pluginName + " Plugin settings loaded", 10);

            log.out("Will send chat to Discord: " + String.valueOf(postChat), 10);
            log.out("Will send status to Discord: " + String.valueOf(postStatus), 10);
            log.out("Will send support tickets to Discord: " + String.valueOf(postSupport), 10);
            log.out("Sending welcome message on login is: " + String.valueOf(sendPluginWelcome), 10);

        } catch (IOException ex) {
            log.out("IOException on initSettings: " + ex.getMessage(), 100);
            // e.printStackTrace();
        } catch (NumberFormatException ex) {
            log.out("NumberFormatException on initSettings: " + ex.getMessage(), 100);
        } catch (Exception ex) {
            log.out("Exception on initSettings: " + ex.getMessage(), 100);
        }
    }

    /**
     *
     * @param i18nIndex
     * @param playerName
     */
    private void broadcastMessage(String i18nIndex, String playerName) {
        getServer().getAllPlayers().forEach((player) -> {
            String lang = player.getSystemLanguage();
            player.sendTextMessage(c.warning + pluginName + ":> " + c.text
                    + t.get(i18nIndex, lang).replace("PH_PLAYER", playerName));
        });
    }

    /**
     *
     * @param i18nIndex
     * @param playerCount
     */
    private void broadcastMessage(String i18nIndex, int playerCount) {
        getServer().getAllPlayers().forEach((player) -> {
            String lang = player.getSystemLanguage();
            player.sendTextMessage(c.warning + pluginName + ":> " + c.text
                    + t.get(i18nIndex, lang).replace("PH_PLAYERS", playerCount + ""));
        });
    }

    /**
     *
     * @param i18nIndex
     */
    private void broadcastMessage(String i18nIndex) {
        this.broadcastMessage(i18nIndex, null);
    }

    /**
     *
     * @param filename
     */
    @Override
    public void onFileCreateEvent(Path file) {
        if (file.toString().endsWith("jar")) {
            log.out(file + " file was changed, set restart flag (or restart if no player online)", 10);
            Server server = getServer();
            if (reportJarChanged) {
                String username = statusUsername;
                if (useServerName) {
                    username = server.getName();
                }
                if (restartOnUpdate) {

                    if (server.getPlayerCount() > 0) {
                        this.sendDiscordMessage(username,
                                t.get("UPDATE_FLAG", botLang).replace("PH_FILE", file.getFileName() + ""),
                                webHookStatusUrl);
                        this.broadcastMessage("BC_UPDATE_FLAG");
                    } else {
                        this.sendDiscordMessage(username,
                                t.get("UPDATE_RESTART", botLang).replace("PH_FILE", file.getFileName() + ""),
                                webHookStatusUrl);
                    }

                } else {
                    this.sendDiscordMessage(username,
                            t.get("UPDATE_INFO", botLang).replace("PH_FILE", file.getFileName() + ""),
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
            log.out("File changed: <" + file + ">", 0);
        }
    }

    @Override
    public void onFileChangeEvent(Path file) {
        if (file.toString().endsWith("settings.properties")) {
            log.out("Settings file was changed, reloading settings now", 10);
            if (reportSettingsChanged) {
                Server server = getServer();
                String username = statusUsername;
                if (useServerName) {
                    username = server.getName();
                }
                this.sendDiscordMessage(username, t.get("UPDATE_SETTINGS", botLang), webHookStatusUrl);
            }
            this.initSettings();
        } else {
            log.out(file.toString() + " was changed", 0);
        }
    }
}
