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
 */
@ApiStatus.Experimental
public interface IValueCodec<V> {
    
    /** @return The value format (e.g., {@literal "<Number (Any Value)>"}). */
    String getFormat();
    
    /** @return The value, converted to a single-line string. */
    default String toTomlString( V value ) { return TomlHelper.toLiteral( value ); }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string.
     */
    V parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value );
    
    
    /** @return The string, split into an array of arguments. */
    static String[] getArgs( @Nullable String value ) {
        return value == null ? new String[0] : value.trim().split( FuzzyKey.ARG_SEPARATOR );
    }
    
    //TODO spec-style codec
    
    //    abstract class Custom<T> implements EntryValueCodec<T> {
    //        protected final List<Arg<?>> arguments = new ArrayList<>();
    //
    //        protected <A> Arg<A> define( Arg<A> argument ) {
    //            arguments.add( argument );
    //            return argument;
    //        }
    //
    //        public int args() { return arguments.size(); }
    //    }
}