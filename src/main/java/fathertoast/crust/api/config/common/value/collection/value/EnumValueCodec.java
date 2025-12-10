package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * An enum value codec. Defines a default value and valid values.
 * <p>
 * Just like {@link fathertoast.crust.api.config.common.field.EnumField}, this uses {@link Enum#name()}
 * for its string representations and is not case-sensitive. Therefore, you should avoid vanilla enums
 * (due to obfuscation) and any enums with constants that share the same name when ignoring case.
 */
@SuppressWarnings( "ClassCanBeRecord" )
@ApiStatus.Experimental
public class EnumValueCodec<V extends Enum<V>> implements IValueCodec<V>, IValueCorrector<V> {
    
    // Disabling these until MC disables obfuscation
    //    /** The standard enum codec for any direction. Defaults to DOWN. */
    //    public static final EnumValueCodec<Direction> DIRECTION = of( Direction.DOWN );
    //
    //    /** The standard enum codec for horizontal directions. Defaults to NORTH. */
    //    public static final EnumValueCodec<Direction> HORIZONTAL_DIRECTION = of( Direction.NORTH,
    //            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST );
    //
    //    /** The standard enum codec for vertical directions. Defaults to DOWN. */
    //    public static final EnumValueCodec<Direction> VERTICAL_DIRECTION = of( Direction.DOWN,
    //            Direction.DOWN, Direction.UP );
    
    /** An enum codec that allows any value from the enum. */
    public static <V extends Enum<V>> EnumValueCodec<V> of( V defaultValue ) {
        //noinspection unchecked
        return of( defaultValue, (V[]) defaultValue.getClass().getEnumConstants() );
    }
    
    /** An enum codec that allows specific values from the enum. */
    @SafeVarargs
    public static <V extends Enum<V>> EnumValueCodec<V> of( V defaultValue, V... validValues ) {
        return new EnumValueCodec<>( defaultValue, validValues );
    }
    
    
    // ---- Instance Methods ---- //
    
    public final V defaultValue;
    public final V[] validValues;
    
    private EnumValueCodec( V def, V[] valid ) {
        defaultValue = def;
        validValues = valid;
    }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() {
        if( validValues.length > 8 ) return "<Enum>";
        
        StringBuilder str = new StringBuilder( "<" );
        boolean first = true;
        for( V v : validValues ) {
            if( first ) first = false;
            else str.append( "|" );
            str.append( TomlHelper.enumToString( v ) );
        }
        return str.append( ">" ).toString();
    }
    
    /** @return The value, converted to a single-line string. */
    @Override
    public String toTomlString( V value ) { return TomlHelper.enumToString( value ); }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override // IValueCodec
    public V parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        if( value != null ) for( V val : validValues ) {
            if( val.name().equalsIgnoreCase( value ) ) return val;
        }
        return defaultValue;
    }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value to correct, or null if the value is missing.
     * @return The same value if it is present and valid. If the value is missing, a default value is quietly returned.
     * If invalid, it reports the problem (unless field is null) and returns the closest valid value.
     */
    @Override // IValueCorrector
    public V correctValue( @Nullable AbstractConfigField field, String line, @Nullable V value ) {
        return value == null || !isValid( value ) ? defaultValue : value;
    }
    
    /** @return True if the value is valid. */
    private boolean isValid( V value ) {
        for( V v : validValues ) if( v == value ) return true;
        return false;
    }
}