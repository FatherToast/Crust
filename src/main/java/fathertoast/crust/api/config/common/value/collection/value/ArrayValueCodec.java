package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;

import javax.annotation.Nullable;
import java.lang.reflect.Array;

/**
 * An array value codec. Supports either arrays of specified length, or arrays of length >= 1.
 * <p>
 * This is the classic value pattern provided by Crust.
 * Legacy "value lists" all use the equivalent of the double array codec.
 *
 * @param <V> The array type.
 */
@SuppressWarnings( "ClassCanBeRecord" )
public class ArrayValueCodec<V> implements IValueCodec<V[]> {
    
    
    /** @param length If >0, the array value will have exactly this length. Otherwise, its length will be >=1. */
    public static ArrayValueCodec<Double> ofDoubles( int length, double defaultValue, DoubleField.Range range ) { return of( length, Double.class, DoubleValueCodec.of( defaultValue, range ) ); }
    
    /** @param length If >0, the array value will have exactly this length. Otherwise, its length will be >=1. */
    public static ArrayValueCodec<Double> ofDoubles( int length, double defaultValue, double min, double max ) { return of( length, Double.class, DoubleValueCodec.of( defaultValue, min, max ) ); }
    
    /** @param length If >0, the array value will have exactly this length. Otherwise, its length will be >=1. */
    public static ArrayValueCodec<Integer> ofInts( int length, int defaultValue, IntField.Range range ) { return of( length, Integer.class, IntValueCodec.of( defaultValue, range ) ); }
    
    /** @param length If >0, the array value will have exactly this length. Otherwise, its length will be >=1. */
    public static ArrayValueCodec<Integer> ofInts( int length, int defaultValue, int min, int max ) { return of( length, Integer.class, IntValueCodec.of( defaultValue, min, max ) ); }
    
    /** @param length If >0, the array value will have exactly this length. Otherwise, its length will be >=1. */
    public static <T> ArrayValueCodec<T> of( int length, Class<T> type, IValueCodec<T> codec ) { return new ArrayValueCodec<>( length, type, codec ); }
    
    
    // ---- Instance Methods ---- //
    
    public final int length;
    public final IValueCodec<V> elementCodec;
    public final Class<V> typeClass;
    
    private ArrayValueCodec( int len, Class<V> type, IValueCodec<V> codec ) {
        length = len;
        elementCodec = codec;
        typeClass = type;
    }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return elementCodec.getFormat() + " x " + (length > 0 ? length : "1+") + " Times"; }
    
    /** @return The value, converted to a single-line string. */
    @Override
    public String toTomlString( V[] value ) {
        final StringBuilder str = new StringBuilder();
        for( Object arg : value ) {
            // Do null check to allow simple array initializers to be used as default values (e.g., new Integer[3])
            str.append( FuzzyKey.ARG_SEPARATOR ).append( arg == null ? elementCodec.getDefaultValue() : arg.toString() );
        }
        return str.substring( FuzzyKey.ARG_SEPARATOR.length() );
    }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override
    public V[] parseTomlString( @Nullable IConfigField<?> field, String line, @Nullable String value ) {
        String[] args = IValueCodec.getArgs( value );
        int actualArgs = args.length;
        
        // Determine argument count
        int expectedArgs;
        if( length < 1 ) { expectedArgs = Math.max( 1, actualArgs ); }
        else expectedArgs = length;
        if( field != null ) {
            if( actualArgs < expectedArgs ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value has too few arguments! Expected {}, but found {}. Replacing missing args with {}. Entry: {}",
                        length < 1 ? "at least one arg" : expectedArgs + " args", actualArgs,
                        elementCodec.getDefaultValue(), line );
            }
            else if( actualArgs > expectedArgs ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value has too many arguments! Expected {} args, but found {}. Deleting excess args. Entry: {}",
                        expectedArgs, actualArgs, line );
            }
        }
        // Parse the arguments
        V[] v = makeArray( expectedArgs, typeClass );
        for( int i = 0; i < expectedArgs; i++ ) {
            v[i] = elementCodec.parseTomlString( field, line, get( args, i ) );
        }
        return v;
    }
    
    private static <T> T[] makeArray( int length, Class<T> type ) {
        // noinspection unchecked
        return (T[]) Array.newInstance( type, length );
    }
    
    @Nullable
    private static <T> T get( T[] a, int i ) { return i < a.length ? a[i] : null; }
}