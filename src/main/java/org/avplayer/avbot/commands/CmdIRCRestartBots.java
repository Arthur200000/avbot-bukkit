package org.avplayer.avbot.commands;

import org.avplayer.avbot.RUtils;
import org.avplayer.avbot.AvBot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdIRCRestartBots implements CommandExecutor {

    private final AvBot plugin;

    public CmdIRCRestartBots(AvBot instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ircrestartbots")) {
            if (!cs.hasPermission("royalirc.ircrestartbots")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            plugin.bh.disconnect();
            plugin.bh.clearListenerManager();
            plugin.bh.createBots();
            return true;
        }
        return false;
    }

}
