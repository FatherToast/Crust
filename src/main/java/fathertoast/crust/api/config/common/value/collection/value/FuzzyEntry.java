package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.key.DefaultKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKeyWrapper;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents a key-value pair used for fuzzy maps.
 * <p>
 * It functions by wrapping a fuzzy key and associating it with a value.
 *
 * @param <T> The type to match against.
 * @param <V> The value type.
 */
public class FuzzyEntry<T, V> extends FuzzyKeyWrapper<T> implements Supplier<V> {
    
    /** Creates an entry that associates a non-blacklist key with a value. */
    public static <T, V> FuzzyEntry<T, V> of( FuzzyKey<T> key, V value, IValueCodec<V> codec ) {
        return new FuzzyEntry<>( key, value, codec );
    }
    
    /** Creates an entry that defines a default value for the map. */
    public static <T, V> FuzzyEntry<T, V> ofDefault( V value, IValueCodec<V> codec ) {
        return of( DefaultKey.get(), value, codec );
    }
    
    /** Creates a blacklist type entry. */
    public static <T, V> FuzzyEntry<T, V> ofBlacklist( FuzzyKey<T> key ) {
        return new FuzzyEntry<>( key, null, null );
    }
    
    /**
     * Loads a fuzzy entry from the provided TOML string. Expects the pattern "key value".<p>
     * If the value is "exclude", the entry is declared a blacklist type.<p>
     * If the value is not included, it is set to the codec's default value.<p>
     * If the key is "default" and also declared a blacklist type, the line is deleted.
     *
     * @param parser The key parser to use.
     * @param codec  The value codec to use.
     * @param field  The config field we are loading for, or null if error reporting should be suppressed.
     * @param line   The full TOML string.
     * @return A new fuzzy entry based on the provided line, or null if the line should be deleted.
     */
    @Nullable
    public static <T, V> FuzzyEntry<T, V> parseLine( IFuzzyKeyParser<T> parser, IValueCodec<V> codec,
                                                     @Nullable IConfigField<?> field, String line ) {
        // Check for blacklist declaration and parse the value
        String[] keyAndValue = FuzzyKey.getKeyAndValue( line );
        String key = keyAndValue[0];
        boolean isDefault = key.equalsIgnoreCase( DEFAULT_KEY );
        boolean isBlacklist;
        V value;
        if( keyAndValue.length > 1 ) {
            if( keyAndValue[1].equalsIgnoreCase( BLACKLIST_VALUE ) ) {
                if( isDefault ) {
                    if( field != null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Default keys cannot be blacklist type! Deleting." );
                    }
                    return null;
                }
                isBlacklist = true;
                value = null;
            }
            else {
                isBlacklist = false;
                value = codec.parseTomlString( field, line, keyAndValue[1].trim() );
            }
        }
        else {
            isBlacklist = false;
            value = codec.getDefaultValue();
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Key-value pair must include a value! Assigning fallback value of {}. Entry: {}",
                        value, line );
            }
        }
        
        // Finally, parse the key
        return isDefault ? ofDefault( value, codec ) :
                isBlacklist ? ofBlacklist( parser.parseKeyStringNonNull( field, line, key, true ) ) :
                        of( parser.parseKeyStringNonNull( field, line, key, false ), value, codec );
    }
    
    
    /** @see #get() */
    @Nullable // Only null for blacklist keys
    private final V value;
    /** @see #getCodec() */
    @Nullable // Only null for blacklist keys
    private final IValueCodec<V> valueCodec;
    
    /** Constructs a key from the loaded string definition. */
    protected FuzzyEntry( FuzzyKey<T> k, @Nullable V v, @Nullable IValueCodec<V> codec ) {
        super( k );
        value = v;
        valueCodec = codec;
        
        // Perform some validation
        if( k.isBlacklist() ) {
            if( v != null ) {
                throw new IllegalArgumentException( "Blacklist key cannot map to value!" );
            }
        }
        else if( !k.isNull() && v == null ) {
            throw new IllegalArgumentException( "Non-null, non-blacklist key must map to a value!" );
        }
    }
    
    /**
     * @return This entry's associated value.
     * @throws NullPointerException If this is a blacklist key.
     */
    @Override // Supplier
    public V get() { return Objects.requireNonNull( value ); }
    
    /**
     * @return This entry's value codec.
     * @throws NullPointerException If this is a blacklist key.
     */
    public IValueCodec<V> getCodec() { return Objects.requireNonNull( valueCodec ); }
    
    
    /** @return This value, converted to a single-line string. */
    @Override // ITomlStringValue
    public String toTomlString() {
        return keyWithValue( keyString(), isBlacklist() ? BLACKLIST_VALUE : getCodec().toTomlString( get() ) );
    }
}