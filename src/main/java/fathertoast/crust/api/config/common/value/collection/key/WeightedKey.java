package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.value.IntValueCodec;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a weighted key used for weighted lists.
 * <p>
 * It functions by wrapping a fuzzy key and associating it with a weight.
 * <p>
 * Intended only for polling usage ({@link fathertoast.crust.api.config.common.value.collection.KeyUsage#POLL}).
 *
 * @param <T> The type to match against.
 */
@ApiStatus.Experimental
public class WeightedKey<T> extends FuzzyKey<T> {
    
    /** Creates a weighted key that represents a chance to pick a particular key. */
    public static <T> WeightedKey<T> of( int weight, FuzzyKey<T> key ) { return new WeightedKey<>( weight, key ); }
    
    /** Creates a weighted key that represents a chance to pick nothing. */
    public static <T> WeightedKey<T> ofNull( int weight ) { return of( weight, NullKey.ofValue() ); }
    
    /**
     * Loads a weighted key from the provided TOML string. Expects the pattern "weight key".<p>
     * If the weight is not included, it is given 0 weight.<p>
     * If the key is not included, it will try to parse the weight as a key... which probably won't work too well.<p>
     * If a value is included after the key, the value is deleted.<p>
     * If the key is "default" or declared a blacklist type, it is converted to a null key instead.
     *
     * @param parser The key parser to use.
     * @param field  The config field we are loading for, or null if error reporting should be suppressed.
     * @param line   The full TOML string.
     * @return A new weighted key based on the provided line.
     */
    public static <T> WeightedKey<T> parseLine( IFuzzyKeyParser<T> parser, @Nullable AbstractConfigField field, String line ) {
        // Parse the key/value from the weight (pattern: "weight key"); assume that if one is missing, it's the weight
        String[] keyAndWeight = FuzzyKey.getKeyAndValue( line );
        int weight;
        String keyWithValue;
        if( keyAndWeight.length < 2 ) {
            weight = IntValueCodec.WEIGHT.parseTomlString( field, line, null );
            keyWithValue = keyAndWeight[0];
        }
        else {
            weight = IntValueCodec.WEIGHT.parseTomlString( field, line, keyAndWeight[0] );
            keyWithValue = keyAndWeight[1];
        }
        
        // Delete value or blacklist keyword, if needed
        String[] keyAndValue = FuzzyKey.getKeyAndValue( keyWithValue );
        String key;
        if( keyAndValue.length > 1 ) {
            if( keyAndValue[1].equalsIgnoreCase( BLACKLIST_VALUE ) ) {
                if( field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Weighted keys cannot be blacklist type! Converting to null key. Entry: {}",
                            line );
                }
                return ofNull( weight );
            }
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Weighted keys do not allow values! Deleting value. Entry: {}", line );
            }
            key = keyAndValue[0];
        }
        else {
            key = keyWithValue.trim();
        }
        if( key.equalsIgnoreCase( DEFAULT_KEY ) ) {
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Weighted keys cannot be default keys! Converting to null key. Entry: {}", line );
            }
            return ofNull( weight );
        }
        
        // Finally, parse the key
        return key.equalsIgnoreCase( NULL_KEY ) ? ofNull( weight ) :
                of( weight, parser.parseKeyStringNonNull( field, line, key, false ) );
    }
    
    /** @return The key and weight, combined into a single string. */
    public static String keyWithWeight( int weight, String key ) {
        return keyWithValue( IntValueCodec.WEIGHT.toTomlString( weight ), key );
    }
    
    
    private final int weight;
    private final FuzzyKey<T> key;
    
    /** Constructs a key from the loaded string definition. */
    protected WeightedKey( int w, FuzzyKey<T> k ) {
        super( false );
        weight = Math.max( w, 0 );
        key = k;
        
        // Perform some validation
        if( k.isBlacklist() ) {
            throw new IllegalArgumentException( "Weighted keys cannot be blacklist type!" );
        }
    }
    
    /** @return This entry's underlying key. */
    public FuzzyKey<T> getKey() { return key; }
    
    /** @return This entry's weight. Always non-negative. */
    public int getWeight() { return weight; }
    
    
    /** @return True if the other key is contained within this one. */
    @Override
    public boolean matches( T target ) { return key.matches( target ); }
    
    /** @return This fuzzy key's string definition. */
    @Override
    public String keyString() { return key.keyString(); }
    
    /**
     * @return True if this key is a blacklist type; in other words, when this is the best match,
     * the containing set/map should treat it as if no match was found.
     */
    @Override
    public boolean isBlacklist() { return false; }
    
    /** @return True if this key is a default key. A default key's {@link #matches(Object)} always returns true. */
    @Override
    public boolean isDefault() { return key.isDefault(); }
    
    /** @return This value, converted to a single-line string. */
    @Override // ITomlStringValue
    public String toTomlString() { return keyWithWeight( getWeight(), keyString() ); }
}