package fathertoast.crust.api.config.common;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.TomlHelper;

/**
 * Represents one config file that contains a reference for each configurable value within and a specification
 * that defines the file's format. Before use, the config file must be initialized by calling its spec's
 * {@link CrustConfigSpec#initialize()} method.
 * <p>
 * For simple implementations, defining fields in the config file constructor is completely fine.
 * <p>
 * For most config files, you will primarily be instantiating config categories within your config file
 * constructor. Those config categories will then define the fields.
 * <p>
 * It is a good practice to have this config file referenced in a public static final field to simplify
 * access. Similarly, your config file implementations should have public final references to all their
 * categories and fields, and each category to its fields.
 * <p>
 * Crust implements its own configs. See {@link fathertoast.crust.common.config.CrustConfig} for example
 * config implementations.
 *
 * @see AbstractConfigCategory
 */
public abstract class AbstractConfigFile {
    
    /** The spec used by this config that defines the file's format. */
    public final CrustConfigSpec SPEC;
    
    /**
     * @param cfgManager      The mod's config manager.
     * @param cfgName         Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     * @param fileDescription Opening file comment to describe/summarize the contents of the file.
     *                        Each string is printed as a separate line.
     */
    public AbstractConfigFile( ConfigManager cfgManager, String cfgName, String... fileDescription ) {
        cfgManager.register( this );
        
        SPEC = new CrustConfigSpec( cfgManager, this, cfgName );
        SPEC.loadingCategory = "";
        SPEC.header( TomlHelper.newComment( fileDescription ) );
    }
}