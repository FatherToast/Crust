package fathertoast.crust.api.util;

import com.google.common.collect.ImmutableMap;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.ITomlStringValue;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A mapping of block state property=value pairs used to represent block states entirely
 * separated from blocks. This allows more generalized property definition and does not
 * need the block registry to be populated to be built and used.
 * <p>
 * The downside to this approach is that error-checking is very difficult, as we do not
 * always know what states are allowed for the block we want to use. When the block is
 * available, use the actual Property parameter methods.
 * <p>
 * This is generally intended for config use, so its focus is on immutability; however,
 * it does offer a mutable version that exposes a modifiable underlying Map.
 *
 * @see Property
 * @see NbtUtils#readBlockState(HolderGetter, CompoundTag)
 * @see NbtUtils#writeBlockState(BlockState)
 * @see BlockStateParser
 * @see BlockStateProperties
 * @see BlockStatePredicate
 */
public record BlockStatePropertyMap(Map<String, String> map) implements
        ITomlStringValue, IValueCodec<BlockStatePropertyMap> {
    
    public static final char VALUE_SEPARATOR = '=';
    public static final char PROPERTY_SEPARATOR = ',';
    public static final char START_CHAR = '[';
    public static final char END_CHAR = ']';
    private static final String EMPTY_STRING = String.valueOf( START_CHAR ) + END_CHAR;
    
    /** An empty, immutable block state property map. */
    public static final BlockStatePropertyMap EMPTY = new BlockStatePropertyMap( Collections.emptyMap() );
    
    /** The standard block state property map codec. Defaults to an empty map. */
    public static final IValueCodec<BlockStatePropertyMap> CODEC = EMPTY; // lol
    
    /** @return An immutable block state property map parsed from the provided block state. */
    public static BlockStatePropertyMap of( BlockState state ) { return new Builder().putAll( state ).build(); }
    
    /** @return An immutable block state property map parsed from the provided string. */
    public static BlockStatePropertyMap of( String properties ) {
        return properties.isEmpty() || EMPTY_STRING.equals( properties ) ? EMPTY :
                new BlockStatePropertyMap( Collections.unmodifiableMap( extract( properties ) ) );
    }
    
    /** @return A new mutable block state property map parsed from the provided block state. */
    public static BlockStatePropertyMap ofMutable( BlockState state ) { return new Builder().putAll( state ).buildMutable(); }
    
    /** @return A new mutable block state property map parsed from the provided string. */
    public static BlockStatePropertyMap ofMutable( String properties ) {
        return new BlockStatePropertyMap( extract( properties ) );
    }
    
    /** @return The property-value map, converted to a single-line string. */
    public static String combine( Map<String, String> properties ) { return combine( properties, false ); }
    
    /** @return The property-value map, converted to a single-line string. */
    public static String combine( Map<String, String> properties, boolean addSpaces ) {
        if( properties.isEmpty() ) return EMPTY_STRING;
        StringBuilder str = new StringBuilder( String.valueOf( START_CHAR ) );
        for( Map.Entry<String, String> entry : properties.entrySet() ) {
            if( addSpaces ) str.append( ' ' );
            str.append( entry.getKey() ).append( VALUE_SEPARATOR ).append( entry.getValue() ).append( PROPERTY_SEPARATOR );
        }
        str.deleteCharAt( str.length() - 1 );
        if( addSpaces ) str.append( ' ' );
        return str.append( END_CHAR ).toString();
    }
    
    /** @return The property and value, converted to a single-line string. */
    public static String combine( String property, String value ) { return property + VALUE_SEPARATOR + value; }
    
    /**
     * @return A new property-value map, extracted from the provided string. Ignores all whitespace.
     * Square brackets ([]) are optional; if included, this will only extract property-value pairs from
     * the portion of the string inside the square brackets. Otherwise, it uses the entire string.
     */
    public static Map<String, String> extract( String properties ) {
        Map<String, String> map = new LinkedHashMap<>();
        if( !properties.isEmpty() ) {
            boolean noStartChar = true;
            String property = null;
            StringBuilder current = new StringBuilder();
            for( char c : properties.toCharArray() ) {
                if( !Character.isWhitespace( c ) ) {
                    // Check for optional start/end chars
                    if( c == END_CHAR ) break;
                    if( noStartChar && c == START_CHAR ) {
                        noStartChar = false;
                        // Restart
                        map.clear();
                        property = null;
                        current.setLength( 0 );
                    }
                    else if( property == null ) {
                        // Building a property
                        if( c == VALUE_SEPARATOR ) {
                            property = current.toString();
                            current.setLength( 0 );
                        }
                        else if( c == PROPERTY_SEPARATOR ) {
                            map.put( current.toString(), "" );
                            current.setLength( 0 );
                        }
                        else {
                            current.append( c );
                        }
                    }
                    else {
                        // Building a value
                        if( c == PROPERTY_SEPARATOR ) {
                            map.put( property, current.toString() );
                            property = null;
                            current.setLength( 0 );
                        }
                        else {
                            current.append( c );
                        }
                    }
                }
            }
            // Close out whatever we were last building
            if( property != null ) {
                map.put( property, current.toString() );
            }
            else if( !current.isEmpty() ) {
                map.put( current.toString(), "" );
            }
        }
        return map;
    }
    
    /**
     * @return Splits a block state string into a registry object string (index 0) and a block state
     * properties string (index 1). The returned array always contains 2 non-null strings.
     */
    public static String[] split( String s ) {
        int startIndex = s.indexOf( START_CHAR );
        if( startIndex < 0 ) return new String[] { s, "" };
        return new String[] { s.substring( 0, startIndex ), s.substring( startIndex ) };
    }
    
    /** @return Parses the block state string into a block state, if possible. Returns null otherwise. */
    @Nullable
    public static BlockState stateFrom( String s ) {
        final String[] split = split( s );
        ResourceLocation resLoc = ResourceLocation.tryParse( split[0] );
        return resLoc == null ? null : BlockStatePropertyMap.of( split[1] )
                .stateForNullable( ForgeRegistries.BLOCKS.getValue( resLoc ) );
    }
    
    /**
     * @return The same result as {@link BlockStatePropertyMap#stateFrom(String)},
     * except the first part of the string <br>
     * (ID / resource location) MUST be a complete resource location
     * with namespace included.
     * <br><br>
     * For example, <strong>"coolmod:epic_stone[epic=true]"</strong> will parse,
     * <br>
     * but <strong>"stupid_block[uncool=true]"</strong> will not.
     */
    @Nullable
    public static BlockState strictStateFrom( String s ) {
        final String[] split = split( s );
        final String id = split[0];
        
        // We want both the namespace and path, no partial
        if( id.split( ":" ).length != 2 )
            return null;
        
        ResourceLocation resLoc = ResourceLocation.tryParse( id );
        
        return resLoc == null ? null : BlockStatePropertyMap.of( split[1] )
                .stateForNullable( ForgeRegistries.BLOCKS.getValue( resLoc ) );
    }
    
    // ---- Instance Methods ---- //
    
    /** @return True if this has no properties defined. */
    public boolean isEmpty() { return map().isEmpty(); }
    
    /** @return True if the block state has the same values for all properties it shares with this map. */
    public boolean matches( BlockState state ) {
        if( isEmpty() ) return true;
        StateDefinition<Block, BlockState> stateDef = state.getBlock().getStateDefinition();
        for( Map.Entry<String, String> entry : map().entrySet() ) {
            Property<?> property = stateDef.getProperty( entry.getKey() );
            if( property != null && !hasValue( state, property, entry.getValue() ) )
                return false;
        }
        return true;
    }
    
    /** @return True if the block state has all properties defined in this map, with the same values. */
    public boolean matchesStrict( BlockState state ) {
        if( isEmpty() ) return true;
        StateDefinition<Block, BlockState> stateDef = state.getBlock().getStateDefinition();
        for( Map.Entry<String, String> entry : map().entrySet() ) {
            Property<?> property = stateDef.getProperty( entry.getKey() );
            if( property == null || !hasValue( state, property, entry.getValue() ) )
                return false;
        }
        return true;
    }
    
    /**
     * @return Applies all properties and values defined in this map to the block, starting from
     * the block's default state. Property-value pairs that are invalid for the block are ignored.
     */
    public BlockState stateFor( Block block ) { return stateFor( block.defaultBlockState() ); }
    
    /**
     * @return Applies all properties and values defined in this map to the block, starting from
     * the provided state. Property-value pairs that are invalid for the block are ignored.
     */
    public BlockState stateFor( BlockState state ) {
        if( !isEmpty() ) {
            StateDefinition<Block, BlockState> stateDef = state.getBlock().getStateDefinition();
            for( Map.Entry<String, String> entry : map().entrySet() ) {
                Property<?> property = stateDef.getProperty( entry.getKey() );
                if( property != null ) {
                    state = setValue( state, property, entry.getValue() );
                }
            }
        }
        return state;
    }
    
    /**
     * @return Applies all properties and values defined in this map to the block, starting from
     * the block's default state. Property-value pairs that are invalid for the block are ignored.
     * Only returns null if the provided block was null.
     */
    @Nullable
    public BlockState stateForNullable( @Nullable Block block ) { return block == null ? null : stateFor( block ); }
    
    /**
     * @return Applies all properties and values defined in this map to the block, starting from
     * the block's default state. Property-value pairs that are invalid for the block are ignored.
     * Only returns null if the provided block state was null.
     */
    @Nullable
    public BlockState stateForNullable( @Nullable BlockState state ) { return state == null ? null : stateFor( state ); }
    
    /** @return This value, converted to a single-line string. */
    @Override // ITomlStringValue
    public String toTomlString() { return combine( map() ); }
    
    /** @return This value, converted to a single-line string. */
    @Override
    public String toString() { return toTomlString(); }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override // IValueCodec
    public String getFormat() { return "<[property=value,...]>"; }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override // IValueCodec
    public BlockStatePropertyMap parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        return value == null ? EMPTY : of( value );
    }
    
    
    /**
     * A simple builder that can be used to more easily make block state property maps.
     */
    public static class Builder {
        /** Underlying map. */
        private final Map<String, String> map = new LinkedHashMap<>();
        
        /** @return A new immutable block state property map based on the current state of this builder. */
        public BlockStatePropertyMap build() { return new BlockStatePropertyMap( Collections.unmodifiableMap( map ) ); }
        
        /** @return A new mutable block state property map. Further changes to this builder will affect the map. */
        public BlockStatePropertyMap buildMutable() { return new BlockStatePropertyMap( map ); }
        
        /** Adds a property=value pair to the map. */
        public Builder put( String property, String value ) {
            map.put( property, value );
            return this;
        }
        
        /** Adds a property=value pair to the map. */
        public Builder put( Property<?> property, String value ) {
            return put( property.getName(), value );
        }
        
        /** Adds a property=value pair to the map. */
        public <T extends Comparable<T>> Builder put( Property<T> property, T value ) {
            return put( property, property.getName( value ) );
        }
        
        /** Adds a property=value pair to the map. */
        public <T extends Comparable<T>> Builder put( Property.Value<T> propertyValue ) {
            return put( propertyValue.property(), propertyValue.value() );
        }
        
        /** Adds all property=value pairs to the map. */
        public Builder putAll( BlockState state ) {
            ImmutableMap<Property<?>, Comparable<?>> stateProperties = state.getValues();
            if( !stateProperties.isEmpty() ) {
                for( Map.Entry<Property<?>, Comparable<?>> entry : stateProperties.entrySet() ) {
                    Property<?> property = entry.getKey();
                    put( property, getName( property, entry.getValue() ) );
                }
            }
            return this;
        }
    }
    
    
    /** @return True if the block state property's value is equal to the given value. */
    private static boolean hasValue( BlockState state, Property<?> property, String value ) {
        return Objects.equals( state.getOptionalValue( property ), property.getValue( value ) );
    }
    
    /** @return Tries to set a block state property's value and returns the result. */
    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValue( S state, Property<T> property, String value ) {
        return property.getValue( value ).map( t -> state.setValue( property, t ) ).orElse( state );
    }
    
    /** @return The value, converted to a string. We use this to fix generic type issues. */
    private static <T extends Comparable<T>> String getName( Property<T> property, Comparable<?> value ) {
        //noinspection unchecked
        return property.getName( (T) value );
    }
}