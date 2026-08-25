package fathertoast.crust.api.config.common.file;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.CharacterOutput;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingException;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.field.RestartNote;
import fathertoast.crust.api.config.common.file.action.*;
import fathertoast.crust.api.event.config.CrustConfigEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A config spec maps read and write functions to the runtime variables used to hold them.
 * <p>
 * Contains methods to build the config spec similarly to writing a default file, allowing
 * insertion of fields, load actions, comments, and formatting as desired.
 */
@SuppressWarnings( "unused" )
public class CrustConfigSpec {
    
    /** The config spec's manager. */
    public final ConfigManager MANAGER;
    
    /** The config spec's config file instance. */
    public final AbstractConfigFile FILE;
    
    /** The name of the config. The file name is this plus the file extension. */
    public final String NAME;
    
    /**
     * True if this config is only on the client. This allows any user to view and edit these files using
     * the in game editor, even when on a server. Client-only files cannot have synced fields.
     */
    public final boolean CLIENT_ONLY;
    
    /** The base key prefix to use for all fields, based on the currently loading config category. */
    public String loadingCategory;
    
    /** @return The file this config spec loads to/from. */
    public File getFile() { return NIGHT_CONFIG_FILE.getFile(); }
    
    /** @return The file path, relative to the game directory, that this config spec loads to/from. */
    public String getFilePath() { return ConfigUtil.toRelativePath( getFile() ); }
    
    /** @return A read-only map of all keys defined in this spec to their config fields. */
    public Map<String, IConfigField<?>> getFields() { return Collections.unmodifiableMap( FIELD_MAP ); }
    
    /** @return True if the config contains any fields that need to be synced to clients. */
    public boolean isSynced() { return synced; }
    
    /**
     * @return True if the config is initialized, and therefore safe to use (though specific field types may
     * still be unsafe).
     */
    public boolean isInitialized() { return initialized; }
    
    /**
     * Performs first-time loading of the config from disk and registers it in the auto-reload system.
     * <p>
     * You must call this method when you want the initialization to occur. This method immediately loads,
     * so the config file's values will be immediately ready to use
     */
    public void initialize() {
        ConfigUtil.LOG.info( "First-time loading config file {}", getFilePath() );
        boolean hasErrors = false;
        
        try {
            NIGHT_CONFIG_FILE.load();
        }
        catch( ParsingException ex ) {
            ConfigUtil.LOG.error( "Failed first-time loading of config file {} - this is bad!",
                    getFilePath(), ex );
            hasErrors = true;
        }
        
        // Crash dedicated servers if any parsing errors occurred.
        if( hasErrors && FMLEnvironment.dist == Dist.DEDICATED_SERVER ) {
            ConfigUtil.LOG.error( "The config '{}' is broken or malformed, and the game likely can't persist safely!", getFilePath() );
            throw new IllegalStateException( "Encountered broken or malformed config: " + getFilePath() );
        }
        
        try {
            FileWatcher.defaultInstance().addWatch( NIGHT_CONFIG_FILE.getFile(), this::onFileChanged );
            ConfigUtil.LOG.info( "Started watching config file {} for updates", getFilePath() );
        }
        catch( IOException ex ) {
            ConfigUtil.LOG.error( "Failed to watch config file {} - this file will NOT update in-game until restarted!",
                    getFilePath(), ex );
            hasErrors = true;
        }
        initialized = !hasErrors;
        
        MinecraftForge.EVENT_BUS.post( new CrustConfigEvent.File.Initialized( FILE, hasErrors ) );
    }
    
    
    // ---- Spec Building Methods ---- //
    
    /** Adds an action to this spec. Actions determine how a file is saved, loaded, and displayed in the in-game editor. */
    public void add( ISpecAction action ) { ACTIONS.add( action ); }
    
    
    /**
     * Adds a field. The added field will automatically update its value when the config file is loaded.
     * It is good practice to avoid storing the field's value whenever possible.
     * <p>
     * When not possible (e.g. the field is used to initialize something that you can't modify afterward),
     * consider providing a restart note to inform users of the limitation.
     *
     * @param field The field to define in this config spec.
     * @return The same field for convenience in constructing.
     * @throws IllegalStateException If the spec already has a field defined for the same key.
     */
    public <T, F extends IConfigField<T>> F define( F field ) { return define( field, false ); }
    
    /**
     * Adds a field. The added field will automatically update its value when the config file is loaded.
     * It is good practice to avoid storing the field's value whenever possible.
     * <p>
     * When not possible (e.g. the field is used to initialize something that you can't modify afterward),
     * consider providing a restart note to inform users of the limitation.
     *
     * @param field The field to define in this config spec.
     * @param sync  True if the field's value should be synced from the server.
     * @return The same field for convenience in constructing.
     * @throws IllegalStateException If the spec already has a field defined for the same key.
     */
    public <T, F extends IConfigField<T>> F define( F field, boolean sync ) { return define( field, sync, null ); }
    
    /**
     * Adds a field. The added field will automatically update its value when the config file is loaded.
     * It is good practice to avoid storing the field's value whenever possible.
     * <p>
     * When not possible (e.g. the field is used to initialize something that you can't modify afterward),
     * consider providing a restart note to inform users of the limitation.
     *
     * @param field       The field to define in this config spec.
     * @param restartNote Note to provide for the field's restart requirements.
     * @return The same field for convenience in constructing.
     * @throws IllegalStateException If the spec already has a field defined for the same key.
     */
    public <T, F extends IConfigField<T>> F define( F field, @Nullable RestartNote restartNote ) {
        return define( field, false, restartNote );
    }
    
    /**
     * Adds a field. The added field will automatically update its value when the config file is loaded.
     * It is good practice to avoid storing the field's value whenever possible.
     * <p>
     * When not possible (e.g. the field is used to initialize something that you can't modify afterward),
     * consider providing a restart note to inform users of the limitation.
     *
     * @param field       The field to define in this config spec.
     * @param sync        True if the field's value should be synced from the server.
     * @param restartNote Note to provide for the field's restart requirements.
     * @return The same field for convenience in constructing.
     * @throws IllegalStateException If the spec already has a field defined for the same key.
     */
    public <T, F extends IConfigField<T>> F define( F field, boolean sync, @Nullable RestartNote restartNote ) {
        FieldAction<T> action = new FieldAction<>( this, field, sync, restartNote );
        field.setSpec( this );
        if( sync ) {
            if( CLIENT_ONLY ) throw new IllegalArgumentException( "Client-only files cannot have synced fields!" );
            synced = true;
        }
        if( sync && restartNote == RestartNote.GAME ) {
            throw new IllegalArgumentException( "Attempted to register synced field '" + field.getKey() +
                    "' that requires a game restart (impossible to sync) in config " + NAME );
        }
        if( FIELD_MAP.containsKey( field.getKey() ) ) {
            throw new IllegalStateException( "Attempted to register duplicate field key '" + field.getKey() +
                    "' in config " + NAME );
        }
        FIELD_MAP.put( field.getKey(), field );
        add( action );
        return field;
    }
    
    
    /**
     * Registers a runnable (or void no-argument method reference) to be called when the config is loaded.
     * It is called at exactly the point defined, so fields defined above will be loaded with new values, while fields
     * below will still contain their previous values (null/zero on the first load).
     * <p>
     * This is effectively an "on config loading" event.
     *
     * @param callback The callback to run on read.
     */
    public void callback( Runnable callback ) { add( new ReadAction( callback ) ); }
    
    
    /** Inserts a single new line. */
    public void newLine() { newLine( 1 ); }
    
    /** @param count The number of new lines to insert. */
    public void newLine( int count ) { add( new FormatAction.NewLines( count ) ); }
    
    /** Inserts a single new line. In the GUI, no lines are inserted. */
    public void fileOnlyNewLine() { fileOnlyNewLine( 1 ); }
    
    /** @param count The number of new lines to insert. In the GUI, no lines are inserted. */
    public void fileOnlyNewLine( int count ) { add( new FormatAction.FileOnlyNewLines( count ) ); }
    
    
    /** Increases the indent by one level. */
    public void increaseIndent() { indent( +1 ); }
    
    /** Decreases the indent by one level. */
    public void decreaseIndent() { indent( -1 ); }
    
    /** @param count The amount to change the indent by. */
    public void indent( int count ) { add( new FormatAction.Indent( count ) ); }
    
    
    /**
     * Adds a comment. Each argument is printed on a separate line, in the order given.
     *
     * @param comment The comment to insert.
     */
    public void comment( String... comment ) { comment( TomlHelper.newComment( comment ) ); }
    
    /**
     * Adds a comment. Each string in the list is printed on a separate line, in the order returned by iteration.
     *
     * @param comment The comment to insert.
     */
    public void comment( List<String> comment ) { add( new FormatAction.Comment( comment ) ); }
    
    /**
     * Adds a comment. After the title, each string in the list is printed on a separate line, in the order returned by
     * iteration. In the GUI, the comment is only shown as a tooltip when the mouse is over the title.
     *
     * @param title   The comment's title.
     * @param comment The comment to insert.
     */
    public void titledComment( String title, String... comment ) { titledComment( title, TomlHelper.newComment( comment ) ); }
    
    /**
     * Adds a comment. After the title, each string in the list is printed on a separate line, in the order returned by
     * iteration. In the GUI, the comment is only shown as a tooltip when the mouse is over the title.
     *
     * @param title   The comment's title.
     * @param comment The comment to insert.
     */
    public void titledComment( String title, List<String> comment ) { add( new FormatAction.TitledComment( title, comment ) ); }
    
    /**
     * Adds a comment. Each argument is printed on a separate line, in the order given.
     * In the GUI, this comment is NOT shown.
     *
     * @param comment The comment to insert.
     */
    public void fileOnlyComment( String... comment ) { fileOnlyComment( TomlHelper.newComment( comment ) ); }
    
    /**
     * Adds a comment with added space around it. Each argument is printed on a separate line, in the order given.
     * In the GUI, this comment is NOT shown.
     *
     * @param comment The comment to insert.
     */
    public void paddedFileOnlyComment( String... comment ) {
        fileOnlyNewLine();
        fileOnlyComment( TomlHelper.newComment( comment ) );
        fileOnlyNewLine();
    }
    
    /**
     * Adds a comment. Each string in the list is printed on a separate line, in the order returned by iteration.
     * In the GUI, this comment is NOT shown.
     *
     * @param comment The comment to insert.
     */
    public void fileOnlyComment( List<String> comment ) { add( new FormatAction.FileOnlyComment( comment ) ); }
    
    /**
     * Adds a comment with added space around it. Each string in the list is printed on a separate line,
     * in the order returned by iteration.
     * In the GUI, this comment is NOT shown.
     *
     * @param comment The comment to insert.
     */
    public void paddedFileOnlyComment( List<String> comment ) {
        fileOnlyNewLine();
        add( new FormatAction.FileOnlyComment( comment ) );
        fileOnlyNewLine();
    }
    
    
    /**
     * Sets up this spec as a simple file, so that you can just start defining fields and they'll print nicely.
     * There is no need to call this if you extend {@link AbstractConfigFile.Simple}, as it is already called for you.
     * <p>
     * This decreases the indent by 1, so if you plan on making categories, etc. later in the file, it is good
     * practice to increase the indent back to its original value before doing so.
     */
    public void setupSimpleFile() {
        newLine( 2 );
        decreaseIndent();
    }
    
    /**
     * Adds a category header with a comment to describe/summarize the contents of the category section.
     * All following fields you define will be considered part of this category until you define another category.
     * <p>
     * NOTE: Do not call this for {@link AbstractConfigCategory} - only to add in-line categories to simple files.
     *
     * @param name    The category name.
     * @param comment The category comment to insert.
     */
    public void category( String name, String... comment ) {
        add( new FormatAction.SimpleCategory( this, name, TomlHelper.newComment( comment ) ) );
    }
    
    
    /**
     * Adds a subcategory header, optionally including a comment to describe/summarize the contents of the section.
     * <p>
     * The header and its comment are printed at the current indent level - 1. Therefore, it is good practice to always
     * increase the indent before the first subcategory and then decrease the indent after the final subcategory.
     *
     * @param name    The subcategory name.
     * @param comment The subcategory comment to insert.
     */
    public void subcategory( String name, String... comment ) {
        add( new FormatAction.Subcategory( this, name, TomlHelper.newComment( comment ) ) );
    }
    
    /**
     * Adds a header to signal the start of the appendix section, optionally including a comment to describe/summarize
     * the section.
     *
     * @param comment The appendix comment to insert.
     */
    public void appendixHeader( String... comment ) { add( new FormatAction.AppendixHeader( TomlHelper.newComment( comment ) ) ); }
    
    
    // ---- Internal Methods ---- //
    
    /**
     * NOTE: You should never need to call this method. It is called automatically in the config file constructor.
     * <p>
     * Adds a config header with a comment to describe/summarize the contents of the file.
     *
     * @param comment The file comment to insert.
     */
    @ApiStatus.Internal
    public void header( List<String> comment ) { add( new FormatAction.FileHeader( this, comment ) ); }
    
    /**
     * NOTE: You should never need to call this method. It is called automatically in the config category constructor.
     * <p>
     * Adds a category header with a comment to describe/summarize the contents of the category section.
     *
     * @param name    The category name.
     * @param comment The category comment to insert.
     */
    @ApiStatus.Internal
    public void category( String name, List<String> comment ) {
        add( new FormatAction.Category( this, name, comment ) );
    }
    
    
    /** The underlying NightConfig config. */
    private final FileConfig NIGHT_CONFIG_FILE;
    
    /** The list of actions to perform, in a specific order, when reading or writing the config file. */
    private final List<ISpecAction> ACTIONS = new ArrayList<>();
    /** The fields defined in this spec. */
    private final Map<String, IConfigField<?>> FIELD_MAP = new HashMap<>();
    
    /** True if this config spec has any synced fields defined. */
    private boolean synced;
    
    /**
     * This is set to true once the config is ready for use.
     * Used to assist in keeping everything straight during the multithreaded initialization mess.
     */
    private volatile boolean initialized;
    
    /** True while this config spec is currently writing. */
    volatile boolean writing;
    
    /**
     * NOTE: Do NOT call this constructor. It is called automatically by the config file constructor.
     * <p>
     * Creates a new config spec at a specified location with only the basic 'start of file' action.
     */
    @ApiStatus.Internal
    public CrustConfigSpec( ConfigManager cfgManager, AbstractConfigFile cfgFile, String cfgName, boolean clientOnly ) {
        MANAGER = cfgManager;
        FILE = cfgFile;
        NAME = cfgName;
        CLIENT_ONLY = clientOnly;
        
        File file = new File( cfgManager.DIR, cfgName + CrustConfigFormat.FILE_EXT );
        File dir = file.getParentFile();
        
        // Make sure the directory exists
        if( !dir.exists() && !dir.mkdirs() ) {
            ConfigUtil.LOG.error( "Failed to make config folder! Things will likely explode. " +
                    "Create the folder manually to avoid this problem in the future: {}", dir );
        }
        
        // Create the underlying NightConfig object
        NIGHT_CONFIG_FILE = FileConfig.builder( file, new CrustConfigFormat( this ) ).sync().build();
        
        // Make sure the file exists (an empty file is all we need at this point)
        if( !NIGHT_CONFIG_FILE.getFile().exists() ) {
            ConfigUtil.LOG.info( "Generating default config file {}", getFilePath() );
            try {
                if( !NIGHT_CONFIG_FILE.getFile().createNewFile() ) {
                    ConfigUtil.LOG.error( "Failed to make blank config file! Things will likely explode. " +
                            "Create the file manually to avoid this problem in the future: {}", NIGHT_CONFIG_FILE.getFile() );
                }
            }
            catch( IOException ex ) {
                ConfigUtil.LOG.error( "Caught exception while generating blank config file! Things will likely explode. " +
                        "Create the file manually to avoid this problem in the future: {}", NIGHT_CONFIG_FILE.getFile(), ex );
            }
        }
    }
    
    /** Called when a change to the config file is detected. */
    private void onFileChanged() {
        if( writing ) {
            ConfigUtil.LOG.debug( "Skipping config file reload (it is currently saving) {}", getFilePath() );
        }
        else if( MANAGER.freezeFileWatcher ) {
            ConfigUtil.LOG.debug( "Skipping config file reload (mod's file watcher paused) {}", getFilePath() );
        }
        else if( !ConfigManager.GLOBAL_FREEZE_FILE_WATCHERS ) {
            ConfigUtil.LOG.info( "Reloading config file {}", getFilePath() );
            try {
                NIGHT_CONFIG_FILE.load();
            }
            catch( ParsingException ex ) {
                ConfigUtil.LOG.error( "Failed to reload config file {}", getFilePath(), ex );
            }
        }
    }
    
    /** Saves this config to file. */
    private void save() {
        try {
            NIGHT_CONFIG_FILE.save();
        }
        catch( WritingException ex ) {
            ConfigUtil.LOG.error( "Failed to save config file {}", getFilePath(), ex );
        }
    }
    
    /** INTERNAL METHOD. The underlying Night Config. */
    @ApiStatus.Internal
    public FileConfig getNightConfig() { return NIGHT_CONFIG_FILE; }
    
    /** INTERNAL METHOD. Called after the config is loaded to update cached values. */
    @ApiStatus.Internal
    public void onLoad() {
        // Perform load actions
        boolean rewrite = false;
        for( ISpecAction action : ACTIONS ) {
            if( action.onLoad() ) rewrite = true;
        }
        MinecraftForge.EVENT_BUS.post( new CrustConfigEvent.File.Loaded( FILE, rewrite ) );
        // Only rewrite and sync if one of the load actions requests it
        if( rewrite ) {
            save();
            ConfigManager.SYNC_SPEC_CONSUMER.accept( this );
        }
    }
    
    /** INTERNAL METHOD. Writes the current state of the config to file. */
    @ApiStatus.Internal
    public void write( CrustTomlWriter writer, CharacterOutput output ) {
        ACTIONS.forEach( action -> action.write( writer, output ) );
        MinecraftForge.EVENT_BUS.post( new CrustConfigEvent.File.Saved( FILE ) );
    }
    
    /** INTERNAL METHOD. Builds a value list for later serialization. */
    @ApiStatus.Internal
    public List<Object> buildValueList( boolean remote, boolean forSync ) {
        List<Object> values = new ArrayList<>();
        ACTIONS.forEach( action -> {
            if( action instanceof FieldAction<?> fieldAction && (!forSync || fieldAction.SYNC) )
                values.add( remote ? fieldAction.FIELD.getRemoteValue() : fieldAction.FIELD.getLocalValue() );
        } );
        return values;
    }
    
    /** INTERNAL METHOD. Serializes the config spec identifier to buffer. */
    @ApiStatus.Internal
    public void writeSpec( FriendlyByteBuf buffer ) {
        buffer.writeUtf( MANAGER.MOD_ID, 64 );
        buffer.writeUtf( NAME );
    }
    
    /** INTERNAL METHOD. Serializes the config spec identifier and value list to buffer. */
    @ApiStatus.Internal
    public void serialize( List<Object> values, FriendlyByteBuf buffer, boolean forSync ) {
        writeSpec( buffer );
        Iterator<Object> iterator = values.iterator();
        ACTIONS.forEach( action -> {
            if( action instanceof FieldAction<?> fieldAction && (!forSync || fieldAction.SYNC) )
                fieldAction.serialize( iterator, buffer );
        } );
    }
    
    /** INTERNAL METHOD. Deserializes a config spec identifier from buffer. */
    @ApiStatus.Internal
    public static CrustConfigSpec readSpec( FriendlyByteBuf buffer ) {
        return ConfigManager.getRequiredConfig(
                buffer.readUtf( 64 ),
                buffer.readUtf() ).SPEC;
    }
    
    /** INTERNAL METHOD. Deserializes a value list from buffer. */
    @ApiStatus.Internal
    public List<Object> deserialize( FriendlyByteBuf buffer, boolean forSync ) {
        List<Object> values = new ArrayList<>();
        ACTIONS.forEach( action -> {
            if( action instanceof FieldAction<?> fieldAction && (!forSync || fieldAction.SYNC) )
                fieldAction.deserialize( values, buffer );
        } );
        return values;
    }
    
    /** INTERNAL METHOD. Applies a deserialized remote value list. */
    @ApiStatus.Internal
    public void applyValueListRemote( List<Object> values, boolean forSync ) {
        Iterator<Object> iterator = values.iterator();
        ACTIONS.forEach( action -> {
            if( action instanceof FieldAction<?> fieldAction ) {
                if( !forSync || fieldAction.SYNC )
                    fieldAction.applyRemote( iterator, forSync );
            }
            else if( forSync ) action.onLoad();
        } );
    }
    
    /** INTERNAL METHOD. Applies a deserialized local value list. */
    @ApiStatus.Internal
    public void applyValueListLocal( List<Object> values ) {
        Iterator<Object> iterator = values.iterator();
        ACTIONS.forEach( action -> {
            if( action instanceof FieldAction<?> fieldAction )
                fieldAction.applyLocal( iterator );
        } );
    }
    
    /** INTERNAL METHOD. Clears all remote config data. Called when closing the in-game editor to clear up memory. */
    @ApiStatus.Internal
    public void clearRemoteData() {
        FIELD_MAP.forEach( ( key, field ) -> field.setRemoteValue( null ) );
    }
    
    /** INTERNAL METHOD. Clears all sync data. Called when leaving a server to reset to local values. */
    @ApiStatus.Internal
    public void clearSyncData() {
        FIELD_MAP.forEach( ( key, field ) -> field.setSyncValue( null ) );
    }
    
    /** INTERNAL METHOD. Builds the config editor widget for the config. */
    @ApiStatus.Internal
    public void initGui( ICrustConfigGuiSpec guiSpec ) {
        ACTIONS.forEach( action -> action.initGui( guiSpec ) );
    }
}