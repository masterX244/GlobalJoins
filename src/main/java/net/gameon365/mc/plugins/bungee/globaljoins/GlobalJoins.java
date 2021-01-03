package net.gameon365.mc.plugins.bungee.globaljoins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class GlobalJoins extends Plugin implements Listener {
    protected String loginString;
    protected String logoutString;
    
    @Override
    public void onEnable()
    {
        this.initalizeConfigIfNotExisting();

        Configuration configuration = null;
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            this.loginString = configuration.getString( "strings.login", "&e%s joined the network." );
            this.logoutString = configuration.getString( "strings.logout", "&e%s left the network." );

            this.getProxy().getPluginManager().registerListener( this, this );
        } catch (IOException e) {
            e.printStackTrace();
        }

        
    }
    
    @EventHandler
    public void onPostLoginEvent( PostLoginEvent e )
    {
        this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.loginString, e.getPlayer().getName() ) ) );
    }
    
    @EventHandler
    public void onPlayerDisconnectEvent( PlayerDisconnectEvent e )
    {
        this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.logoutString, e.getPlayer().getName() ) ) );
    }
    
    private void initalizeConfigIfNotExisting()
    {
        if (!getDataFolder().exists())
        getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

       
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
