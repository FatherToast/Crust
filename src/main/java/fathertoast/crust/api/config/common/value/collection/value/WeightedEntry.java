package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;
import fathertoast.crust.api.config.common.value.collection.key.NullKey;
import fathertoast.crust.api.config.common.value.collection.key.WeightedKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a weighted key-value pair used for fuzzy maps.
 * <p>
 * It functions by wrapping a fuzzy key and associating it with a weight and value.
 * <p>
 * Intended only for polling usage ({@link fathertoast.crust.api.config.common.value.collection.KeyUsage#POLL}).
 *
 * @param <T> The type to match against.
 * @param <V> The value type.
 */
@ApiStatus.Experimental
public class WeightedEntry<T, V> extends FuzzyEntry<T, V> {
    
    /** Creates a weighted entry that represents a chance to pick a particular key-value pair. */
    public static <T, V> WeightedEntry<T, V> of( int weight, FuzzyKey<T> key, V value, IValueCodec<V> codec ) {
        return new WeightedEntry<>( weight, key, value, codec );
    }
    
    /** Creates a weighted entry that represents a chance to pick nothing. */
    public static <T, V> WeightedEntry<T, V> ofNull( int weight ) {
        return new WeightedEntry<>( weight, NullKey.ofValue(), null, null );
    }
    
    /**
     * Loads a weighted entry from the provided TOML string. Expects the pattern "weight key value".<p>
     * If the weight is not included, it is given 0 weight.<p>
     * If the key is not included, it will try to parse the weight as a key... which probably won't work too well.<p>
     * If the value is not included, it is set to the codec's default value.<p>
     * If the key is "default" or declared a blacklist type, it is converted to a null key instead.
     *
     * @param parser The key parser to use.
     * @param codec  The value codec to use.
     * @param field  The config field we are loading for, or null if error reporting should be suppressed.
     * @param line   The full TOML string.
     * @return A new weighted entry based on the provided line.
     */
    public static <T, V> WeightedEntry<T, V> parseLine( IFuzzyKeyParser<T> parser, IValueCodec<V> codec,
                                                        @Nullable AbstractConfigField field, String line ) {
        // Parse the key/value from the weight (pattern: "weight key value"); assume that if one is missing, it's the weight
        String[] keyAndWeight = FuzzyKey.getKeyAndValue( line );
        int weight;
        String keyWithValue;
        if( keyAndWeight.length < 2 ) {
            weight = IntValueCodec.NON_NEGATIVE.parseTomlString( field, line, null );
            keyWithValue = keyAndWeight[0];
        }
        else {
            weight = IntValueCodec.NON_NEGATIVE.parseTomlString( field, line, keyAndWeight[0] );
            keyWithValue = keyAndWeight[1];
        }
        
        // Parse the value and delete blacklist keyword, if needed
        String[] keyAndValue = FuzzyKey.getKeyAndValue( keyWithValue );
        String key = keyAndValue[0];
        if( key.equalsIgnoreCase( DEFAULT_KEY ) ) {
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Weighted keys cannot be default keys! Converting to null key. Entry: {}", line );
            }
            return ofNull( weight );
        }
        V value;
        if( keyAndValue.length > 1 ) {
            if( keyAndValue[1].equalsIgnoreCase( BLACKLIST_VALUE ) ) {
                if( field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Weighted entries cannot be blacklist type! Converting to null key. Entry: {}",
                            line );
                }
                return ofNull( weight );
            }
            value = codec.parseTomlString( field, line, keyAndValue[1].trim() );
        }
        else {
            value = codec.parseTomlString( field, line, null );
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Key-value pair must include a value! Assigning fallback value of {}. Entry: {}",
                        value, line );
            }
        }
        
        // Finally, parse the key
        return key.equalsIgnoreCase( DEFAULT_KEY ) ? ofNull( weight ) :
                of( weight, parser.parseKeyStringNonNull( field, line, key, false ), value, codec );
    }
    
    
    private final int weight;
    
    /** Constructs a key from the loaded string definition. */
    protected WeightedEntry( int w, FuzzyKey<T> k, @Nullable V v, @Nullable IValueCodec<V> codec ) {
        super( k, v, codec );
        weight = Math.max( w, 0 );
        
        // Perform some validation
        if( k.isBlacklist() ) {
            throw new IllegalArgumentException( "Weighted keys cannot be blacklist type!" );
        }
    }
    
    /** @return This entry's weight. Always non-negative. */
    public int getWeight() { return weight; }
    
    /**
     * @return This entry's associated value.
     * @throws NullPointerException If this is a "proper" null entry.
     */
    @Override
    public V get() { return super.get(); } // Just overridden to change the javadoc
    
    /**
     * @return This entry's value codec.
     * @throws NullPointerException If this is a "proper" null entry.
     */
    @Override
    public IValueCodec<V> getCodec() { return super.getCodec(); } // Just overridden to change the javadoc
    
    
    /**
     * @return True if this key is a blacklist type; in other words, when this is the best match,
     * the containing set/map should treat it as if no match was found.
     */
    @Override
    public boolean isBlacklist() { return false; }
    
    /** @return This value, converted to a single-line string. */
    @Override // ITomlStringValue
    public String toTomlString() {
        String keyWithWeight = WeightedKey.keyWithWeight( getWeight(), keyString() );
        try {
            return keyWithValue( keyWithWeight, getCodec().toTomlString( get() ) );
        }
        catch( NullPointerException ignored ) {} // Means this is a "proper" null entry
        return keyWithWeight;
    }
}