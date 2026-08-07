package fathertoast.crust.api.config.common;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.event.config.CrustConfigEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Represents one config file that contains a reference for each configurable value within and a specification
 * that defines the file's format. Before use, the config file must be initialized by calling its spec's
 * {@link CrustConfigSpec#initialize()} method.
 * <p>
 * For simple implementations, defining fields in the config file constructor is completely fine; consider
 * extending {@link AbstractConfigFile.Simple} instead to automatically format it a little nicer.
 * <p>
 * For most config files, you will primarily be instantiating config categories within your config file
 * constructor. Those config categories will then define the fields in their constructors.
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
     * @param clientOnly      True if this config only exists on the client side.
     * @param fileDescription Opening file comment to describe/summarize the contents of the file.
     *                        Each string is printed as a separate line.
     */
    public AbstractConfigFile( ConfigManager cfgManager, String cfgName, boolean clientOnly, String... fileDescription ) {
        SPEC = new CrustConfigSpec( cfgManager, this, cfgName, clientOnly );
        cfgManager.register( this );
        SPEC.loadingCategory = "";
        SPEC.header( TomlHelper.newComment( fileDescription ) );
        MinecraftForge.EVENT_BUS.post( new CrustConfigEvent.File.Constructed( this ) );
    }
    
    /**
     * Extend this class instead of the base abstract config file class if you plan on doing a very simple
     * config file with no categories. Just immediately start defining your fields with SPEC#define() in
     * your constructor following the call to super.
     * <p>
     * See {@link fathertoast.crust.client.config.RenderSettingsCrustConfig} for a simple example config implementation.
     */
    public static abstract class Simple extends AbstractConfigFile {
        /**
         * @param cfgManager      The mod's config manager.
         * @param cfgName         Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
         * @param clientOnly      True if this config only exists on the client side.
         * @param fileDescription Opening file comment to describe/summarize the contents of the file.
         *                        Each string is printed as a separate line.
         */
        public Simple( ConfigManager cfgManager, String cfgName, boolean clientOnly, String... fileDescription ) {
            super( cfgManager, cfgName, clientOnly, fileDescription );
            SPEC.setupSimpleFile();
        }
    }
}