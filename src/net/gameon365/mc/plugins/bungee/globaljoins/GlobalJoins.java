package net.gameon365.mc.plugins.bungee.globaljoins;

import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

//import java.util.concurrent.TimeUnit;

public class GlobalJoins extends ConfigurablePlugin implements Listener {
    protected String loginString;
    protected String logoutString;
//    protected Boolean delayedCheck;
//    protected Integer delayedSeconds;
    
    @Override
    public void onEnable()
    {
        this.loginString = this.getConfig().getString( "strings.login", "&e%s joined the network." );
        this.logoutString = this.getConfig().getString( "strings.logout", "&e%s left the network." );
//        this.delayedCheck = this.getConfig().getBoolean( "delayedCheck.enabled", false );
//        this.delayedSeconds = this.getConfig().getInt( "delayedCheck.seconds", 3 );
//        
//        if( this.delayedCheck )
//        {
//            this.getProxy().getLogger().info( "You have delayedCheck enabled.  Please note, this is an experimental feature and may reduce performance and/or stability." );
//        }
//        else if( this.delayedCheck && ( this.delayedSeconds == 0 ) )
//        {
//            this.getProxy().getLogger().warning( "You have delayedCheck enabled, but set the delay to 0 seconds.  This will likely act the same as having delayedCheck disabled, though performance and/or stability may be decreased." );
//        }
//        else if( this.delayedCheck && ( this.delayedSeconds < 0 ) )
//        {
//            this.getProxy().getLogger().severe( "You have delayedCheck enabled, but set the delay to a negative value, which is not acceptable for this option.  Please set the delay to a positive number of seconds.  Because of this, delayedCheck has been disabled for now.");
//            this.delayedCheck = false;
//        }
        
        this.getProxy().getPluginManager().registerListener( this, this );
    }
    
    @EventHandler
    public void onPostLoginEvent( PostLoginEvent e )
    {
//        if( ! this.delayedCheck )
//        {
            this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.loginString, e.getPlayer().getName() ) ) );
//        }
//        else
//        {
//            this.getProxy().getScheduler().schedule( this, new onPostLoginEventTask( e ), this.delayedSeconds, TimeUnit.SECONDS );
//        }
    }
    
//    public class onPostLoginEventTask extends GlobalJoins implements Runnable
//    {
//        protected PostLoginEvent e;
//        protected Boolean getPlayerResult;
//        
//        public onPostLoginEventTask( PostLoginEvent e )
//        {
//            this.e = e;
//            
//        }
//        
//        @Override
//        public void run()
//        {
//            try
//            {
//                this.e.getPlayer();
//                this.getPlayerResult = true;
//            }
//            catch( NullPointerException ex )
//            {
//                this.getPlayerResult = false;
//            }
//            
//            if( this.getPlayerResult )
//            {
//                this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.loginString, e.getPlayer().getName() ) ) );
//            }
//        }
//    }
    
    @EventHandler
    public void onPlayerDisconnectEvent( PlayerDisconnectEvent e )
    {
//        if( ! this.delayedCheck )
//        {
            this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.logoutString, e.getPlayer().getName() ) ) );
//        }
//        else
//        {
//            this.getProxy().getScheduler().schedule( this, new onPlayerDisconnectEvent( e ), this.delayedSeconds, TimeUnit.SECONDS );
//        }
    }
    
//    public class onPlayerDisconnectEvent extends GlobalJoins implements Runnable
//    {
//        protected PlayerDisconnectEvent e;
//        protected Boolean getPlayerResult;
//        
//        public onPlayerDisconnectEvent( PlayerDisconnectEvent e )
//        {
//            this.e = e;
//            
//        }
//        
//        @Override
//        public void run()
//        {
//            try
//            {
//                this.e.getPlayer();
//                this.getPlayerResult = true;
//            }
//            catch( NullPointerException ex )
//            {
//                this.getPlayerResult = false;
//            }
//            
//            if( this.getPlayerResult )
//            {
//                this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.logoutString, e.getPlayer().getName() ) ) );
//            }
//        }
//    }
}
