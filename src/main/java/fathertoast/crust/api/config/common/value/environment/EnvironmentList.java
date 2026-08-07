package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.TomlStringList;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.MultiValueCodec;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A list of condition-value entries used to link one number to specific environments.
 *
 * @see EnvironmentEntry
 * @see EnvironmentContext
 */
@SuppressWarnings( "unused" )
public class EnvironmentList<V> extends TomlStringList<EnvironmentEntry<V>> {
    
    /**
     * Creates a new environment list builder.
     * <p>
     * Note: If you use a codec that takes multiple arguments (space-separated values), use {@link MultiValueCodec}.
     * Otherwise, you must use the other builder method which specifies argument count.
     */
    public static <V> Builder<V> builder( IValueCodec<V> codec ) { return builder( codec, autoPickArgs( codec ) ); }
    
    /**
     * Creates a new environment list builder, specifying the number of value arguments (space-separated values).
     * <p>
     * Only use this if the codec takes multiple arguments but is NOT a {@link MultiValueCodec}.
     */
    public static <V> Builder<V> builder( IValueCodec<V> codec, int args ) { return new Builder<>( codec, args ); }
    
    
    /** This list's value codec. */
    protected final IValueCodec<V> valueCodec;
    /** Number of arguments used by the value codec. */
    protected final int valueArgs;
    
    /**
     * Constructs an empty list. Use this if you want to {@link #load} a list from file/NBT.
     * <p>
     * Note: If you use a codec that takes multiple arguments (space-separated values), use {@link MultiValueCodec}.
     * Otherwise, you must use a constructor which specifies argument count.
     */
    public EnvironmentList( IValueCodec<V> codec ) { this( codec, autoPickArgs( codec ) ); }
    
    /**
     * Constructs an empty list, specifying the number of value arguments (space-separated values).
     * Use this if you want to {@link #load} a list from file/NBT.
     * <p>
     * Only use this if the codec takes multiple arguments but is NOT a {@link MultiValueCodec}.
     */
    public EnvironmentList( IValueCodec<V> codec, int args ) {
        super();
        valueCodec = codec;
        valueArgs = args;
    }
    
    /**
     * Constructs a list containing the entries provided. Use this or the builder for creating default values
     * during config definition.
     * <p>
     * Note: If you use a codec that takes multiple arguments (space-separated values), use {@link MultiValueCodec}.
     * Otherwise, you must use a constructor which specifies argument count.
     */
    @SafeVarargs
    public EnvironmentList( IValueCodec<V> codec, EnvironmentEntry<V>... entries ) {
        this( codec, autoPickArgs( codec ), entries );
    }
    
    /**
     * Constructs a list containing the entries provided, specifying the number of value arguments
     * (space-separated values). Use this or the builder for creating default values during config definition.
     * <p>
     * Only use this if the codec takes multiple arguments but is NOT a {@link MultiValueCodec}.
     */
    @SafeVarargs
    public EnvironmentList( IValueCodec<V> codec, int args, EnvironmentEntry<V>... entries ) {
        super( entries );
        valueCodec = codec;
        valueArgs = args;
    }
    
    /**
     * Constructs a list containing the entries provided. Use this or the builder for creating default values
     * during config definition.
     * <p>
     * Note: If you use a codec that takes multiple arguments (space-separated values), use {@link MultiValueCodec}.
     * Otherwise, you must use a constructor which specifies argument count.
     */
    public EnvironmentList( IValueCodec<V> codec, Collection<? extends EnvironmentEntry<V>> entries ) {
        this( codec, autoPickArgs( codec ), entries );
    }
    
    /**
     * Constructs a list containing the entries provided, specifying the number of value arguments
     * (space-separated values). Use this or the builder for creating default values during config definition.
     * <p>
     * Only use this if the codec takes multiple arguments but is NOT a {@link MultiValueCodec}.
     */
    public EnvironmentList( IValueCodec<V> codec, int args, Collection<? extends EnvironmentEntry<V>> entries ) {
        super( entries );
        valueCodec = codec;
        valueArgs = args;
    }
    
    /**
     * @return The value for the first entry that matches the given environment context,
     * or null if no matching environment is defined.
     */
    @Nullable
    public V get( EnvironmentContext context ) {
        for( EnvironmentEntry<V> entry : this ) if( entry.test( context ) ) return entry.getValue();
        return null;
    }
    
    /**
     * @return The value for the first entry that matches the given environment context,
     * or the default value if no matching environment is defined.
     */
    public V getOrElse( EnvironmentContext context, V defaultValue ) {
        return Objects.requireNonNullElse( get( context ), defaultValue );
    }
    
    /**
     * @return The value for the first entry that matches the given environment context,
     * or the default value if no matching environment is defined.
     */
    public V getOrElse( EnvironmentContext context, Supplier<V> defaultValue ) {
        return Objects.requireNonNullElseGet( get( context ), defaultValue );
    }
    
    /**
     * Note: This method is less preferred over the others, as the codec default is not really
     * configurable. However, in some cases this may be perfectly acceptable.
     *
     * @return The value for the first entry that matches the given environment context,
     * or the codec's default value if no matching environment is defined.
     */
    public V getOrDefault( EnvironmentContext context ) {
        return Objects.requireNonNullElse( get( context ), valueCodec.getDefaultValue() );
    }
    
    /**
     * Loads this value from the given list. If anything goes wrong, correct it at the lowest level possible.
     * If the field is null, error reporting is suppressed.
     *
     * @param field The config field we are loading for, or null if not loading from a config.
     * @param value List value to load from. This generally comes from a TOML string array value
     *              (config loading) or a string list tag (NBT loading).
     */
    @Override
    public void load( @Nullable IConfigField<?> field, List<String> value ) {
        final ArrayList<EnvironmentEntry<V>> list = new ArrayList<>( value.size() );
        value.forEach( line -> list.add( new EnvironmentEntry<>( field, valueCodec, valueArgs, line ) ) );
        
        // Tidy up and set value
        list.trimToSize();
        setList( list );
    }
    
    /** @return This list's value codec. */
    public IValueCodec<V> codec() { return valueCodec; }
    
    /** @return This list's value arguments. */
    public int args() { return valueArgs; }
    
    /** @return The number of arguments we think the codec uses. */
    private static int autoPickArgs( IValueCodec<?> codec ) {
        return codec instanceof MultiValueCodec<?> mvc ? mvc.arguments() : 1;
    }
    
    
    // ---- Builder Implementation ---- //
    
    /**
     * Builder class used to simplify creation of environment lists for default configs.
     */
    public static class Builder<V> {
        
        /** This list's value codec. */
        private final IValueCodec<V> valueCodec;
        /** Number of arguments used by the value codec. */
        private final int valueArgs;
        
        /** The current list. */
        private final List<EnvironmentEntry<V>> entryList = new ArrayList<>();
        
        private Builder( IValueCodec<V> codec, int args ) {
            valueCodec = codec;
            valueArgs = args;
        }
        
        /** @return An environment list reflecting the current state of this builder. */
        public EnvironmentList<V> build() { return new EnvironmentList<>( valueCodec, valueArgs, entryList ); }
        
        /**
         * Creates a new environment entry builder and returns it. Whenever you build the environment
         * entry, it will return this builder so you can continue adding entries or build the list.
         */
        public EnvironmentEntry.Builder<V> entryBuilder( V value ) {
            return new EnvironmentEntry.Builder<>( this, valueCodec.toTomlString( value ) );
        }
        
        /** Adds an environment entry to the list. */
        public Builder<V> add( EnvironmentEntry<V> entry ) {
            entryList.add( entry );
            return this;
        }
        
        /** Adds an environment entry to the list. */
        public Builder<V> add( V value, String condition ) {
            entryList.add( new EnvironmentEntry<>( null, valueCodec, valueArgs,
                    valueCodec.toTomlString( value ) + " " + condition ) );
            return this;
        }
        
        /** Adds an environment entry to the list. */
        public Builder<V> add( String valueAndCondition ) {
            entryList.add( new EnvironmentEntry<>( null, valueCodec, valueArgs, valueAndCondition ) );
            return this;
        }
    }
}