package org.avplayer.avbot;

import org.avplayer.avbot.irclisteners.IChatListener;
import org.avplayer.avbot.irclisteners.IChatRelay;
import org.avplayer.avbot.irclisteners.IKickListener;
import org.avplayer.avbot.irclisteners.fantasy.ICmdKick;
import org.avplayer.avbot.irclisteners.fantasy.ICmdPlayers;
import org.avplayer.avbot.irclisteners.privcommands.IPCmdAuthenticate;
import org.avplayer.avbot.irclisteners.privcommands.IPCmdCommand;
import org.avplayer.avbot.irclisteners.privcommands.IPCmdDeauthenticate;
import org.avplayer.avbot.irclisteners.privcommands.IPCmdJoin;
import org.avplayer.avbot.irclisteners.privcommands.IPCmdMessage;
import org.avplayer.avbot.irclisteners.privcommands.IPCmdPart;
import org.avplayer.avbot.irclisteners.privcommands.IPCmdPrivmsg;
import org.avplayer.avbot.irclisteners.privcommands.IPCmdRaw;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;

import java.util.ArrayList;
import java.util.List;

public class BotHandler {

    private final AvBot plugin;
    private final List<RoyalIRCBot> bots = new ArrayList<RoyalIRCBot>();
    private final ListenerManager<PircBotX> lm = new ThreadedListenerManager<PircBotX>();

    public BotHandler(AvBot instance) {
        plugin = instance;
    }

    private String convertMessages(String s) {
        if (Config.parseMinecraftColors) s = ChatColor.translateAlternateColorCodes('&', s);
        if (Config.allowColors) s = RUtils.minecraftColorstoIRCColors(s);
        else s = ChatColor.stripColor(s);
        return s;
    }

    public void createBots() {
        if (lm.getListeners().size() < 1) {
            lm.addListener(new IChatListener(plugin));
            lm.addListener(new IChatRelay(plugin));
            lm.addListener(new IKickListener());
            lm.addListener(new ICmdPlayers(plugin));
            lm.addListener(new ICmdKick(plugin));
            lm.addListener(new IPCmdMessage(plugin));
            lm.addListener(new IPCmdCommand(plugin));
            lm.addListener(new IPCmdAuthenticate(plugin));
            lm.addListener(new IPCmdDeauthenticate());
            lm.addListener(new IPCmdPrivmsg());
            lm.addListener(new IPCmdRaw());
            lm.addListener(new IPCmdJoin());
            lm.addListener(new IPCmdPart());
        }
        ConfigurationSection servers = plugin.getConfig().getConfigurationSection("servers");
        for (String server : servers.getKeys(false)) {
            plugin.getLogger().info("Starting bot " + server + "...");
            final RoyalIRCBot rib = new RoyalIRCBot(plugin, servers.getConfigurationSection(server), lm);
            synchronized (bots) {
                bots.add(rib);
            }
            plugin.getLogger().info("Bot initiated.");
        }
    }

    public void sendMessage(String message) {
        message = convertMessages(message);
        synchronized (bots) {
            for (RoyalIRCBot bot : bots) {
                for (Channel c : bot.getBackend().getUserBot().getChannels()) c.send().message(message);
            }
        }
    }

    public void sendMessage(String message, Channel c) {
        message = convertMessages(message);
        c.send().message(message);
    }

    public void sendMessageToOtherChannels(String message, Channel dontSendTo) {
        message = convertMessages(message);
        synchronized (bots) {
            for (RoyalIRCBot bot : bots) {
                for (Channel c : bot.getBackend().getUserBot().getChannels()) {
                    if (RUtils.sameChannels(dontSendTo, c)) continue;
                    c.send().message(message);
                }
            }
        }
    }

    public void sendMessageToOtherServers(String message, String dontSendTo) {
        message = convertMessages(message);
        synchronized (bots) {
            for (RoyalIRCBot bot : bots) {
                if (bot.getBackend().getServerInfo().getServerName().equals(dontSendTo)) continue;
                for (Channel c : bot.getBackend().getUserBot().getChannels()) {
                    c.send().message(message);
                }
            }
        }
    }

    public void disconnect() {
        try {
            synchronized (bots) {
                for (final RoyalIRCBot bot : bots) {
                    bot.getBackend().sendIRC().quitServer("AvBot disabled.");
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void clearListenerManager() {
        final Listener[] ls = lm.getListeners().toArray(new Listener[lm.getListeners().size()]);
        for (Listener l : ls) lm.removeListener(l);
    }

    public List<RoyalIRCBot> getBots() {
        return bots;
    }

    public RoyalIRCBot getBotByServer(String server) {
        for (RoyalIRCBot rib : bots) {
            if (!rib.getBackend().getConfiguration().getServerHostname().equalsIgnoreCase(server)) continue;
            return rib;
        }
        return null;
    }

    public boolean userInChannels(User u) {
        for (Channel uC : u.getChannels()) {
            for (Channel bC : u.getBot().getUserBot().getChannels()) {
                if (!uC.getName().equalsIgnoreCase(bC.getName())) continue;
                return true;
            }
        }
        return false;
    }

}
