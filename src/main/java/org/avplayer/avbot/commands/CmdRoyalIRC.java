package org.avplayer.avbot.commands;

import org.avplayer.avbot.RUtils;
import org.avplayer.avbot.AvBot;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdRoyalIRC implements CommandExecutor {

    private final AvBot plugin;

    public CmdRoyalIRC(AvBot instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("royalirc")) {
            if (!cs.hasPermission("royalirc.royalirc")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            plugin.c.reloadConfiguration();
            cs.sendMessage(ChatColor.BLUE + "Reloaded config for " + ChatColor.GRAY + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + ChatColor.BLUE + ".");
            return true;
        }
        return false;
    }

}
