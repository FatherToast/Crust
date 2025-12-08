package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Provides instructions on how to read/write a value from/to an entry value TOML string.
 *
 * @param <V> The type of value this codec reads/writes.
 * @see DoubleValueCodec
 * @see IntValueCodec
 * @see ArrayValueCodec
 * @see fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser
 */
@ApiStatus.Experimental
public interface IValueCodec<V> {
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    String getFormat();
    
    /** @return The value, converted to a single-line string. */
    default String toTomlString( V value ) { return TomlHelper.toLiteral( value ); }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    V parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value );
    
    /** @return This codec's default value; basically, shorthand for loading a missing value. */
    default V getDefaultValue() { return parseTomlString( null, "", null ); }
    
    
    /** @return The string, split into an array of arguments. */
    static String[] getArgs( @Nullable String value ) {
        return value == null ? new String[0] : value.trim().split( FuzzyKey.ARG_SEPARATOR );
    }
}