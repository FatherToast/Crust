package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.ITomlStringValue;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A multi-value codec. Acts as a boilerplate for a value that combines multiple values of any type.
 * This is setup like a condensed form of a config file with spec:
 * <p>
 * To use multi-value codecs, create a class or static nested class extending this with that same
 * class as its own type parameter (V). You do not need to add a constructor, but if you do, you must
 * include a no-argument constructor or override {@link #duplicate()} to still deep-clone the class.
 * <p>
 * In the body of the class, declare the values you want in exactly the order you want them to be
 * defined in config files by calling {@link #value(IValueCodec)} and storing the results in final
 * fields. Do NOT access these supplier fields from the instances you use as codecs.
 * <p>
 * It is not required, but may be handy to store an instance of the new class in a public static
 * final field to be used as the codec "singleton".
 * <p>
 * Finally, the values loaded by this codec will also be instances of the codec - however, the values
 * are loaded in, so you may access the loaded values through the supplier fields you defined above.
 *
 * @param <V> The type of value this codec reads/writes; should be this multi-value codec itself.
 * @see MultiValueCodec.MobEffectStats An example multi-value codec.
 */
@ApiStatus.Experimental
public abstract class MultiValueCodec<V extends MultiValueCodec<V>> implements IValueCodec<V> {
    
    /** Holds the duration and amplifier for a mob effect instance. */
    public static class MobEffectStats extends MultiValueCodec<MobEffectStats> {
        /** The mob effect stats codec "singleton". */
        public static final MobEffectStats CODEC = new MobEffectStats();
        
        /** The effect duration, in ticks (20 ticks = 1 second). */
        public final Supplier<Integer> duration = value( IntValueCodec.NON_NEGATIVE );
        /** The effect amplifier (0 = I, 1 = II, etc.). */
        public final Supplier<Integer> amplifier = value( IntValueCodec.ANY );
        
        /** @return A new effect instance using the loaded duration and amplifier. */
        public MobEffectInstance create( MobEffect effect ) {
            return new MobEffectInstance( effect, duration.get(), amplifier.get() );
        }
        
        /** @return A new invisible effect instance using the loaded duration and amplifier. */
        public MobEffectInstance createInvisible( MobEffect effect ) {
            return new MobEffectInstance( effect, duration.get(), amplifier.get(), true, false, false );
        }
    }
    
    
    // ---- Instance Methods ---- //
    
    final List<Entry<?>> entries = new ArrayList<>();
    
    protected <T> Supplier<T> value( IValueCodec<T> codec ) {
        Entry<T> entry = new Entry<>( codec );
        entries.add( entry );
        return entry;
    }
    
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
        for( Entry<?> entry : entries ) str.append( FuzzyKey.ARG_SEPARATOR ).append( entry.valueCodec.getFormat() );
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
        
        final IValueCodec<V> valueCodec;
        @Nullable
        V value;
        
        Entry( IValueCodec<V> codec ) { this( codec, null ); }
        
        Entry( IValueCodec<V> codec, @Nullable V v ) {
            valueCodec = codec;
            value = v;
        }
        
        /** @return The loaded value. Don't call this on the codec itself, only on the loaded "codec clone" value. */
        @Override // Supplier
        public V get() { return Objects.requireNonNull( value ); }
        
        /** @return This value, converted to a single-line string. */
        @Override // ITomlStringValue
        public String toTomlString() { return valueCodec.toTomlString( get() ); }
        
        /** Loads the entry's value based on the argument string. */
        public void load( @Nullable AbstractConfigField field, String line, @Nullable String arg ) {
            value = valueCodec.parseTomlString( field, line, arg );
        }
    }
}