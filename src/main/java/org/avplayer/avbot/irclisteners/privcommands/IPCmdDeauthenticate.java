package org.avplayer.avbot.irclisteners.privcommands;

import org.avplayer.avbot.PermissionHandler;
import org.avplayer.avbot.RUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class IPCmdDeauthenticate extends ListenerAdapter {

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        final String command = RUtils.getFirstWord(e.getMessage());
        if (!command.equalsIgnoreCase("deauthenticate")) return;
        final User u = e.getUser();
        if (!PermissionHandler.atLeastMod(u.getNick())) {
            e.respond("You are not allowed to do this.");
            return;
        }
        if (!PermissionHandler.isAuthenticated(u.getNick(), u.getServer())) {
            e.respond("Not authenticated!");
            return;
        }
        PermissionHandler.setAuthenticated(e.getUser().getNick(), e.getUser().getServer(), false);
        e.respond("Deuthenticated!");
    }
}
