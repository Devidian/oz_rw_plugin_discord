package de.omegazirkel.risingworld;

import java.util.List;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.listener.GloballyAttachableListener;

import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import de.omegazirkel.risingworld.commands.BanCommand;
import de.omegazirkel.risingworld.commands.BroadcastCommand;
import de.omegazirkel.risingworld.commands.GetBannedCommand;
import de.omegazirkel.risingworld.commands.GetTimeCommand;
import de.omegazirkel.risingworld.commands.GetVersionCommand;
import de.omegazirkel.risingworld.commands.GetWeatherCommand;
import de.omegazirkel.risingworld.commands.GroupCommand;
import de.omegazirkel.risingworld.commands.HelpCommand;
import de.omegazirkel.risingworld.commands.KickCommand;
import de.omegazirkel.risingworld.commands.MakeAdminCommand;
import de.omegazirkel.risingworld.commands.OnlineCommand;
import de.omegazirkel.risingworld.commands.RestartCommand;
import de.omegazirkel.risingworld.commands.SetHealthCommand;
import de.omegazirkel.risingworld.commands.SetHungerCommand;
import de.omegazirkel.risingworld.commands.SetThirstCommand;
import de.omegazirkel.risingworld.commands.SetTimeCommand;
import de.omegazirkel.risingworld.commands.SetWeatherCommand;
import de.omegazirkel.risingworld.commands.SupportCommand;
import de.omegazirkel.risingworld.commands.TeleportToPlayerCommand;
import de.omegazirkel.risingworld.commands.UnAdminCommand;
import de.omegazirkel.risingworld.commands.UnbanCommand;
import de.omegazirkel.risingworld.commands.YellCommand;
import de.omegazirkel.risingworld.listeners.DiscordChatListener;

public class JavaCordBot implements Runnable {

    public static DiscordWebHook pluginInstance = null;
    private static DiscordApi api = null;
    private static boolean running = false;

    static final de.omegazirkel.risingworld.tools.Logger log = new de.omegazirkel.risingworld.tools.Logger(
            "[OZ.DP] [JavaCordBot]");

    public JavaCordBot(final DiscordWebHook plugin) {
        pluginInstance = plugin;
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
        running = true;
        log.out("DiscordBot is now running", 0);
        api = new DiscordApiBuilder().setToken(pluginInstance.getBotToken()).login().join();

        // api.getChannelById("522681749659058186").get().asServerTextChannel().get().sendMessage("It's me, Mario!");

        CommandHandler handler = new JavacordHandler(api);
        handler.registerCommand(new BanCommand());
        handler.registerCommand(new BroadcastCommand());
        handler.registerCommand(new GetBannedCommand());
        handler.registerCommand(new GetTimeCommand());
        handler.registerCommand(new GetVersionCommand());
        handler.registerCommand(new GetWeatherCommand());
        handler.registerCommand(new GroupCommand());
        handler.registerCommand(new HelpCommand());
        handler.registerCommand(new KickCommand());
        handler.registerCommand(new MakeAdminCommand());
        handler.registerCommand(new OnlineCommand());
        handler.registerCommand(new RestartCommand());
        handler.registerCommand(new SetHealthCommand());
        handler.registerCommand(new SetHungerCommand());
        handler.registerCommand(new SetThirstCommand());
        handler.registerCommand(new SetTimeCommand());
        handler.registerCommand(new SetWeatherCommand());
        handler.registerCommand(new SupportCommand());
        handler.registerCommand(new TeleportToPlayerCommand());
        handler.registerCommand(new UnAdminCommand());
        handler.registerCommand(new UnbanCommand());
        handler.registerCommand(new YellCommand());

        api.addMessageCreateListener(new DiscordChatListener(handler));
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
