package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import javax.annotation.Nullable;
import java.util.Objects;

@Deprecated( forRemoval = true )
public class RegistryValueTagEntry<T> {
    
    /** The field containing this entry. We save a reference to help improve error/warning reports. */
    private final AbstractConfigField FIELD;
    
    /** The entity type tag this entry holds. */
    public final TagKey<T> TAG;
    
    /** The values given to this entry. Null for comparison objects. */
    public final double[] VALUES;
    
    
    /** Creates an entry with the specified values. Used for creating default configs. */
    public RegistryValueTagEntry( ResourceKey<Registry<T>> registryKey, ResourceLocation tagLocation, double... values ) {
        this( null, new TagKey<>( registryKey, tagLocation ), values );
    }
    
    /** Creates an entry with the specified values. Used for creating default configs. */
    public RegistryValueTagEntry( TagKey<T> tagKey, double... values ) {
        this( null, tagKey, values );
    }
    
    /** Creates an entry with the specified values. */
    public RegistryValueTagEntry( @Nullable AbstractConfigField field, TagKey<T> tagKey, double... values ) {
        Objects.requireNonNull( tagKey );
        
        FIELD = field;
        TAG = tagKey;
        VALUES = values;
    }
    
    /**
     * @return The string representation of this registry value tag entry, as it would appear in a config file.
     * <p>
     * Format is "#namespace:path value0 value1 ...".
     */
    @Override
    public String toString() {
        // Start with the tag
        StringBuilder str = new StringBuilder( ConfigUtil.toString( TAG ) );
        
        // Append values array
        if( VALUES != null ) {
            for( double value : VALUES ) {
                str.append( ' ' ).append( value );
            }
        }
        return str.toString();
    }
}
