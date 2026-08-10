package fathertoast.crust.api.event.config;

import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

/**
 * The base event used for all Crust config events.
 */
public class CrustConfigEvent extends Event {
    
    /** The config manager. */
    public final ConfigManager manager;
    
    protected CrustConfigEvent( ConfigManager cfgManager ) {
        manager = cfgManager;
    }
    
    /**
     * Fired when a new config manager is being created, right before it is registered.
     * Config managers can be created at any time, which may be during mod construction, therefore if you want to
     * catch a specific mod's manager creation event, you should ensure your mod is earlier in the load order.
     * <p>
     * This event is not cancelable and does not have a result.
     * <p>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS} and may not be on the main thread.
     */
    public static class ManagerCreated extends CrustConfigEvent {
        @ApiStatus.Internal
        public ManagerCreated( ConfigManager cfgManager ) { super( cfgManager ); }
    }
    
    /**
     * The base event used for Crust config file events.
     */
    public static class File extends CrustConfigEvent {
        
        /** The config file. */
        public final AbstractConfigFile file;
        /** The config spec. */
        public final CrustConfigSpec spec;
        
        protected File( AbstractConfigFile cfgFile ) {
            super( cfgFile.SPEC.MANAGER );
            file = cfgFile;
            spec = cfgFile.SPEC;
        }
        
        /**
         * Fired when a new config file is being created, right after the spec's file header has been defined.
         * Config files can be constructed at any time, which may be during mod construction, therefore if you want to
         * catch a specific mod's file construction event, you should ensure your mod is earlier in the load order.
         * <p>
         * You can add comments or even define new fields to the spec here; everything you do with the spec
         * will be immediately after the header and before the rest of the spec's normal content.
         * <p>
         * This event is not cancelable and does not have a result.
         * <p>
         * This event is fired on the {@link MinecraftForge#EVENT_BUS} and may not be on the main thread.
         */
        public static class Constructed extends File {
            @ApiStatus.Internal
            public Constructed( AbstractConfigFile cfgFile ) { super( cfgFile ); }
        }
        
        /**
         * Fired after a config file has been initialized (first-time load).
         * Config files can be initialized at any time, which may be during mod construction, therefore if you want to
         * catch a specific mod's file initialization event, you should ensure your mod is earlier in the load order.
         * <p>
         * This event is not cancelable and does not have a result.
         * <p>
         * This event is fired on the {@link MinecraftForge#EVENT_BUS} and may not be on the main thread.
         */
        public static class Initialized extends File {
            /** True if the file encountered any load errors. */
            public final boolean errored;
            
            @ApiStatus.Internal
            public Initialized( AbstractConfigFile cfgFile, boolean hasErrors ) {
                super( cfgFile );
                errored = hasErrors;
            }
        }
        
        /**
         * Fired after a config file has been loaded.
         * <p>
         * This event is not cancelable and does not have a result.
         * <p>
         * This event is fired on the {@link MinecraftForge#EVENT_BUS} and may not be on the main thread.
         */
        public static class Loaded extends File {
            /**
             * True for the first-time load or if the file has been changed. Changed files will be
             * {@linkplain Saved saved} to disk and then {@linkplain Synced synced} to clients if needed.
             */
            public final boolean changed;
            
            @ApiStatus.Internal
            public Loaded( AbstractConfigFile cfgFile, boolean rewrite ) {
                super( cfgFile );
                changed = rewrite;
            }
        }
        
        /**
         * Fired after a config file has been saved.
         * <p>
         * This event is not cancelable and does not have a result.
         * <p>
         * This event is fired on the {@link MinecraftForge#EVENT_BUS} and may not be on the main thread.
         */
        public static class Saved extends File {
            @ApiStatus.Internal
            public Saved( AbstractConfigFile cfgFile ) { super( cfgFile ); }
        }
        
        /**
         * Fired after a config file has been synced by sending a sync packet (on logical server)
         * or after receiving a sync packet (on logical client).
         * <p>
         * When a player joins a server, a sync packet is sent to that player for each synced config.
         * After a synced config is {@linkplain Loaded reloaded}, a sync packet is sent to each player for that config.
         * <p>
         * This event is not cancelable and does not have a result.
         * <p>
         * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
         */
        public static class Synced extends File {
            /** The player we sent the sync to (on server) or who is receiving the sync (on client). */
            public final Player player;
            
            @ApiStatus.Internal
            public Synced( AbstractConfigFile cfgFile, Player thePlayer ) {
                super( cfgFile );
                player = thePlayer;
            }
        }
        
        /**
         * Fired when a common-sided config file has been selected in the in-game editor.
         * <p>
         * On the client, this happens for each common-side file shown when a mod is selected,
         * after the {@link WriteAccessRequest} event and only if write access was denied.<p>
         * On the server, this happens when the player tries to open a file.
         * <p>
         * This event has a result:<p>
         * * {@link Result#ALLOW} means read access will be granted.<p>
         * * {@link Result#DEFAULT} means the player's permissions will be checked against the config setting.<p>
         * * {@link Result#DENY} means read access will be denied.
         * <p>
         * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
         */
        @Event.HasResult
        public static class ReadAccessRequest extends File {
            /** The player requesting the read-access data. */
            public final Player player;
            
            @ApiStatus.Internal
            public ReadAccessRequest( AbstractConfigFile cfgFile, Player thePlayer ) {
                super( cfgFile );
                player = thePlayer;
            }
        }
        
        /**
         * Fired when a common-sided config file has been edited in the in-game editor.
         * <p>
         * On the client, this happens for each common-side file shown when a mod is selected,
         * before the {@link ReadAccessRequest} event.<p>
         * On the server, this happens when the player tries to save changes to a file.
         * <p>
         * Note that if write access is denied on the server side, the player will be forcibly disconnected.
         * <p>
         * This event has a result:<p>
         * * {@link Result#ALLOW} means write access will be granted.<p>
         * * {@link Result#DEFAULT} means the player's permissions will be checked.<p>
         * * {@link Result#DENY} means write access will be denied.
         * <p>
         * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
         */
        @Event.HasResult
        public static class WriteAccessRequest extends File {
            /** The player requesting the read-access data. */
            public final Player player;
            
            @ApiStatus.Internal
            public WriteAccessRequest( AbstractConfigFile cfgFile, Player thePlayer ) {
                super( cfgFile );
                player = thePlayer;
            }
        }
    }
}