package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * A key for fuzzy collections that test against or contain block states. Very similar to a Block registry
 * object key, but allows specifying block state properties in addition to (or instead of) registered blocks.
 */
@ApiStatus.Experimental
public abstract class BlockStateKey<K extends RegObjKey<Block>> extends FuzzyKey<BlockState> {
    
    /** The parser for block state keys. */
    public static final IFuzzyKeyParser<BlockState> PARSER = new Parser();
    
    /** @return A new props-only key based on the block state properties. */
    public static PropsOnly ofProps( String properties, boolean blacklist ) {
        return ofProps( BlockStatePropertyMap.of( properties ), blacklist );
    }
    
    /** @return A new props-only key based on the block state properties. */
    public static PropsOnly ofProps( BlockStatePropertyMap properties, boolean blacklist ) {
        return new PropsOnly( blacklist, properties );
    }
    
    /** @return A new key based on the resource location and block state properties. */
    public static Basic of( String resLocAndProperties, boolean blacklist ) {
        String[] keys = BlockStatePropertyMap.split( resLocAndProperties );
        return of( RegObjKey.of( REGISTRY, keys[0], blacklist ),
                BlockStatePropertyMap.of( keys[1] ) );
    }
    
    /** @return A new key based on the resource location and block state properties. */
    public static Basic of( String resLoc, BlockStatePropertyMap properties, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, resLoc, blacklist ), properties );
    }
    
    /** @return A new key based on the resource location and block state properties. */
    public static Basic of( ResourceLocation resLoc, BlockStatePropertyMap properties, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, resLoc, blacklist ), properties );
    }
    
    /** @return A new key based on the registry object and block state properties. */
    public static Basic of( RegistryObject<? extends Block> regObj, BlockStatePropertyMap properties, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, regObj, blacklist ), properties );
    }
    
    /** @return A new key based on the resource key and block state properties. */
    public static Basic of( ResourceKey<? extends Block> resKey, BlockStatePropertyMap properties, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, resKey, blacklist ), properties );
    }
    
    /**
     * @return A new key based on the block and block state properties.
     * When building default config values, this is only suitable for vanilla blocks unless you
     * hold off config initialization until after the blocks registry is populated.
     */
    public static Basic of( Block block, BlockStatePropertyMap properties, boolean blacklist ) {
        return of( RegObjKey.of( REGISTRY, block, blacklist ), properties );
    }
    
    /**
     * @return A new key based on the block state. Will only match the provided state exactly.
     * When building default config values, this is only suitable for vanilla blocks unless you
     * hold off config initialization until after the blocks registry is populated.
     */
    public static Basic of( BlockState blockState, boolean blacklist ) {
        return of( blockState.getBlock(), BlockStatePropertyMap.of( blockState ), blacklist );
    }
    
    /** @return A new key based on the block registry object key and block state properties. */
    public static Basic of( RegObjKey.Basic<Block> key, BlockStatePropertyMap properties ) {
        return new Basic( key, properties );
    }
    
    /** @return A new wildcard key, based on the partial resource location and block state properties. */
    public static Wildcard ofWildcard( ResourceLocation partialResLoc, BlockStatePropertyMap properties, boolean blacklist ) {
        return ofWildcard( RegObjKey.ofWildcard( REGISTRY, partialResLoc, blacklist ), properties );
    }
    
    /** @return A new wildcard key, based on the namespace and block state properties. */
    public static Wildcard ofWildcard( String namespace, BlockStatePropertyMap properties, boolean blacklist ) {
        return ofWildcard( RegObjKey.ofWildcard( REGISTRY, namespace, blacklist ), properties );
    }
    
    /** @return A new wildcard key, based on the namespace, partial path, and block state properties. */
    public static Wildcard ofWildcard( String namespace, String partialPath, BlockStatePropertyMap properties, boolean blacklist ) {
        return ofWildcard( RegObjKey.ofWildcard( REGISTRY, namespace, partialPath, blacklist ), properties );
    }
    
    /** @return A new wildcard key, based on the block registry object key and block state properties. */
    public static Wildcard ofWildcard( RegObjKey.Wildcard<Block> key, BlockStatePropertyMap properties ) {
        return new Wildcard( key, properties );
    }
    
    /** @return A new tag key based on the tag resource location and block state properties. */
    public static Tag ofTag( String resLocAndProperties, boolean blacklist ) {
        String[] keys = BlockStatePropertyMap.split( resLocAndProperties );
        return ofTag( RegObjKey.ofTag( REGISTRY, keys[0], blacklist ),
                BlockStatePropertyMap.of( keys[1] ) );
    }
    
    /** @return A new tag key based on the tag resource location and block state properties. */
    public static Tag ofTag( ResourceLocation resLoc, BlockStatePropertyMap properties, boolean blacklist ) {
        return ofTag( RegObjKey.ofTag( REGISTRY, resLoc, blacklist ), properties );
    }
    
    /** @return A new tag key based on the tag key (well, different kind of tag key) and block state properties. */
    public static Tag ofTag( TagKey<? extends Block> tag, BlockStatePropertyMap properties, boolean blacklist ) {
        return ofTag( RegObjKey.ofTag( REGISTRY, tag, blacklist ), properties );
    }
    
    /** @return A new tag key based on the block registry object key and block state properties. */
    public static Tag ofTag( RegObjKey.Tag<Block> key, BlockStatePropertyMap properties ) {
        return new Tag( key, properties );
    }
    
    /** @return A new key based on the block registry object key and block state properties. */
    public static BlockStateKey<?> ofRegObj( RegObjKey<Block> key, BlockStatePropertyMap properties ) {
        if( key instanceof RegObjKey.Basic<Block> k ) return new Basic( k, properties );
        if( key instanceof RegObjKey.Wildcard<Block> k ) return new Wildcard( k, properties );
        if( key instanceof RegObjKey.Tag<Block> k ) return new Tag( k, properties );
        throw new IllegalArgumentException( "Invalid registry object key!" );
    }
    
    /** @return A new key, parsed from a key string, or null if the key was invalid. */
    @Nullable
    public static BlockStateKey<?> parse( @Nullable AbstractConfigField field, String line, String key, boolean blacklist ) {
        String[] keyAndProps = BlockStatePropertyMap.split( key );
        BlockStatePropertyMap properties = BlockStatePropertyMap.of( keyAndProps[1] );
        if( keyAndProps[0].isEmpty() ) return new PropsOnly( blacklist, properties );
        FuzzyKey<Block> loadedKey = REG_PARSER.parseKeyString( field, line, keyAndProps[0], blacklist );
        return loadedKey == null ? null :
                loadedKey instanceof RegObjKey.Basic<Block> k ? new Basic( k, properties ) :
                        loadedKey instanceof RegObjKey.Wildcard<Block> k ? new Wildcard( k, properties ) :
                                loadedKey instanceof RegObjKey.Tag<Block> k ? new Tag( k, properties ) : null;
    }
    
    
    // ---- Key Implementations ---- //
    
    protected static final IRegWrapper<Block> REGISTRY = IRegWrapper.of( ForgeRegistries.BLOCKS );
    protected static final IFuzzyKeyParser<Block> REG_PARSER = REGISTRY.getParser();
    
    /** This is null for PropsOnly keys. */
    protected final K regObjKey;
    protected final BlockStatePropertyMap stateProps;
    protected final String statePropsString;
    
    protected BlockStateKey( @Nullable K k, boolean blacklist, BlockStatePropertyMap p ) {
        super( blacklist );
        regObjKey = k;
        stateProps = p;
        statePropsString = stateProps.isEmpty() ? "" : stateProps.toTomlString();
    }
    
    /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
    @Override
    public String keyString() { return regObjKey.keyString() + statePropsString; }
    
    /** @return True if this key matches the target. */
    @Override
    public boolean matches( BlockState target ) {
        return regObjKey.matches( target.getBlock() ) && stateProps.matches( target );
    }
    
    
    /**
     * A key that matches any block with appropriate block state properties.
     */
    @ApiStatus.Experimental
    public static class PropsOnly extends BlockStateKey<RegObjKey<Block>> {
        
        protected PropsOnly( boolean blacklist, BlockStatePropertyMap p ) { super( null, blacklist, p ); }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() { return statePropsString; }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( BlockState target ) { return stateProps.matches( target ); }
    }
    
    
    /**
     * A key that matches one specific block with appropriate block state properties.
     * Also functions as a block state supplier.
     */
    @ApiStatus.Experimental
    public static class Basic extends BlockStateKey<RegObjKey.Basic<Block>> implements IReverseKey<BlockState> {
        
        protected Basic( RegObjKey.Basic<Block> k, BlockStatePropertyMap p ) { super( k, k.isBlacklist(), p ); }
        
        /** @return The value that matches this key, or null if anything goes wrong. */
        @Override // IReverseKey
        @Nullable // Should return an air block most of the time if the block was missing, but not guaranteed
        public BlockState asValue() {
            Block block = regObjKey.asValue();
            return block == null ? null : stateProps.stateFor( block );
        }
    }
    
    
    /**
     * A key that matches all blocks in a namespace that have a path starting with a specific string
     * with appropriate block state properties.
     */
    @ApiStatus.Experimental
    public static class Wildcard extends BlockStateKey<RegObjKey.Wildcard<Block>> {
        
        protected Wildcard( RegObjKey.Wildcard<Block> k, BlockStatePropertyMap p ) { super( k, k.isBlacklist(), p ); }
    }
    
    
    /**
     * A key that matches all blocks contained by a specific tag with appropriate block state properties.
     */
    @ApiStatus.Experimental
    public static class Tag extends BlockStateKey<RegObjKey.Tag<Block>> implements IMultiKey<BlockState> {
        
        protected Tag( RegObjKey.Tag<Block> k, BlockStatePropertyMap p ) { super( k, k.isBlacklist(), p ); }
        
        /** @return A value that matches this key, or null if anything goes wrong. */
        @Override // IRandomKey
        @Nullable
        public BlockState nextValue( RandomSource random ) {
            return stateProps.stateForNullable( regObjKey.nextValue( random ) );
        }
        
        /** @return An iterator over all values that match this key, or null if anything goes wrong. */
        @Override // IMultiKey
        @Nullable
        public Iterator<BlockState> getValueIterator() {
            Iterator<Block> itr = regObjKey.getValueIterator();
            return itr == null ? null : new IMultiKey.ConverterIterator<>( itr, stateProps::stateForNullable );
        }
    }
    
    
    // ---- Parser Implementation ---- //
    
    private record Parser() implements IFuzzyKeyParser<BlockState> {
        
        /** @return The key parser's type name (e.g., "Fuzzy"). */
        @Override
        public String getTypeName() { return "Block State"; }
        
        /** @return The key parser's patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
        @Override
        public String getPatterns( KeyUsage usage ) {
            return REG_PARSER.getPatterns( usage ) +
                    ", \"namespace:path[property1=value1,property2=value2,...]\" (Note: [block_state_properties] is allowed on any key)";
        }
        
        /**
         * Loads a key from the provided TOML string. If anything goes wrong, correct it at the lowest level possible,
         * and if the config field is not null, provide useful feedback and identify the field.
         *
         * @param field The config field we are loading for, or null if error reporting should be suppressed.
         * @param line  The full line, for error context.
         * @param key   The key string to parse from.
         * @return A new fuzzy key based on the key string, or null if parsing fails.
         */
        @Override
        @Nullable
        public FuzzyKey<BlockState> parseKeyString( @Nullable AbstractConfigField field, String line, String key, boolean blacklist ) {
            return parse( field, line, key, blacklist );
        }
    }
}