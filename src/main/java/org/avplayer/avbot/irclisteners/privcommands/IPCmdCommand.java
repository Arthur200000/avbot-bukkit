package org.avplayer.avbot.irclisteners.privcommands;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.avplayer.avbot.PermissionHandler;
import org.avplayer.avbot.RUtils;
import org.avplayer.avbot.AvBot;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class IPCmdCommand extends ListenerAdapter {

    private final AvBot plugin;

    public IPCmdCommand(AvBot instance) {
        plugin = instance;
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        final String command = RUtils.getFirstWord(e.getMessage());
        if (!command.equalsIgnoreCase("command")) return;
        final User u = e.getUser();
        if (!PermissionHandler.isAuthedOrAdmin(u.getNick(), u.getServer())) {
            e.respond("You are not allowed to do this.");
            return;
        }
        String[] args = e.getMessage().split(" ");
        args = (String[]) ArrayUtils.subarray(args, 1, args.length);
        if (args.length < 1) {
            e.respond("Usage: command [command]");
            return;
        }
        final String commandToSend = StringUtils.join(args, " ").trim();
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commandToSend);
        e.respond("Sent command to server: /" + commandToSend);
    }
}
