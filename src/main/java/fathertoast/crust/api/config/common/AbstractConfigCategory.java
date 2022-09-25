package fathertoast.crust.api.config.common;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.TomlHelper;

/**
 * Represents one category within a config file. Create these within your config file implementations'
 * constructors to group similar fields.
 * <p>
 * It is a good practice to have this config category referenced in a public final field within the
 * config file implementation to simplify access. Similarly, your config category implementations should
 * have public final references to all their fields.
 * <p>
 * This uses the config file's spec to continue building the file format in exactly the same way;
 * see AbstractConfigFile for more explanation on config implementation.
 *
 * @see AbstractConfigFile
 */
public abstract class AbstractConfigCategory<T extends AbstractConfigFile> {
    
    /** The config file containing this category. */
    protected final T PARENT;
    
    /** The spec used by this config that defines the file's format. */
    protected final CrustConfigSpec SPEC;
    
    /**
     * @param parent              The config file containing this category.
     * @param categoryName        Name for the new category. Will be prefixed before all field keys defined within.
     * @param categoryDescription Comment to describe/summarize the contents of the category.
     *                            Each string is printed as a separate line.
     */
    public AbstractConfigCategory( T parent, String categoryName, String... categoryDescription ) {
        PARENT = parent;
        SPEC = parent.SPEC;
        
        parent.SPEC.loadingCategory = categoryName + ".";
        SPEC.category( categoryName, TomlHelper.newComment( categoryDescription ) );
    }
}