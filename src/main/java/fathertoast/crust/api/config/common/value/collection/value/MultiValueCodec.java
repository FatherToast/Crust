package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
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
 * defined in config files by calling {@link #subValue(IValueCodec)} and storing the results in final
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
public abstract class MultiValueCodec<V extends MultiValueCodec<V>> implements IValueCodec<V>, ITomlStringValue {
    /**
     * Call this to define a sub-value for this multi-value codec.
     * Should only be called during instantiation; either in field definitions or in the constructor.
     * Just store a reference to it so you can access the sub-value from the loaded multi-value.
     *
     * @param codec The sub-value's codec (read/write instructions).
     * @return A sub-value holder.
     */
    protected <T> SubValue<T> subValue( IValueCodec<T> codec ) { return subValue( codec, null ); }
    
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
    protected <T> SubValue<T> subValue( IValueCodec<T> codec, @Nullable String format ) {
        SubValue<T> v = new SubValue<>( codec, format );
        subValues.add( v );
        return v;
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
    
    /** @return The number of arguments this codec uses; i.e., how many sub-values it has. */
    public int arguments() { return subValues.size(); }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() {
        validate();
        StringBuilder str = new StringBuilder();
        for( SubValue<?> v : subValues ) str.append( FuzzyKey.ARG_SEPARATOR ).append( v.getFormat() );
        return str.substring( FuzzyKey.ARG_SEPARATOR.length() );
    }
    
    /** @return The value, converted to a single-line string. */
    @Override
    public String toTomlString( V value ) {
        value.validate();
        final StringBuilder str = new StringBuilder();
        for( SubValue<?> v : value.subValues ) str.append( FuzzyKey.ARG_SEPARATOR ).append( v.toTomlString() );
        return str.substring( FuzzyKey.ARG_SEPARATOR.length() );
    }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override
    public V parseTomlString( @Nullable IConfigField<?> field, String line, @Nullable String value ) {
        String[] args = IValueCodec.getArgs( value );
        int actualArgs = args.length;
        
        // Validate argument count
        int expectedArgs = subValues.size();
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
        for( int i = 0; i < expectedArgs; i++ ) {
            clone.subValues.get( i ).load( field, line, i < args.length ? args[i] : null );
        }
        return clone;
    }
    
    /** @return This sub-value, converted to a single-line string. */
    @Override // ITomlStringValue
    public String toTomlString() {
        //noinspection unchecked
        return toTomlString( (V) this );
    }
    
    @Override
    public String toString() { return toTomlString(); }
    
    /** List of all sub-values that have been defined via {@link #subValue(IValueCodec)}. */
    @ApiStatus.Internal
    final List<SubValue<?>> subValues = new ArrayList<>();
    
    /** Called to help with error detection. */
    @ApiStatus.Internal
    void validate() {
        if( subValues.isEmpty() ) throw new IllegalStateException( "Multi-value codec is malformed! (Has no values)" );
    }
    
    
    /**
     * A field-like implementation of a generic value codec.
     * Holds the sub-value's codec and, when used as a loaded value codec, the sub-value itself.
     */
    public static final class SubValue<V> implements Supplier<V>, ITomlStringValue {
        /** This entry's read/write logic. */
        private final IValueCodec<V> valueCodec;
        /** The format hint override. */
        @Nullable
        private final String formatOverride;
        
        /**
         * The loaded value.
         * This is always null for the codec "singleton" and never null for loaded value codecs.
         */
        @Nullable
        private V value;
        
        public SubValue( IValueCodec<V> codec, @Nullable String format ) {
            valueCodec = codec;
            formatOverride = format;
        }
        
        /** Sets the sub-value. Use this for creating default sub-values. */
        public void set( V v ) { value = v; }
        
        /** @return The loaded sub-value. Call this from codecs loaded as a value. */
        @Override // Supplier
        public V get() { return Objects.requireNonNull( value ); }
        
        /** @return The sub-value's codec. */
        public IValueCodec<V> codec() { return valueCodec; }
        
        /** @return This sub-value, converted to a single-line string. */
        @Override // ITomlStringValue
        public String toTomlString() {
            return codec().toTomlString( value == null ?
                    // Handle case of using unloaded codecs as default values
                    codec().getDefaultValue() : value );
        }
        
        /** @return This sub-value, converted to a single-line string. */
        @Override
        public String toString() { return toTomlString(); }
        
        /** @return The sub-value format (for example, {@literal "<Number (Any Value)>"}). */
        @ApiStatus.Internal
        String getFormat() { return formatOverride == null ? codec().getFormat() : formatOverride; }
        
        /** Loads the entry's sub-value based on the argument string. Called when a codec is being loaded as a value. */
        @ApiStatus.Internal
        void load( @Nullable IConfigField<?> field, String line, @Nullable String arg ) {
            value = codec().parseTomlString( field, line, arg );
        }
    }
}