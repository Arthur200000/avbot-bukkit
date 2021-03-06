package org.avplayer.avbot.irclisteners;

import org.avplayer.avbot.Config;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.KickEvent;

public class IKickListener extends ListenerAdapter {

    @Override
    public void onKick(KickEvent e) {
        if (!Config.rejoinOnKick) return;
        if (!e.getRecipient().getNick().equalsIgnoreCase(e.getBot().getNick())) return;
        try {
            Thread.sleep(Config.rejoinWaitTime * 1000L);
        } catch (InterruptedException ignored) {
        }
        e.getBot().sendIRC().joinChannel(e.getChannel().getName());
    }
}
