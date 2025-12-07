package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.ITomlStringValue;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A multi-value codec. Acts as a boilerplate for a value that combines multiple sub-values of any type.
 * This is setup like a condensed form of a config file with spec:
 * <p>
 * To use multi-value codecs, create a class or static nested class extending this with itself as its
 * own type parameter (V). You do not need to add a constructor, but if you do, you must include a
 * no-argument constructor or override {@link #duplicate()}.
 * <p>
 * In the body of the class, declare the sub-values you want in exactly the order you want them to be
 * defined in config files by calling {@link #value(IValueCodec)} and storing the results in final
 * fields. Do NOT access these supplier fields from any instances you create to use as codecs.
 * <p>
 * The values loaded by multi-value codecs will also be instances of the same codec. However, codecs
 * that are loaded as a value have their sub-values loaded in; access the loaded sub-values from these
 * instances through the supplier fields you defined above.
 * <p>
 * It is not required, but may be handy to store an instance of the new class in a public static
 * final field to be used as the codec "singleton", to provide to fields that use an {@link IValueCodec}.
 *
 * @param <V> The type of value this codec reads/writes; should be this multi-value codec itself.
 * @see MobEffectStats An example multi-value codec implementation.
 */
@ApiStatus.Experimental
public abstract class MultiValueCodec<V extends MultiValueCodec<V>> implements IValueCodec<V> {
    /** List of all value entries that have been defined via {@link #value(IValueCodec)}. */
    final List<Entry<?>> entries = new ArrayList<>();
    
    /**
     * Call this to define a sub-value for this multi-value codec.
     * Should only be called during instantiation; either in field definitions or in the constructor.
     * Just store a reference to it so you can access the sub-value from the loaded multi-value.
     *
     * @param codec The sub-value's codec (read/write instructions).
     * @return A supplier that provides the loaded sub-value, or throws a null pointer exception when
     * used from a codec that wasn't returned as a loaded multi-value.
     */
    protected <T> Supplier<T> value( IValueCodec<T> codec ) { return value( codec, null ); }
    
    /**
     * Call this to define a sub-value for this multi-value codec with a custom format hint.
     * Should only be called during instantiation; either in field definitions or in the constructor.
     * Just store a reference to it so you can access the sub-value from the loaded multi-value.
     *
     * @param codec  The sub-value's codec (read/write instructions).
     * @param format A custom format hint to describe this sub-value in config file comments (for example,
     *               {@literal "<Duration (≥ 0)>"}). If null, the codec's standard format hint is used.
     * @return A supplier that provides the loaded sub-value, or throws a null pointer exception when
     * used from a codec that wasn't returned as a loaded multi-value.
     */
    protected <T> Supplier<T> value( IValueCodec<T> codec, @Nullable String format ) {
        Entry<T> entry = new Entry<>( codec, format );
        entries.add( entry );
        return entry;
    }
    
    /** @return A copy of this multi-value codec, with no values loaded. */
    public V duplicate() {
        try {
            Constructor<?> constructor = getClass().getConstructor();
            //noinspection unchecked
            return (V) constructor.newInstance();
        }
        catch( Exception ex ) {
            throw new IllegalStateException( "You must override #duplicate() or provide a default no-args constructor!" );
        }
    }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() {
        validate();
        StringBuilder str = new StringBuilder();
        for( Entry<?> entry : entries ) {
            str.append( FuzzyKey.ARG_SEPARATOR )
                    .append( entry.formatOverride == null ? entry.valueCodec.getFormat() : entry.formatOverride );
        }
        return str.substring( FuzzyKey.ARG_SEPARATOR.length() );
    }
    
    /** @return The value, converted to a single-line string. */
    @Override
    public String toTomlString( V value ) {
        value.validate();
        final StringBuilder str = new StringBuilder();
        for( Entry<?> entry : value.entries ) str.append( FuzzyKey.ARG_SEPARATOR ).append( entry.toTomlString() );
        return str.substring( FuzzyKey.ARG_SEPARATOR.length() );
    }
    
    /** Called to help with error detection. */
    void validate() {
        if( entries.isEmpty() ) throw new IllegalStateException( "Multi-value codec is malformed! (Has no values)" );
    }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override
    public V parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        String[] args = IValueCodec.getArgs( value );
        int actualArgs = args.length;
        
        // Validate argument count
        int expectedArgs = entries.size();
        if( field != null ) {
            if( actualArgs < expectedArgs ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value has too few arguments! Expected {} args, but found {}. Replacing missing args with default values. Entry: {}",
                        expectedArgs, actualArgs, line );
            }
            else if( actualArgs > expectedArgs ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value has too many arguments! Expected {} args, but found {}. Deleting excess args. Entry: {}",
                        expectedArgs, actualArgs, line );
            }
        }
        
        // Parse the arguments
        V clone = duplicate();
        for( int i = 1; i < expectedArgs; i++ ) {
            clone.entries.get( i ).load( field, line, i < args.length ? args[i] : null );
        }
        return clone;
    }
    
    
    /** A field-like implementation of a generic value codec. */
    private static final class Entry<V> implements Supplier<V>, ITomlStringValue {
        /** This entry's read/write logic. */
        public final IValueCodec<V> valueCodec;
        /** The format hint override. */
        @Nullable
        public final String formatOverride;
        
        /**
         * The loaded value.
         * This is always null for the codec "singleton" and never null for loaded value codecs.
         */
        @Nullable
        private V value;
        
        public Entry( IValueCodec<V> codec, @Nullable String format ) {
            valueCodec = codec;
            formatOverride = format;
        }
        
        /** @return The loaded value. Don't call this from codecs you create, only on codecs loaded as a value. */
        @Override // Supplier
        public V get() { return Objects.requireNonNull( value ); }
        
        /** @return This value, converted to a single-line string. */
        @Override // ITomlStringValue
        public String toTomlString() { return valueCodec.toTomlString( get() ); }
        
        /** Loads the entry's value based on the argument string. Called when a codec is being loaded as a value. */
        public void load( @Nullable AbstractConfigField field, String line, @Nullable String arg ) {
            value = valueCodec.parseTomlString( field, line, arg );
        }
    }
}