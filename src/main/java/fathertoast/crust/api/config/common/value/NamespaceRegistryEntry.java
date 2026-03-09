package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Registry entry that covers all IDs under a given mod namespace.
 */

@Deprecated( forRemoval = true )
public class NamespaceRegistryEntry {
    
    /** The field containing this entry. We save a reference to help improve error/warning reports. */
    private final AbstractConfigField FIELD;
    
    /** The namespace covered by this entry. */
    public final String NAMESPACE;
    
    /** The values given to this entry. Null for comparison objects. */
    public final double[] VALUES;
    
    
    /** Creates an entry with the specified values. Used for creating default configs. */
    public NamespaceRegistryEntry( String namespace, double... values ) {
        this( null, namespace, values );
    }
    
    /** Creates an entry with the specified values. */
    public NamespaceRegistryEntry( @Nullable AbstractConfigField field, String namespace, double... values ) {
        Objects.requireNonNull( namespace );
        
        FIELD = field;
        NAMESPACE = namespace;
        VALUES = values;
    }
    
    /** @return True if nothing is wrong with this entry. Currently unused. */
    private boolean validate() {
        // No real validation needed.
        // Could potentially check if namespace exists, but no big deal.
        return true;
    }
    
    /**
     * @return Returns true if the given entry's registry key matches the namespace of this namespace-entry.
     */
    public boolean contains( String namespace ) {
        if( !validate() ) return false;
        
        return namespace.equals( NAMESPACE );
    }
    
    /**
     * @return The string representation of this namespace registry entry, as it would appear in a config file.
     * <p>
     * Format is "namespace:* value0 value1 ...".
     */
    @Override
    public String toString() {
        // Start with the namespace wildcard string
        StringBuilder str = new StringBuilder( ConfigUtil.namespaceWildcard( NAMESPACE ) );
        
        // Append values array
        if( VALUES != null ) {
            for( double value : VALUES ) {
                str.append( ' ' ).append( value );
            }
        }
        return str.toString();
    }
}
