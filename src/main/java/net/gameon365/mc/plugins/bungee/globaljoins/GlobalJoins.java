package net.gameon365.mc.plugins.bungee.globaljoins;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
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
import java.util.UUID;

public class GlobalJoins extends Plugin implements Listener {
    protected String loginString;
    protected String logoutString;

    protected boolean usePrefix;
    protected boolean usePlayerPrefix;

    protected LuckPerms permissions;
    @Override
    public void onEnable()
    {
        this.initalizeConfigIfNotExisting();

        Configuration configuration = null;
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            this.loginString = configuration.getString( "strings.login", "&e%1$s joined the network." );
            this.logoutString = configuration.getString( "strings.logout", "&e%1$s left the network." );

            this.usePrefix = configuration.getBoolean("usePrefix");
            this.usePlayerPrefix = configuration.getBoolean("usePlayerPrefix");
            this.getProxy().getPluginManager().registerListener( this, this );

            if(usePrefix)
            {
                permissions = LuckPermsProvider.get();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    
    @EventHandler
    public void onPostLoginEvent( PostLoginEvent e )
    {
        ProxiedPlayer player = e.getPlayer();
        String prefix = getPrefix(player);
        this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.loginString, e.getPlayer().getName() , prefix) ) );
    }
    
    @EventHandler
    public void onPlayerDisconnectEvent( PlayerDisconnectEvent e )
    {
        ProxiedPlayer player = e.getPlayer();
        String prefix = getPrefix(player);
        this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.logoutString, e.getPlayer().getName() , prefix) ) );
    }

    private String getPrefix(ProxiedPlayer player)
    {
        String prefix = "";
        if(usePrefix)
        {
            PlayerAdapter<ProxiedPlayer> adapter = this.permissions.getPlayerAdapter(ProxiedPlayer.class);

            CachedMetaData metaData = adapter.getMetaData(player);

            // Get their prefix.
            if(usePlayerPrefix)
            {
                prefix = metaData.getPrefix();
            }
            else
            {
                String primarygroup = metaData.getPrimaryGroup();
                Group group = permissions.getGroupManager().getGroup(primarygroup);
                prefix = group.getCachedData().getMetaData().getPrefix();
            }
        }
        return prefix;
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
