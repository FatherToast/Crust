package fathertoast.crust.api.config.common.file;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.CharacterOutput;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingException;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.*;

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
    
    /** The base key prefix to use for all fields, based on the currently loading config category. */
    public String loadingCategory;
    
    /** @return The file this config spec loads to/from. */
    public File getFile() { return NIGHT_CONFIG_FILE.getFile(); }
    
    /** @return A read-only map of all keys defined in this spec to their config fields. */
    public Map<String, AbstractConfigField> getFields() { return Collections.unmodifiableMap( FIELD_MAP ); }
    
    /** @return True if the config is initialized, and therefore safe to use (though specific field types may still be unsafe). */
    public boolean isInitialized() { return initialized; }
    
    /**
     * Performs first-time loading of the config from disk and registers it in the auto-reload system.
     * <p>
     * You must call this method when you want the initialization to occur. This method immediately loads, so the config file's values will be immediately ready to use
     */
    public void initialize() {
        ConfigUtil.LOG.info( "First-time loading config file {}", ConfigUtil.toRelativePath( NIGHT_CONFIG_FILE ) );
        try {
            NIGHT_CONFIG_FILE.load();
        }
        catch( ParsingException ex ) {
            ConfigUtil.LOG.error( "Failed first-time loading of config file {} - this is bad!",
                    ConfigUtil.toRelativePath( NIGHT_CONFIG_FILE ), ex );
        }
        
        try {
            FileWatcher.defaultInstance().addWatch( NIGHT_CONFIG_FILE.getFile(), this::onFileChanged );
            ConfigUtil.LOG.info( "Started watching config file {} for updates", ConfigUtil.toRelativePath( NIGHT_CONFIG_FILE ) );
        }
        catch( IOException ex ) {
            ConfigUtil.LOG.error( "Failed to watch config file {} - this file will NOT update in-game until restarted!",
                    ConfigUtil.toRelativePath( NIGHT_CONFIG_FILE ), ex );
        }
        
        initialized = true;
    }
    
    
    // ---- Spec Building Methods ---- //
    
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
    public <T extends AbstractConfigField> T define( T field ) { return define( field, null ); }
    
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
    public <T extends AbstractConfigField> T define( T field, @Nullable RestartNote restartNote ) {
        ACTIONS.add( new Field( this, field, restartNote ) );
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
    public void callback( Runnable callback ) { ACTIONS.add( new ReadCallback( callback ) ); }
    
    
    /** Inserts a single new line. */
    public void newLine() { newLine( 1 ); }
    
    /** @param count The number of new lines to insert. */
    public void newLine( int count ) { ACTIONS.add( new NewLines( count ) ); }
    
    
    /** Increases the indent by one level. */
    public void increaseIndent() { indent( +1 ); }
    
    /** Decreases the indent by one level. */
    public void decreaseIndent() { indent( -1 ); }
    
    /** @param count The amount to change the indent by. */
    public void indent( int count ) { ACTIONS.add( new Indent( count ) ); }
    
    
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
    public void comment( List<String> comment ) { ACTIONS.add( new Comment( comment ) ); }
    
    
    /**
     * Adds a subcategory header, optionally including a comment to describe/summarize the contents of the section.
     * <p>
     * The header and its comment are printed at the current indent level - 1. Therefore, it is good practice to always
     * increase the indent before the first subcategory and then decrease the indent after the final subcategory.
     *
     * @param name    The subcategory name.
     * @param comment The subcategory comment to insert.
     */
    public void subcategory( String name, String... comment ) { ACTIONS.add( new Subcategory( name, TomlHelper.newComment( comment ) ) ); }
    
    /**
     * Adds a header to signal the start of the appendix section, optionally including a comment to describe/summarize the section.
     *
     * @param comment The appendix comment to insert.
     */
    public void appendixHeader( String... comment ) { ACTIONS.add( new AppendixHeader( TomlHelper.newComment( comment ) ) ); }
    
    
    /**
     * Inserts a detailed description of how to use the registry entry list field.
     * Recommended to include either in a README or at the start of each config that contains any registry entry list fields.
     */
    public void describeRegistryEntryList() { ACTIONS.add( new Comment( RegistryEntryListField.verboseDescription() ) ); }
    
    /**
     * Inserts a detailed description of how to use the entity list field.
     * Recommended to include either in a README or at the start of each config that contains any entity list fields.
     */
    public void describeEntityList() { ACTIONS.add( new Comment( EntityListField.verboseDescription() ) ); }
    
    /**
     * Inserts a detailed description of how to use the attribute list field.
     * Recommended to include either in a README or at the start of each config that contains any attribute list fields.
     */
    public void describeAttributeList() { ACTIONS.add( new Comment( AttributeListField.verboseDescription() ) ); }
    
    /**
     * Inserts a detailed description of how to use the block list field.
     * Recommended to include either in a README or at the start of each config that contains any block list fields.
     */
    public void describeBlockList() { ACTIONS.add( new Comment( BlockListField.verboseDescription() ) ); }
    
    /**
     * Inserts the first part of a detailed description of how to use the environment list field.
     * Should go with the other field descriptions.
     */
    public void describeEnvironmentListPart1of2() { ACTIONS.add( new Comment( EnvironmentListField.verboseDescription() ) ); }
    
    /**
     * Inserts the second and last part of a detailed description of how to use the environment list field.
     * Should go at the bottom of the file, preferably after the appendix header (if used).
     */
    public void describeEnvironmentListPart2of2() { ACTIONS.add( new Comment( EnvironmentListField.environmentDescriptions() ) ); }
    
    
    // ---- Internal Methods ---- //
    
    /**
     * NOTE: You should never need to call this method. It is called automatically in the config file constructor.
     * <p>
     * Adds a config header with a comment to describe/summarize the contents of the file.
     *
     * @param comment The file comment to insert.
     */
    public void header( List<String> comment ) { ACTIONS.add( new Header( this, comment ) ); }
    
    /**
     * NOTE: You should never need to call this method. It is called automatically in the config category constructor.
     * <p>
     * Adds a category header with a comment to describe/summarize the contents of the category section.
     *
     * @param name    The category name.
     * @param comment The category comment to insert.
     */
    public void category( String name, List<String> comment ) { ACTIONS.add( new Category( name, comment ) ); }
    
    
    /** The underlying NightConfig config. */
    private final FileConfig NIGHT_CONFIG_FILE;
    
    /** The list of actions to perform, in a specific order, when reading or writing the config file. */
    private final List<Action> ACTIONS = new ArrayList<>();
    /** The fields defined in this spec. */
    private final Map<String, AbstractConfigField> FIELD_MAP = new HashMap<>();
    
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
    public CrustConfigSpec( ConfigManager cfgManager, AbstractConfigFile cfgFile, String cfgName ) {
        MANAGER = cfgManager;
        FILE = cfgFile;
        NAME = cfgName;
        
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
            ConfigUtil.LOG.info( "Generating default config file {}", ConfigUtil.toRelativePath( NIGHT_CONFIG_FILE ) );
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
            ConfigUtil.LOG.debug( "Skipping config file reload (it is currently saving) {}", ConfigUtil.toRelativePath( NIGHT_CONFIG_FILE ) );
        }
        else if( MANAGER.freezeFileWatcher ) {
            ConfigUtil.LOG.debug( "Skipping config file reload (file watcher paused) {}", ConfigUtil.toRelativePath( NIGHT_CONFIG_FILE ) );
        }
        else {
            ConfigUtil.LOG.info( "Reloading config file {}", ConfigUtil.toRelativePath( NIGHT_CONFIG_FILE ) );
            try {
                NIGHT_CONFIG_FILE.load();
            }
            catch( ParsingException ex ) {
                ConfigUtil.LOG.error( "Failed to reload config file {}", ConfigUtil.toRelativePath( NIGHT_CONFIG_FILE ), ex );
            }
        }
    }
    
    /** Saves this config to file. */
    private void save() {
        try {
            NIGHT_CONFIG_FILE.save();
        }
        catch( WritingException ex ) {
            ConfigUtil.LOG.error( "Failed to save config file {}", ConfigUtil.toRelativePath( NIGHT_CONFIG_FILE ), ex );
        }
    }
    
    /** INTERNAL METHOD. Called after the config is loaded to update cached values. */
    public void onLoad() {
        // Perform load actions
        boolean rewrite = false;
        for( Action action : ACTIONS ) {
            if( action.onLoad() ) rewrite = true;
        }
        // Only rewrite if one of the load actions requests it
        if( rewrite ) save();
    }
    
    /** INTERNAL METHOD. Writes the current state of the config to file. */
    public void write( CrustTomlWriter writer, CharacterOutput output ) {
        for( Action action : ACTIONS ) { action.write( writer, output ); }
    }
    
    
    // ---- Action Implementations ---- //
    
    /** Represents a single action performed by the spec when reading or writing the config file. */
    private interface Action {
        
        /** Called when the config is loaded. */
        boolean onLoad();
        
        /** Called when the config is saved. */
        void write( CrustTomlWriter writer, CharacterOutput output );
    }
    
    /** Represents a write-only spec action. */
    private static abstract class Format implements Action {
        
        /** Called when the config is loaded. */
        @Override
        public final boolean onLoad() { return false; } // Formatting actions do not affect file reading
        
        /** Called when the config is saved. */
        @Override
        public abstract void write( CrustTomlWriter writer, CharacterOutput output );
    }
    
    /** Represents a variable number of new lines. */
    private static class NewLines extends Format {
        
        /** The number of new lines to write. */
        private final int COUNT;
        
        /** Create a new comment action that will insert a number of new lines. */
        private NewLines( int count ) { COUNT = count; }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            for( int i = 0; i < COUNT; i++ ) {
                writer.writeNewLine( output );
            }
        }
    }
    
    /** Represents a variable number of indent increases or decreases. */
    private static class Indent extends Format {
        
        /** The amount to change the indent by. */
        private final int AMOUNT;
        
        /** Create a new comment action that will insert a number of new lines. */
        private Indent( int amount ) { AMOUNT = amount; }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) { writer.changeIndentLevel( AMOUNT ); }
    }
    
    /** Represents a comment. */
    private static class Comment extends Format {
        
        /** The spec this action belongs to. */
        private final List<String> COMMENT;
        
        /** Create a new comment action that will insert a comment. */
        private Comment( List<String> comment ) { COMMENT = comment; }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) { writer.writeComment( COMMENT, output ); }
    }
    
    /** Represents a file header comment. */
    private static class Header extends Format {
        
        /** The spec this action belongs to. */
        private final CrustConfigSpec PARENT;
        /** The file comment. */
        private final List<String> COMMENT;
        
        /** Create a new header action that will insert the opening file comment. */
        private Header( CrustConfigSpec parent, List<String> comment ) {
            PARENT = parent;
            COMMENT = comment;
        }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            writer.writeComment( PARENT.MANAGER.MOD_ID + ":" + PARENT.NAME + CrustConfigFormat.FILE_EXT, output );
            writer.writeComment( COMMENT, output );
            
            writer.increaseIndentLevel();
        }
    }
    
    /** Represents an appendix header comment. */
    private static class AppendixHeader extends Format {
        
        /** The appendix comment. */
        private final List<String> COMMENT;
        
        /** Create a new appendix header action that will insert a closing file comment. */
        private AppendixHeader( List<String> comment ) { COMMENT = comment; }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            writer.decreaseIndentLevel();
            
            writer.writeNewLine( output );
            writer.writeNewLine( output );
            writer.writeComment( "Appendix:", output );
            writer.writeComment( COMMENT, output );
            
            writer.increaseIndentLevel();
        }
    }
    
    /** Represents a category comment. */
    private static class Category extends Format {
        
        /** The category comment. */
        private final List<String> COMMENT;
        
        /** Create a new category action that will insert the category comment. */
        private Category( String categoryName, List<String> comment ) {
            comment.add( 0, "Category: " + categoryName );
            COMMENT = comment;
        }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            writer.decreaseIndentLevel();
            
            writer.writeNewLine( output );
            writer.writeNewLine( output );
            writer.writeComment( COMMENT, output );
            
            writer.increaseIndentLevel();
            writer.writeNewLine( output );
        }
    }
    
    /** Represents a subcategory comment. */
    private static class Subcategory extends Format {
        
        /** The subcategory comment. */
        private final List<String> COMMENT;
        
        /** Create a new subcategory action that will insert the subcategory comment. */
        private Subcategory( String subcategoryName, List<String> comment ) {
            comment.add( 0, "Subcategory: " + subcategoryName );
            COMMENT = comment;
        }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            writer.decreaseIndentLevel();
            
            writer.writeNewLine( output );
            writer.writeComment( COMMENT, output );
            
            writer.increaseIndentLevel();
            writer.writeNewLine( output );
        }
    }
    
    /** Represents a read-only spec action. */
    private static class ReadCallback implements Action {
        
        /** The method to call on read. */
        private final Runnable CALLBACK;
        
        /** Create a new field action that will load/create and save the field value. */
        private ReadCallback( Runnable callback ) { CALLBACK = callback; }
        
        /** Called when the config is loaded. */
        @Override
        public boolean onLoad() {
            CALLBACK.run();
            return false;
        }
        
        /** Called when the config is saved. */
        @Override
        public final void write( CrustTomlWriter writer, CharacterOutput output ) { } // Read callback actions do not affect file writing
    }
    
    /** Represents a spec action that reads and writes to a field. */
    private static class Field implements Action {
        
        /** The spec this action belongs to. */
        private final CrustConfigSpec PARENT;
        /** The underlying config field to perform actions for. */
        private final AbstractConfigField FIELD;
        
        /** Create a new field action that will load/create and save the field value. */
        private Field( CrustConfigSpec parent, AbstractConfigField field, @Nullable RestartNote restartNote ) {
            PARENT = parent;
            FIELD = field;
            field.finalizeComment( parent, restartNote );
            
            if( parent.FIELD_MAP.containsKey( field.getKey() ) ) {
                throw new IllegalStateException( "Attempted to register duplicate field key '" + field.getKey() + "' in config " + parent.NAME );
            }
            parent.FIELD_MAP.put( FIELD.getKey(), FIELD );
        }
        
        /** Called when the config is loaded. */
        @Override
        public boolean onLoad() {
            // Get cached value to detect changes
            final Object oldRaw = FIELD.getRaw();
            
            // Fetch the newly loaded value
            final Object rawToml = PARENT.NIGHT_CONFIG_FILE.getOptional( FIELD.getKey() ).orElse( null );
            FIELD.load( rawToml );
            
            // Push the field's value back to the config if its value was changed
            final Object newRaw = FIELD.getRaw();
            if( rawToml == null || !Objects.equals( oldRaw, newRaw ) ) {
                PARENT.NIGHT_CONFIG_FILE.set( FIELD.getKey(), newRaw );
                return true;
            }
            return false;
        }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            // Write the key and value
            writer.writeField( FIELD, output );
        }
    }
}