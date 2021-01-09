package net.gameon365.mc.plugins.bungee.globaljoins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GJCommand extends Command {

    GlobalJoins plugin;

    public GJCommand(GlobalJoins main) {
        super("gj","globaljoins.gj");
        plugin=main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length!=1)
        {
            usage(sender);
        }
        else
        {
            switch(args[0].toLowerCase())
            {
                case "reload":
                    plugin.loadConfig();
                    sender.sendMessage(new ComponentBuilder("Config Reloaded").color(ChatColor.RED).create());
                    break;
                default:
                    usage(sender);
                    break;
            }
        }

    }

    private void usage(CommandSender sender)
    {
        sender.sendMessage(new ComponentBuilder("Usage").color(ChatColor.RED).create());
        sender.sendMessage(new ComponentBuilder("/gj reload for reloading").color(ChatColor.RED).create());
    }



}
