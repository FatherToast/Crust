package fathertoast.crust.api.config.common;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Used as the hub for config access.
 * <p>
 * The config manager for any particular mod can be looked up by mod id. From here, you can access
 * the mod's config files and all fields defined in those files' specs.
 */
public final class ConfigManager {
    
    /**
     * Creates a new config manager that operates out of a folder within the config directory.
     * This is the recommended method for most mods, especially mods that use more than one config file.
     *
     * @param path The folder the new config manager will use for its config files, relative to the Forge config directory.
     *             By convention, this should be the mod's name.
     * @return The new config manager.
     * @throws IllegalStateException If your mod has already created a config manager.
     */
    public static ConfigManager create( String path ) {
        return register( new ConfigManager( new File( FMLPaths.CONFIGDIR.get().toFile(), path + "/" ) ) );
    }
    
    /**
     * Creates a new config manager that operates directly within the config directory.
     * This is the recommended method for very simple mods that only need a few options.
     *
     * @return The new config manager.
     * @throws IllegalStateException If your mod has already created a config manager.
     */
    public static ConfigManager createSimple() {
        return register( new ConfigManager( FMLPaths.CONFIGDIR.get().toFile() ) );
    }
    
    /**
     * Gets the config manager for a particular mod, if it has one.
     *
     * @param modId The id of the mod we want the config manager for.
     * @return The mod's config manager, or null if the mod doesn't have one.
     */
    @Nullable
    public static ConfigManager get( String modId ) { return MOD_ID_TO_CM_MAP.get( modId ); }
    
    
    // ---- Instance Methods ---- //
    
    /** The id of the mod that owns this config manager. */
    public final String MOD_ID;
    
    /** The root folder for managed config files. */
    public final File DIR;
    
    /** It's a good idea to freeze the file watcher while initializing a large number of files; can prevent a few unneeded reloads. */
    public volatile boolean freezeFileWatcher;
    
    /** @return A read-only list of all config files this manages. */
    public List<AbstractConfigFile> getConfigs() { return Collections.unmodifiableList( configs ); }
    
    
    // ---- Internal Methods ---- //
    
    /** Mapping of each mod id to its config manager. */
    private static final HashMap<String, ConfigManager> MOD_ID_TO_CM_MAP = new HashMap<>();
    
    /**
     * Registers a config manager with Crust so that it can be tracked and referenced if needed.
     *
     * @param cfgManager The config manager to register with Crust.
     * @return The config manager, for convenience.
     * @throws IllegalStateException If the config manager's owner has already registered a config manager.
     */
    private static ConfigManager register( ConfigManager cfgManager ) {
        if( MOD_ID_TO_CM_MAP.containsKey( cfgManager.MOD_ID ) ) {
            throw new IllegalStateException( "Mod '" + cfgManager.MOD_ID + "' cannot have multiple config managers!" );
        }
        MOD_ID_TO_CM_MAP.put( cfgManager.MOD_ID, cfgManager );
        return cfgManager;
    }
    
    /** The config files this manages. */
    private final List<AbstractConfigFile> configs = new ArrayList<>();
    
    private ConfigManager( File configDir ) {
        MOD_ID = ModLoadingContext.get().getActiveNamespace();
        DIR = configDir;
        
        if( MOD_ID.equals( "minecraft" ) )
            throw new IllegalStateException( "Attempted to create config manager from invalid mod loading context!" );
    }
    
    /** Called by config files on creation to keep track of them. */
    void register( AbstractConfigFile cfg ) { configs.add( cfg ); }
}