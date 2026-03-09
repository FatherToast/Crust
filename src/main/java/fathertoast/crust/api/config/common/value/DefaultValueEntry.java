package fathertoast.crust.api.config.common.value;

/**
 * An entry containing only values to be used for defaults in
 * various config lists, such as entity lists or registry entry-value lists.
 */

@Deprecated( forRemoval = true )
public class DefaultValueEntry {
    
    public static final String KEY_DEFAULT = "default";
    
    /** The values given to this entry. */
    public final double[] VALUES;
    
    /** Creates an entry with the specified values. */
    public DefaultValueEntry( double... values ) {
        if( values.length < 1 ) {
            // Default to a single value of 0.0
            VALUES = new double[] { 0.0 };
        }
        else {
            VALUES = values;
        }
    }
    
    /**
     * @return The string representation of this default entry, as it would appear in a config file.
     * <p>
     * Format is "default value0 value1 ...".
     */
    @Override
    public String toString() {
        // Start with the wildcard string
        StringBuilder str = new StringBuilder( KEY_DEFAULT );
        
        // Append values array
        for( double value : VALUES ) {
            str.append( ' ' ).append( value );
        }
        return str.toString();
    }
}
