package net.gameon365.mc.plugins.bungee.globaljoins;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GlobalJoins extends Plugin implements Listener {
    protected String loginString;
    protected String logoutString;
    protected String discordHook;

    // used to suppress dud joins of banhammered's
    private Set<UUID> validPlayers = new HashSet<>();

    protected boolean usePrefix;
    protected boolean usePlayerPrefix;

    protected boolean useSuffix;
    protected boolean useDisplayName;

    protected LuckPerms permissions;
    @Override
    public void onEnable()
    {
        this.initalizeConfigIfNotExisting();
        permissions = LuckPermsProvider.get();

        loadConfig();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new GJCommand(this));
    }

    public void loadConfig()
    {
        Configuration configuration = null;
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            this.loginString = configuration.getString( "strings.login", "&e%1$s joined the network." );
            this.logoutString = configuration.getString( "strings.logout", "&e%1$s left the network." );
            this.discordHook = configuration.getString( "urls.discord", null);
            if(this.discordHook!=null&&!this.discordHook.startsWith("https"))
            {
                this.discordHook=null;
            }

            this.usePrefix = configuration.getBoolean("usePrefix");
            this.usePlayerPrefix = configuration.getBoolean("usePlayerPrefix");
            this.getProxy().getPluginManager().registerListener( this, this );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLoginEvent( PostLoginEvent e )
    {
        /*
        ProxiedPlayer player = e.getPlayer();
        String prefix = getPrefix(player);
        String suffix = getSuffix(player);
        String groupname = getDisplayname(player);
        this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.loginString, e.getPlayer().getName() , prefix,suffix,groupname) ) );*/
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnServerJoined(ServerConnectedEvent e)
    {

        ProxiedPlayer player = e.getPlayer();
        if(validPlayers.contains(player.getUniqueId()))
        {
            System.out.println("Switcher");
            return; //nothing to do since its a switch
        }
        else
        {
            System.out.println("Logg");
            String prefix = getPrefix(player);
            String suffix = getSuffix(player);
            String groupname = getDisplayname(player);
            validPlayers.add(player.getUniqueId());
            this.getProxy().broadcast(ChatColor.translateAlternateColorCodes('&', String.format(this.loginString, e.getPlayer().getName(), prefix, suffix, groupname)));
            fireDiscordWebhook(true,
                    ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', String.format(this.loginString, e.getPlayer().getName(), prefix, suffix, groupname)))
                    ,e.getPlayer().getUniqueId().toString().replace("-",""));
        }
    }

    @EventHandler
    public void onPlayerDisconnectEvent( PlayerDisconnectEvent e )
    {
        ProxiedPlayer player = e.getPlayer();
        String prefix = getPrefix(player);
        String suffix = getSuffix(player);
        String groupname = getDisplayname(player);
        if(validPlayers.contains(player.getUniqueId()))
        {
            validPlayers.remove(player.getUniqueId());
            this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.logoutString, e.getPlayer().getName() , prefix,suffix,groupname) ) );
            fireDiscordWebhook(false,
                    ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', String.format(this.logoutString, e.getPlayer().getName(), prefix, suffix, groupname)))
                    ,e.getPlayer().getUniqueId().toString().replace("-",""));
        }
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

    private String getSuffix(ProxiedPlayer player)
    {
        String prefix = "";
        if(usePrefix)
        {
            PlayerAdapter<ProxiedPlayer> adapter = this.permissions.getPlayerAdapter(ProxiedPlayer.class);

            CachedMetaData metaData = adapter.getMetaData(player);

            // Get their prefix.
            if(usePlayerPrefix)
            {
                prefix = metaData.getSuffix();
            }
            else
            {
                String primarygroup = metaData.getPrimaryGroup();
                Group group = permissions.getGroupManager().getGroup(primarygroup);
                prefix = group.getCachedData().getMetaData().getSuffix();
            }
        }
        return prefix;
    }

    private String getDisplayname(ProxiedPlayer player)
    {
        String prefix = "";
        if(usePrefix)
        {
            PlayerAdapter<ProxiedPlayer> adapter = this.permissions.getPlayerAdapter(ProxiedPlayer.class);
            CachedMetaData metaData = adapter.getMetaData(player);
            String primarygroup = metaData.getPrimaryGroup();
            Group group = permissions.getGroupManager().getGroup(primarygroup);
            prefix = group.getDisplayName();
            if(prefix==null)
            {
                prefix = group.getFriendlyName();
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


    private void fireDiscordWebhook(final boolean login, final String msg, final String strippeduuid)
    {
        if(this.discordHook==null)
        {
            return;
        }

        Runnable later = ()->
        {
            DiscordWebhook webhook = new DiscordWebhook(this.discordHook);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setColor(login?Color.GREEN:Color.RED)
                    .setAuthor(msg, "", "https://crafatar.com/avatars/"+strippeduuid+"?size=128&overlay#"));
                    //.setUrl("https://kryptongta.com"));
            try {
                webhook.execute(); //Handle exception
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        getProxy().getScheduler().runAsync(this,later);
        // https://crafatar.com/avatars/{uuid-nodashes}?size={size}&overlay#{texture}
    }
}
