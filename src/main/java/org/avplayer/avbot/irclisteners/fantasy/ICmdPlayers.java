package org.avplayer.avbot.irclisteners.fantasy;

import org.avplayer.avbot.RUtils;
import org.avplayer.avbot.AvBot;
import org.avplayer.avbot.VNPHandler;
import org.bukkit.entity.Player;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class ICmdPlayers extends ListenerAdapter {

    private final AvBot plugin;

    public ICmdPlayers(AvBot instance) {
        plugin = instance;
    }

    @Override
    public void onMessage(MessageEvent e) {
        if (!RUtils.isFantasyCommand(e.getMessage())) return;
        final String command = RUtils.getFantasyCommand(e.getMessage());
        if (!command.equalsIgnoreCase("players")) return;
        Player[] online = plugin.getServer().getOnlinePlayers();
        int vanished = 0;
        StringBuilder sb = new StringBuilder("Players (%s/%s): ");
        for (Player p : online) {
            if (plugin.vanishLoaded() && VNPHandler.isVanished(p)) {
                vanished++;
                continue;
            }
            sb.append(p.getName());
            sb.append(", ");
        }
        int visible = online.length - vanished;
        String toSend = String.format(sb.toString(), visible, plugin.getServer().getMaxPlayers());
        if (visible > 0) toSend = toSend.substring(0, toSend.length() - 2);
        e.respond(toSend);
    }
}
