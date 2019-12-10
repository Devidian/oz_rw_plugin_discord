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

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Path;
// import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
// import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import net.risingworld.api.Plugin;
import net.risingworld.api.Server;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.npc.NpcDeathEvent;
import net.risingworld.api.events.npc.NpcDeathEvent.Cause;
import net.risingworld.api.events.player.PlayerChatEvent;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.events.player.PlayerConnectEvent;
import net.risingworld.api.events.player.PlayerDisconnectEvent;
// import net.risingworld.api.events.player.PlayerMountNpcEvent;
// import net.risingworld.api.events.player.PlayerPickupItemEvent;
import net.risingworld.api.events.player.PlayerSpawnEvent;
// import net.risingworld.api.events.player.world.PlayerDestroyConstructionEvent;
import net.risingworld.api.events.player.world.PlayerDestroyObjectEvent;
// import net.risingworld.api.events.player.world.PlayerRemoveConstructionEvent;
import net.risingworld.api.events.player.world.PlayerRemoveObjectEvent;
import net.risingworld.api.objects.Npc;
import net.risingworld.api.objects.Player;
// import net.risingworld.api.objects.WorldItem;
import net.risingworld.api.utils.Vector3f;
import net.risingworld.api.utils.Definitions.NpcDefinition;
import net.risingworld.api.utils.Definitions.ObjectDefinition;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

/**
 *
 * @author Maik "Devidian" Laschober
 */
public class DiscordWebHook extends Plugin implements Listener, FileChangeListener {

	static final String pluginVersion = "0.15.1";
	static final String pluginName = "DiscordPlugin";
	static final String pluginCMD = "dp";

	static final de.omegazirkel.risingworld.tools.Logger log = new de.omegazirkel.risingworld.tools.Logger("[OZ.DP]");
	static final Colors c = Colors.getInstance();
	private static I18n t = null;

	// Settings
	static int logLevel = 0;
	static boolean postChat = false;
	static String webHookChatUrl = "";
	static String joinDiscord = "";
	static boolean overrideAvatar = true;

	static boolean postSupport = false;
	static boolean supportScreenshot = true;

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

	static boolean postTrackedEvents = false;
	static String webHookEventUrl = "";
	static Short trackServerLogLevel = 0;
	static boolean trackMountKill = false;
	static boolean trackNonHostileAnimalKill = false;
	static boolean trackPickupables = false;

	static boolean botEnable = false;
	static boolean botSecure = true;
	static String botToken = "";
	static String botAdmins = "";
	static String botLang = "en";
	static String botChatChannelName = "server-chat";

	static boolean restartAdminOnly = true;
	static boolean allowRestart = false;
	static boolean restartOnUpdate = true;
	static int restartMinimumTime = 86400;// (60 * 60 * 24); // 1 Day default
	static boolean restartTimed = false; // restart schedule
	static int forceRestartAfter = 5; // Minutes

	static boolean allowScreenshots = true;
	static int maxScreenWidth = 1920;

	static boolean colorizeChat = true;
	static boolean showGroup = false;
	static String colorSupport = "[#782d8e]";
	static String colorLocalSelf = "[#ddffdd]";
	static String colorLocalAdmin = "[#db3208]";
	static String colorLocalOther = "[#dddddd]";
	static String colorLocalDiscord = "[#ddddff]";

	public static Map<String, Short> discordCommands = new HashMap<>();

	// END Settings
	// Live properties
	static boolean flagRestart = false;
	static Plugin GlobalIntercom = null;
	static JavaCordBot DiscordBot = null;

	// Timer
	static Timer restartTimer = new Timer();
	static TimerTask restartTask = null;
	static TimerTask restartForcedTask = null;

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

	public String getColorLocalAdmin() {
		return colorLocalAdmin;
	}

	public boolean getShowGroupSetting() {
		return showGroup;
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
			File f = new File(getPath());
			PluginChangeWatcher.registerFileChangeListener(this, f);
		} catch (Exception ex) {
			log.out(ex.toString(), 911);
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

		if (command.equals("/" + pluginCMD)) {
			// Invalid number of arguments (0)
			if (cmdParts.length < 2) {
				player.sendTextMessage(c.error + pluginName + ":>" + c.text
						+ t.get("MSG_CMD_ERR_ARGUMENTS", lang).replace("PH_CMD", c.error + command + c.text)
								.replace("PH_COMMAND_HELP", c.command + "/" + pluginCMD + " help\n" + c.text));
				return;
			}

			String option = cmdParts[1];

			switch (option) {
			case "restart":
				boolean canTriggerRestart = allowRestart && (player.isAdmin() || (!restartAdminOnly
						&& player.getTotalPlayTime() > restartMinimumTime && restartMinimumTime > 0));
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
						.replace("PH_CMD_HELP", c.command + "/" + pluginCMD + " help" + c.text);
				player.sendTextMessage(c.okay + pluginName + ":> " + c.text + infoMessage);
				break;
			case "help":
				String helpMessage = t.get("CMD_HELP", lang)
						.replace("PH_CMD_SUPPORT", c.command + "/support TEXT" + c.text)
						.replace("PH_CMD_HELP", c.command + "/" + pluginCMD + " help" + c.text)
						.replace("PH_CMD_RESTART", c.command + "/" + pluginCMD + " restart" + c.text)
						.replace("PH_CMD_INFO", c.command + "/" + pluginCMD + " info" + c.text)
						.replace("PH_CMD_STATUS", c.command + "/" + pluginCMD + " status" + c.text)
						.replace("PH_CMD_JOIN", c.command + "/joinDiscord" + c.text);
				player.sendTextMessage(c.okay + pluginName + ":> " + c.text + helpMessage);
				break;
			case "status":
				String statusMessage = t.get("CMD_STATUS", lang).replace("PH_VERSION", c.okay + pluginVersion + c.text)
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
				if (supportScreenshot || message.contains("+screen")) {

					int playerResolutionX = player.getScreenResolutionX();
					float sizeFactor = 1.0f;
					if (playerResolutionX > maxScreenWidth) {
						sizeFactor = (maxScreenWidth * 1f / playerResolutionX * 1f);
					}
					log.out("Taking screenshot with factor " + sizeFactor, 0);
					final String msgToSend = supportMessage;
					player.createScreenshot(sizeFactor, (BufferedImage bimg) -> {
						final ByteArrayOutputStream os = new ByteArrayOutputStream();
						try {
							ImageIO.write(bimg, "jpg", os);
							// Base64.getEncoder().encodeToString(os.toByteArray());
							this.sendDiscordMessage("SupportTicket", msgToSend, webHookSupportUrl, os.toByteArray());
							player.sendTextMessage(
									c.okay + pluginName + ":>" + c.text + t.get("SUPPORT_SUCCESS", lang));
						} catch (Exception e) {
							// throw new UncheckedIOException(ioe);
							log.out(e.toString());
						}
					});
				} else {
					this.sendDiscordMessage("SupportTicket", supportMessage, webHookSupportUrl);
					player.sendTextMessage(c.okay + pluginName + ":>" + c.text + t.get("SUPPORT_SUCCESS", lang));
				}
			} else {
				player.sendTextMessage(c.error + pluginName + ":>" + c.text + t.get("SUPPORT_NOTAVAILABLE", lang));
			}
		} else if (command.equals("/joinDiscord")) {
			if (joinDiscord.isEmpty()) {
				player.sendTextMessage(c.error + pluginName + ":>" + c.text + t.get("CMD_JOINDISCORD_NA", lang));
			} else {
				player.connectToDiscord("https://discord.gg/" + joinDiscord);
			}
		} else if (command.equals("/ozrestart")) {
			player.sendTextMessage(c.error + pluginName + ":>" + c.text + t.get("CMD_ERR_DEPRECATED", lang)
					.replace("PH_NEWCMD", c.command + "/" + pluginCMD + " restart" + c.text));
		}

	}

	/**
	 *
	 * @param event
	 */
	@EventMethod
	public void onPlayerChat(PlayerChatEvent event) {

		String message = event.getChatMessage();
		String noColorText = message.replaceAll("(\\[#[0-9a-fA-F]+\\])", "");
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
			if (allowScreenshots == true && noColorText.contains("+screen")) {
				int playerResolutionX = player.getScreenResolutionX();
				float sizeFactor = 1.0f;
				if (playerResolutionX > maxScreenWidth) {
					sizeFactor = (maxScreenWidth * 1f / playerResolutionX * 1f);
				}
				final String textToSend = noColorText.replace("+screen", "[screenshot.jpg]");
				log.out("Taking screenshot with factor " + sizeFactor, 0);
				player.createScreenshot(sizeFactor, (BufferedImage bimg) -> {
					final ByteArrayOutputStream os = new ByteArrayOutputStream();
					try {
						ImageIO.write(bimg, "jpg", os);
						// Base64.getEncoder().encodeToString(os.toByteArray());
						this.sendDiscordMessage(player.getName(), textToSend, webHookChatUrl, os.toByteArray());
					} catch (Exception e) {
						// throw new UncheckedIOException(ioe);
						log.out(e.toString());
					}
				});
			} else {
				if (noColorText.contains("+screen")) {
					log.out("Screenshot taking not enabled", 0);
				}
				this.sendDiscordMessage(player.getName(), noColorText, webHookChatUrl);
			}
			if (colorizeChat) {
				broadcastChatMessage(player, noColorText);
				event.setCancelled(true);
			}
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
			} else if (eventPlayer.isAdmin()) {
				color = colorLocalAdmin;
			}

			String group = "";

			if (showGroup) {
				group = " (" + eventPlayer.getPermissionGroup() + ")";
			}

			player.sendTextMessage(color + "[LOCAL] " + eventPlayer.getName() + group + ": " + c.text + noColorText);

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
					server.saveAll();
					server.shutdown();
				} else if (playersLeft > 1) {
					this.broadcastMessage("BC_PLAYER_REMAIN", playersLeft);
				}
			}
		}
	}

	/**
	 *
	 * @param event
	 */
	@EventMethod
	public void onPlayerRemoveObject(PlayerRemoveObjectEvent event) {
		ObjectDefinition def = event.getObjectDefinition();
		Vector3f pos = event.getObjectPosition();
		String posMap = ((int) pos.x) + (pos.x > 0 ? "W" : "E") + " " + ((int) pos.z) + (pos.z > 0 ? "N" : "S");
		if (!def.isPickupable() || !trackPickupables)
			return;
		String msg = t.get("BAT_OBJECT_REMOVE", botLang).replace("PH_PLAYER", event.getPlayer().getName())
				.replace("PH_OBJECT_NAME", def.getName()).replace("PH_LOCATION", pos.x + " " + pos.y + " " + pos.z)
				.replace("PH_MAP_COORDINATES", posMap);
		log.out(msg, trackServerLogLevel);
		this.sendDiscordMessage(statusUsername, msg, webHookEventUrl);
	}

	/**
	 *
	 * @param event
	 */
	@EventMethod
	public void onPlayerDestroyObject(PlayerDestroyObjectEvent event) {
		ObjectDefinition def = event.getObjectDefinition();
		Vector3f pos = event.getObjectPosition();
		String posMap = ((int) pos.x) + (pos.x > 0 ? "W" : "E") + " " + ((int) pos.z) + (pos.z > 0 ? "N" : "S");
		if (!def.isPickupable() || !trackPickupables)
			return;
		String msg = t.get("BAT_OBJECT_DESTROY", botLang).replace("PH_PLAYER", event.getPlayer().getName())
				.replace("PH_OBJECT_NAME", def.getName()).replace("PH_LOCATION", pos.x + " " + pos.y + " " + pos.z)
				.replace("PH_MAP_COORDINATES", posMap);
		log.out(msg, trackServerLogLevel);
		this.sendDiscordMessage(statusUsername, msg, webHookEventUrl);
	}

	/**
	 * track mount and non aggressive animal deaths
	 *
	 * @param event
	 */
	@EventMethod
	public void onNpcDeath(NpcDeathEvent event) {
		// log.out("NPC DEATH EVENT: " + event.getCause().toString() + " / " +
		// Cause.KilledByPlayer);
		Npc npc = event.getNpc();
		NpcDefinition def = npc.getDefinition();
		Vector3f pos = event.getDeathPosition();
		String posString = pos.x + " " + pos.y + " " + pos.z;
		String posMap = ((int) pos.x) + (pos.x > 0 ? "W" : "E") + " " + ((int) pos.z) + (pos.z > 0 ? "N" : "S");
		if (event.getCause() != Cause.KilledByPlayer) {
			return;
		}
		Player player = (Player) event.getKiller();

		// log.out("NPC TYPE: " + npc.getType() + " / " + Npc.Type.Mount + " | " +
		// Npc.Type.Animal + "("
		// + npc.getDefinition().getBehaviour() + ")");
		// log.out(npc.getType() + "==" + Npc.Type.Mount + "&&" + trackMountKill);
		// log.out("|" + npc.getType() + "==" + Npc.Type.Animal + "&&" +
		// trackNonHostileAnimalKill + "&&"
		// + !npc.getDefinition().getBehaviour().equalsIgnoreCase("AGGRESSIVE"), 0);
		if (npc.getType() == Npc.Type.Mount && trackMountKill) {
			// a mount was killed
			String msg = t.get("BAT_KILL_MOUNT", botLang).replace("PH_PLAYER", player.getName())
					.replace("PH_NPC_NAME", def.getName()).replace("PH_LOCATION", posString)
					.replace("PH_MAP_COORDINATES", posMap);
			log.out(msg, trackServerLogLevel);
			this.sendDiscordMessage(statusUsername, msg, webHookEventUrl);
		} else if (npc.getType() == Npc.Type.Animal && trackNonHostileAnimalKill
				&& !npc.getDefinition().getBehaviour().equalsIgnoreCase("AGGRESSIVE")) {
			// Non agressive animal was killed
			String msg = t.get("BAT_KILL_ANIMAL", botLang).replace("PH_PLAYER", player.getName())
					.replace("PH_NPC_NAME", def.getName()).replace("PH_LOCATION", posString)
					.replace("PH_MAP_COORDINATES", posMap);
			log.out(msg, trackServerLogLevel);

			this.sendDiscordMessage(statusUsername, msg, webHookEventUrl);
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
	 * @param channel
	 * @param image
	 */
	private void sendDiscordMessage(String username, String text, String channel, byte[] image) {
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
			if (image != null) {
				try {
					// json.remove("content");
					// json.put("file", image);
					HttpPost imagePost = new HttpPost(channel);
					MultipartEntityBuilder builder = MultipartEntityBuilder.create();
					builder.addTextBody("username", username, ContentType.TEXT_PLAIN);
					if (overrideAvatar) {
						builder.addTextBody("avatar_url",
								"https://api.adorable.io/avatars/128/" + username.replace(" ", "%20"),
								ContentType.TEXT_PLAIN);
					}
					builder.addBinaryBody("file", image, ContentType.APPLICATION_OCTET_STREAM, "screenshot.jpg");
					HttpEntity multipart = builder.build();
					//
					imagePost.setEntity(multipart);
					HttpResponse imageResponse = httpClient.execute(imagePost);
					int imageStatus = imageResponse.getStatusLine().getStatusCode();
					if (imageStatus != 204) {
						HttpEntity entity = imageResponse.getEntity();
						String responseString = EntityUtils.toString(entity, "UTF-8");
						log.out("HTTP Status: " + status + "\nResponse: " + responseString + "\nRequest was: "
								+ stringObject, 0);
					}
				} catch (Exception e) {
					log.out("Exception on sendDiscordMessage: " + e.getMessage(), 100);
				}
			}
		} catch (IOException ex) {
			log.out("IOException on sendDiscordMessage: " + ex.getMessage(), 100);
		} catch (UnsupportedCharsetException | ParseException ex) {
			log.out("Exception on sendDiscordMessage: " + ex.getMessage(), 100);
		}
	}

	/**
	 *
	 * @param username
	 * @param text
	 * @param channel
	 */
	private void sendDiscordMessage(String username, String text, String channel) {
		this.sendDiscordMessage(username, text, channel, null);
	}

	/**
	 * public API for status channel
	 *
	 * @param username
	 * @param text
	 */
	public void sendDiscordStatusMessage(String username, String text) {
		sendDiscordMessage(username, text, webHookStatusUrl);
	}

	/**
	 * public API for event channel
	 *
	 * @param username
	 * @param text
	 */
	public void sendDiscordEventMessage(String username, String text) {
		sendDiscordMessage(username, text, webHookEventUrl);
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
			logLevel = Integer.parseInt(settings.getProperty("logLevel", "0"));
			// log.out(settings.getProperty("webHookUrl"),0);
			postChat = settings.getProperty("postChat", "false").contentEquals("true");
			webHookChatUrl = settings.getProperty("webHookChatUrl", "");
			joinDiscord = settings.getProperty("joinDiscord", "");
			overrideAvatar = settings.getProperty("overrideAvatar", "true").contentEquals("true");

			postStatus = settings.getProperty("postStatus", "false").contentEquals("true");
			reportStatusEnabled = settings.getProperty("reportStatusEnabled", "true").contentEquals("true");
			reportStatusDisabled = settings.getProperty("reportStatusDisabled", "true").contentEquals("true");
			reportSettingsChanged = settings.getProperty("reportSettingsChanged", "true").contentEquals("true");
			reportJarChanged = settings.getProperty("reportJarChanged", "true").contentEquals("true");
			webHookStatusUrl = settings.getProperty("webHookStatusUrl", "");
			statusUsername = settings.getProperty("statusUsername", "");
			statusEnabledMessage = settings.getProperty("statusEnabledMessage", "");
			statusDisabledMessage = settings.getProperty("statusDisabledMessage", "");
			useServerName = settings.getProperty("useServerName", "false").contentEquals("true");

			postSupport = settings.getProperty("postSupport", "false").contentEquals("true");
			supportScreenshot = settings.getProperty("supportScreenshot", "true").contentEquals("true");
			addTeleportCommand = settings.getProperty("addTeleportCommand", "true").contentEquals("true");
			webHookSupportUrl = settings.getProperty("webHookSupportUrl", "");

			botChatChannelName = settings.getProperty("botChatChannelName", "server-chat");
			botEnable = settings.getProperty("botEnable", "false").contentEquals("true");
			botSecure = settings.getProperty("botSecure", "true").contentEquals("true");
			botToken = settings.getProperty("botToken", "");
			botLang = settings.getProperty("botLang", "en");
			botAdmins = settings.getProperty("botAdmins", "");

			// discord bot commands
			discordCommands.put("help", Short.parseShort(settings.getProperty("botCMDhelp", "1")));
			discordCommands.put("version", Short.parseShort(settings.getProperty("botCMDversion", "1")));
			discordCommands.put("online", Short.parseShort(settings.getProperty("botCMDonline", "1")));
			discordCommands.put("weather", Short.parseShort(settings.getProperty("botCMDweather", "1")));
			discordCommands.put("time", Short.parseShort(settings.getProperty("botCMDtime", "1")));
			discordCommands.put("banned", Short.parseShort(settings.getProperty("botCMDbanned", "1")));

			discordCommands.put("restart", Short.parseShort(settings.getProperty("botCMDrestart", "2")));
			discordCommands.put("support", Short.parseShort(settings.getProperty("botCMDsupport", "2")));
			discordCommands.put("kick", Short.parseShort(settings.getProperty("botCMDkick", "2")));
			discordCommands.put("ban", Short.parseShort(settings.getProperty("botCMDban", "2")));
			discordCommands.put("group", Short.parseShort(settings.getProperty("botCMDgroup", "2")));
			discordCommands.put("yell", Short.parseShort(settings.getProperty("botCMDyell", "2")));
			discordCommands.put("bc", Short.parseShort(settings.getProperty("botCMDbc", "2")));
			discordCommands.put("unban", Short.parseShort(settings.getProperty("botCMDunban", "2")));
			discordCommands.put("tptp", Short.parseShort(settings.getProperty("botCMDtptp", "2")));
			discordCommands.put("mkadmin", Short.parseShort(settings.getProperty("botCMDmkadmin", "2")));
			discordCommands.put("unadmin", Short.parseShort(settings.getProperty("botCMDunadmin", "2")));
			discordCommands.put("setweather", Short.parseShort(settings.getProperty("botCMDsetweather", "2")));
			discordCommands.put("settime", Short.parseShort(settings.getProperty("botCMDsettime", "2")));
			discordCommands.put("sethealth", Short.parseShort(settings.getProperty("botCMDsethealth", "2")));
			discordCommands.put("sethunger", Short.parseShort(settings.getProperty("botCMDsethunger", "2")));
			discordCommands.put("setthirst", Short.parseShort(settings.getProperty("botCMDsetthirst", "2")));
			// badass stuff
			postTrackedEvents = settings.getProperty("postTrackedEvents", "false").contentEquals("true");
			webHookEventUrl = settings.getProperty("webHookEventUrl", "");
			trackServerLogLevel = Short.parseShort(settings.getProperty("trackServerLogLevel", "100"));
			trackMountKill = settings.getProperty("trackMountKill", "false").contentEquals("true");
			trackNonHostileAnimalKill = settings.getProperty("trackNonHostileAnimalKill", "false")
					.contentEquals("true");
			trackPickupables = settings.getProperty("trackPickupables", "false").contentEquals("true");

			// colors

			colorizeChat = settings.getProperty("colorizeChat", "true").contentEquals("true");
			showGroup = settings.getProperty("showGroup", "false").contentEquals("true");
			colorSupport = settings.getProperty("colorSupport", "[#782d8e]");
			colorLocalSelf = settings.getProperty("colorLocalSelf", "[#ddffdd]");
			colorLocalAdmin = settings.getProperty("colorLocalAdmin", "[#db3208]");
			colorLocalOther = settings.getProperty("colorLocalOther", "[#dddddd]");
			colorLocalDiscord = settings.getProperty("colorLocalDiscord", "[#ddddff]");

			// screenshots
			allowScreenshots = settings.getProperty("allowScreenshots", "true").contentEquals("true");
			maxScreenWidth = Integer.parseInt(settings.getProperty("maxScreenWidth", "1920"));

			// motd settings
			sendPluginWelcome = settings.getProperty("sendPluginWelcome", "false").contentEquals("true");

			// restart settings
			restartTimed = settings.getProperty("restartTimed", "false").contentEquals("true");
			allowRestart = settings.getProperty("allowRestart", "false").contentEquals("true");
			restartAdminOnly = settings.getProperty("restartAdminOnly", "false").contentEquals("true");
			restartOnUpdate = settings.getProperty("restartOnUpdate", "false").contentEquals("true");
			restartMinimumTime = Integer.parseInt(settings.getProperty("restartMinimumTime", "86400"));
			forceRestartAfter = Integer.parseInt(settings.getProperty("forceRestartAfter", "0"));

			// parse next restart time (we only need the next beacause we have to lookup
			// again after restart)
			String restartTimesString = settings.getProperty("restartTimes", "00:00");

			// log.out("restartTimed: " + restartTimed + " restartTimesString: " +
			// restartTimesString, 10);
			if (restartTimed) {
				String[] restartTimes = restartTimesString.split("\\|");
				initRestartSchedule(restartTimes);
			}

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
	 * @param times
	 */
	private void initRestartSchedule(String[] times) {
		try {
			// log.out("initRestartSchedule: " + times.length + " items", 10);
			Calendar cal = Calendar.getInstance();
			int minHour = 24;
			int minMinute = 60;
			int nextHour = -1;
			int nextMinute = -1;
			for (String time : times) {
				String[] timeParts = time.split(":");
				int hour = Integer.parseInt(timeParts[0]);
				int minute = Integer.parseInt(timeParts[1]);
				// log.out("checking: " + hour + ":" + minute, 10);
				// get min time if we have to jump to the next day
				if (hour <= minHour) {
					minHour = hour;
					if (minute <= minMinute) {
						minMinute = minute;
					}
				}
				// look for the next time to restart (nearest)
				// Same hour but greater minutes
				if (hour == cal.get(HOUR_OF_DAY) && minute > cal.get(MINUTE)
						&& (nextMinute < 0 || nextMinute > minute)) {
					log.out("new time found: " + hour + ":" + minute, 10);
					nextHour = hour;
					nextMinute = minute;
					// if hour is greater than current AND nextHour is not set or greater than hour
					// AND nextMinute is not set or hour is equal nextHour and nextMinute is greater
				} else if (hour > cal.get(HOUR_OF_DAY) && (nextHour < 0 || nextHour >= hour)
						&& (nextMinute < 0 || (hour == nextHour && nextMinute > minute))) {
					log.out("new time found: " + hour + ":" + minute, 10);
					nextHour = hour;
					nextMinute = minute;
				}

			}

			if (nextHour < 0) {
				// in this case there was no time found on the current day bigger than now
				// so the next restart may be tomorrow
				// log.out("using next day time: " + minHour + ":" + minMinute, 10);
				nextHour = minHour;
				nextMinute = minMinute;
			}

			if (nextHour < cal.get(HOUR_OF_DAY) || (nextHour == cal.get(HOUR_OF_DAY) && nextMinute < cal.get(MINUTE))) {
				cal.set(DAY_OF_MONTH, cal.get(DAY_OF_MONTH) + 1);
			}
			cal.set(HOUR_OF_DAY, nextHour);
			cal.set(MINUTE, nextMinute);

			log.out("Next Server restart time is scheduled on " + nextHour + ":" + nextMinute, 10);

			if (restartTask != null) {
				restartTask.cancel();
			}

			restartTask = new TimerTask() {
				@Override
				public void run() {
					Server server = getServer();
					int playerNum = server.getAllPlayers().size();
					if (playerNum > 0) {
						log.out("Setting restart flag for scheduled server-restart", 10);
						broadcastMessage("RS_SCHEDULE_INFO");
						flagRestart = true;
						if (forceRestartAfter > 0) {
							broadcastMessage("RS_SCHEDULE_WARN", forceRestartAfter);
						}
					} else {
						log.out("Restarting server now (scheduled)", 10);
						server.saveAll();
						server.shutdown();
					}
				}
			};

			restartTimer.schedule(restartTask, cal.getTime());

			// force restarting
			if (forceRestartAfter > 0) {
				if (restartForcedTask != null) {
					restartForcedTask.cancel();
				}

				restartForcedTask = new TimerTask() {
					@Override
					public void run() {
						log.out("Force server restart now!", 10);
						Server server = getServer();
						server.getAllPlayers().forEach(p -> {
							p.kick("Server restart");
						});
						server.saveAll();
						server.shutdown();
					}
				};

				cal.set(MINUTE, nextMinute + forceRestartAfter);
				restartTimer.schedule(restartForcedTask, cal.getTime());
			}

			// clear canceled tasks;
			restartTimer.purge();

		} catch (Exception e) {
			log.out(e.getLocalizedMessage(), 999);
		}
	}

	/**
	 *
	 * @param i18nIndex
	 * @param playerName
	 */
	private void broadcastMessage(String i18nIndex, String playerName) {
		getServer().getAllPlayers().forEach((player) -> {
			try {
				String lang = player.getSystemLanguage();
				player.sendTextMessage(c.warning + pluginName + ":> " + c.text
						+ t.get(i18nIndex, lang).replace("PH_PLAYER", playerName));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 *
	 * @param i18nIndex
	 * @param number
	 */
	private void broadcastMessage(String i18nIndex, int number) {
		getServer().getAllPlayers().forEach((player) -> {
			try {
				String lang = player.getSystemLanguage();
				player.sendTextMessage(c.warning + pluginName + ":> " + c.text
						+ t.get(i18nIndex, lang).replace("PH_NUMBER", number + ""));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 *
	 * @param i18nIndex
	 */
	private void broadcastMessage(String i18nIndex) {
		this.broadcastMessage(i18nIndex, "");
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
					server.saveAll();
					server.shutdown();
				}
			}
		} else {
			log.out("onFileCreateEvent: <" + file + ">", 0);
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
