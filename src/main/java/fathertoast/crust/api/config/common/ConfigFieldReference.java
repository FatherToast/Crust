package fathertoast.crust.api.config.common;

import com.mojang.serialization.Codec;
import fathertoast.crust.api.config.common.field.IConfigField;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * This can be used to hold a reference to a config field and getting read access to
 * it via its field address. Primarily used for data pack value providers.
 */
public class ConfigFieldReference<V> implements Supplier<V> {
    
    public static final Codec<ConfigFieldReference<Boolean>> BOOLEAN_CODEC = makeCodec();
    public static final Codec<ConfigFieldReference<Integer>> INT_CODEC = makeCodec();
    public static final Codec<ConfigFieldReference<Double>> DOUBLE_CODEC = makeCodec();
    
    
    /** The ID of the mod that owns the target field's config. */
    private final String modId;
    /** The file/spec name of the config containing the target field. */
    private final String file;
    /** The key of the target field. */
    private final String key;
    
    /**
     * The actual target field, if it has been located.
     * This is assigned via {@link #check()}.
     */
    @Nullable
    private IConfigField<V> setField;
    
    
    /**
     * Constructs a new instance using the given field.
     *
     * @param field The field that should be referenced by this field reference
     */
    public ConfigFieldReference( IConfigField<V> field ) {
        this.modId = field.getSpec().MANAGER.MOD_ID;
        this.file = field.getSpec().NAME;
        this.key = field.getKey();
        this.setField = field;
    }
    
    /**
     * Constructs a new instance from the given field address.
     * Logs an error if the address is not correctly formatted.
     *
     * @param address The full address of this reference's config field, which must contain
     *                the target field's config's associated mod ID, as well as its spec name and key.
     * @see #toString()
     */
    public ConfigFieldReference( String address ) {
        final String[] parts = address.split( ":", 3 );
        if( parts.length != 3 ) {
            ConfigUtil.LOG.error( "Invalid config field address: '{}'", address );
        }
        this.modId = parts.length > 0 ? parts[0] : "";
        this.file = parts.length > 1 ? parts[1] : "";
        this.key = parts.length > 2 ? parts[2] : "";
    }
    
    
    /** @return The config field value; prints an error and returns the error value if it fails. */
    public V getOrElse( V errorValue ) {
        if( !check() ) {
            ConfigUtil.LOG.error( "Invalid boolean field: {}", this );
            return errorValue;
        }
        // noinspection ConstantConditions
        return setField.get();
    }
    
    /** @return The config field value; returns null if it fails. */
    @Nullable
    @Override // Supplier
    public V get() {
        // noinspection ConstantConditions
        return check() ? setField.get() : null;
    }
    
    /** @return True if the set field reference has been found. */
    private boolean check() {
        if( setField != null ) return true;
        IConfigField<?> foundField = getField( modId, file, key );
        if( foundField != null ) {
            // Try to cast the field so ensure the value type is correct
            try {
                // noinspection unchecked
                setField = (IConfigField<V>) foundField;
                return true;
            }
            catch( ClassCastException ignored ) { }
        }
        // Field was either not found or not the correct type
        return false;
    }
    
    @Override
    public String toString() { return modId + ":" + file + ":" + key; }
    
    
    /**
     * Attempts to find and return a config field that corresponds to the given field properties.
     *
     * @param modId    The ID of the mod that owns the config file of the field to look for.
     * @param specName The name of the config spec to look in.
     * @param fieldKey The key of the field to look for.
     * @return The config field associated with the given parameters, or null if no field was found.
     */
    @Nullable
    protected static IConfigField<?> getField( String modId, String specName, String fieldKey ) {
        return ConfigManager.getRequiredConfig( modId, specName ).SPEC.getFields().get( fieldKey );
    }
    
    /** Convenience method for creating a field reference codec with the inferred type. */
    public static <V> Codec<ConfigFieldReference<V>> makeCodec() {
        return Codec.STRING.xmap( ConfigFieldReference::new, ConfigFieldReference::toString );
    }
}
