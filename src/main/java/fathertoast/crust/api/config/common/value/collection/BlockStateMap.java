package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.BlockStateKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy map used to associate values with block states.
 *
 * @param <V> The value type.
 * @see BlockStateKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.BlockStateMapField
 * @see BlockStateSet BlockStateSet - A similar collection that does not allow values
 */
@ApiStatus.Experimental
public class BlockStateMap<V> extends FuzzyMap<BlockState, V> {
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    public BlockStateMap( IValueCodec<V> codec ) { super( BlockStateKey.PARSER, codec ); }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateMap.Builder} is much easier.
     */
    @SafeVarargs
    public BlockStateMap( IValueCodec<V> codec, FuzzyEntry<BlockState, V>... keys ) {
        super( BlockStateKey.PARSER, codec, keys );
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateMap.Builder} is much easier.
     */
    public BlockStateMap( IValueCodec<V> codec, Collection<FuzzyEntry<BlockState, V>> keys ) {
        super( BlockStateKey.PARSER, codec, keys );
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public BlockStateMap<V> makeNew() { return new BlockStateMap<>( valueCodec ); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing block state maps smoother. */
    @ApiStatus.Experimental
    public static class Builder<V, B extends Builder<V, B>> extends AbstractBuilder<BlockState, V, BlockStateMap<V>, B> {
        
        public Builder( IValueCodec<V> codec ) { super( codec ); }
        
        /** @return A new fuzzy map reflecting the current state of this builder. */
        @Override
        public BlockStateMap<V> build() { return new BlockStateMap<>( valueCodec, list ); }
        
        
        // ---- Properties-Only Keys ---- //
        
        /** Adds a props-only key-value pair based on the resource location. Matches any block with the appropriate block state properties. */
        public B putProps( String properties, V value ) { return put( BlockStateKey.ofProps( properties, false ), value ); }
        
        /** Adds a props-only key-value pair based on the resource location. Matches any block with the appropriate block state properties. */
        public B putProps( BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.ofProps( properties, false ), value ); }
        
        /** Adds a blacklist props-only key based on the resource location. Matches any block with the appropriate block state properties. */
        public B putPropsBlacklist( String properties ) { return putBlacklist( BlockStateKey.ofProps( properties, true ) ); }
        
        /** Adds a blacklist props-only key based on the resource location. Matches any block with the appropriate block state properties. */
        public B putPropsBlacklist( BlockStatePropertyMap properties ) { return putBlacklist( BlockStateKey.ofProps( properties, true ) ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key-value pair based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B put( String resLocAndProperties, V value ) { return put( BlockStateKey.of( resLocAndProperties, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B put( ResourceLocation resLoc, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.of( resLoc, properties, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. Matches only the provided block with any block state properties. */
        public B put( ResourceLocation resLoc, V value ) { return put( resLoc, BlockStatePropertyMap.EMPTY, value ); }
        
        /** Adds a key-value pair based on the registry object. Matches only the provided block with the appropriate block state properties. */
        public B put( RegistryObject<? extends Block> regObj, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.of( regObj, properties, false ), value ); }
        
        /** Adds a key-value pair based on the registry object. Matches only the provided block with any block state properties. */
        public B put( RegistryObject<? extends Block> regObj, V value ) { return put( regObj, BlockStatePropertyMap.EMPTY, value ); }
        
        /** Adds a key-value pair based on the resource key. Matches only the provided block with the appropriate block state properties. */
        public B put( ResourceKey<? extends Block> resKey, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.of( resKey, properties, false ), value ); }
        
        /** Adds a key-value pair based on the resource key. Matches only the provided block with any block state properties. */
        public B put( ResourceKey<? extends Block> resKey, V value ) { return put( resKey, BlockStatePropertyMap.EMPTY, value ); }
        
        /** Adds a key-value pair based on the block. Only suitable for vanilla stuff. Matches only the provided block with the appropriate block state properties. */
        public B put( Block block, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.of( block, properties, false ), value ); }
        
        /** Adds a key-value pair based on the block. Only suitable for vanilla stuff. Matches only the provided block with any block state properties. */
        public B put( Block block, V value ) { return put( block, BlockStatePropertyMap.EMPTY, value ); }
        
        /** Adds a key-value pair based on the block state. Only suitable for vanilla stuff. Matches only the provided block state. */
        public B put( BlockState blockState, V value ) { return put( blockState.getBlock(), BlockStatePropertyMap.of( blockState ), value ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B putBlacklist( String resLocAndProperties ) { return putBlacklist( BlockStateKey.of( resLocAndProperties, true ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B putBlacklist( ResourceLocation resLoc, BlockStatePropertyMap properties ) { return putBlacklist( BlockStateKey.of( resLoc, properties, true ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided block with any block state properties. */
        public B putBlacklist( ResourceLocation resLoc ) { return putBlacklist( resLoc, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a blacklist key based on the registry object. Matches only the provided block with the appropriate block state properties. */
        public B putBlacklist( RegistryObject<? extends Block> regObj, BlockStatePropertyMap properties ) { return putBlacklist( BlockStateKey.of( regObj, properties, true ) ); }
        
        /** Adds a blacklist key based on the registry object. Matches only the provided block with any block state properties. */
        public B putBlacklist( RegistryObject<? extends Block> regObj ) { return putBlacklist( regObj, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a blacklist key based on the resource key. Matches only the provided block with the appropriate block state properties. */
        public B putBlacklist( ResourceKey<? extends Block> resKey, BlockStatePropertyMap properties ) { return putBlacklist( BlockStateKey.of( resKey, properties, true ) ); }
        
        /** Adds a blacklist key based on the resource key. Matches only the provided block with any block state properties. */
        public B putBlacklist( ResourceKey<? extends Block> resKey ) { return putBlacklist( resKey, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a blacklist key based on the block. Only suitable for vanilla stuff. Matches only the provided block with the appropriate block state properties. */
        public B putBlacklist( Block block, BlockStatePropertyMap properties ) { return putBlacklist( BlockStateKey.of( block, properties, true ) ); }
        
        /** Adds a blacklist key based on the block. Only suitable for vanilla stuff. Matches only the provided block with any block state properties. */
        public B putBlacklist( Block block ) { return putBlacklist( block, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a blacklist key based on the block state. Only suitable for vanilla stuff. Matches only the provided block state. */
        public B putBlacklist( BlockState blockState ) { return putBlacklist( blockState.getBlock(), BlockStatePropertyMap.of( blockState ) ); }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key-value pair based on the partial resource location. Matches every block in the namespace that starts with the partial path with the appropriate block state properties. */
        public B putWildcard( ResourceLocation partialResLoc, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.ofWildcard( partialResLoc, properties, false ), value ); }
        
        /** Adds a wildcard key-value pair based on the partial resource location. Matches every block in the namespace that starts with the partial path with any block state properties. */
        public B putWildcard( ResourceLocation partialResLoc, V value ) { return putWildcard( partialResLoc, BlockStatePropertyMap.EMPTY, value ); }
        
        /** Adds a wildcard key-value pair based on the namespace. Matches every block in the namespace with the appropriate block state properties. */
        public B putWildcard( String namespace, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.ofWildcard( namespace, properties, false ), value ); }
        
        /** Adds a wildcard key-value pair based on the namespace. Matches every block in the namespace with any block state properties. */
        public B putWildcard( String namespace, V value ) { return putWildcard( namespace, BlockStatePropertyMap.EMPTY, value ); }
        
        /** Adds a wildcard key-value pair based on the namespace and partial path. Matches every block in the namespace that starts with the partial path with the appropriate block state properties. */
        public B putWildcard( String namespace, String partialPath, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.ofWildcard( namespace, partialPath, properties, false ), value ); }
        
        /** Adds a wildcard key-value pair based on the namespace and partial path. Matches every block in the namespace that starts with the partial path with any block state properties. */
        public B putWildcard( String namespace, String partialPath, V value ) { return putWildcard( namespace, partialPath, BlockStatePropertyMap.EMPTY, value ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. Matches every block in the namespace that starts with the partial path with the appropriate block state properties. */
        public B putWildcardBlacklist( ResourceLocation partialResLoc, BlockStatePropertyMap properties ) { return putBlacklist( BlockStateKey.ofWildcard( partialResLoc, properties, true ) ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. Matches every block in the namespace that starts with the partial path with any block state properties. */
        public B putWildcardBlacklist( ResourceLocation partialResLoc ) { return putWildcardBlacklist( partialResLoc, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a blacklist wildcard key based on the namespace. Matches every block in the namespace with the appropriate block state properties. */
        public B putWildcardBlacklist( String namespace, BlockStatePropertyMap properties ) { return putBlacklist( BlockStateKey.ofWildcard( namespace, properties, true ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace. Matches every block in the namespace with any block state properties. */
        public B putWildcardBlacklist( String namespace ) { return putWildcardBlacklist( namespace, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. Matches every block in the namespace that starts with the partial path with the appropriate block state properties. */
        public B putWildcardBlacklist( String namespace, String partialPath, BlockStatePropertyMap properties ) { return putBlacklist( BlockStateKey.ofWildcard( namespace, partialPath, properties, true ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. Matches every block in the namespace that starts with the partial path with any block state properties. */
        public B putWildcardBlacklist( String namespace, String partialPath ) { return putWildcardBlacklist( namespace, partialPath, BlockStatePropertyMap.EMPTY ); }
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key-value pair based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B putTag( String resLocAndProperties, V value ) { return put( BlockStateKey.ofTag( resLocAndProperties, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B putTag( ResourceLocation resLoc, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.ofTag( resLoc, properties, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Matches every block in the tag with any block state properties. */
        public B putTag( ResourceLocation resLoc, V value ) { return putTag( resLoc, BlockStatePropertyMap.EMPTY, value ); }
        
        /** Adds a tag key-value pair based on the tag. Matches every block in the tag with the appropriate block state properties. */
        public B putTag( TagKey<Block> tag, BlockStatePropertyMap properties, V value ) { return put( BlockStateKey.ofTag( tag, properties, false ), value ); }
        
        /** Adds a tag key-value pair based on the tag. Matches every block in the tag with any block state properties. */
        public B putTag( TagKey<Block> tag, V value ) { return putTag( tag, BlockStatePropertyMap.EMPTY, value ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B putTagBlacklist( String resLocAndProperties ) { return putBlacklist( BlockStateKey.ofTag( resLocAndProperties, true ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B putTagBlacklist( ResourceLocation resLoc, BlockStatePropertyMap properties ) { return putBlacklist( BlockStateKey.ofTag( resLoc, properties, true ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every block in the tag with any block state properties. */
        public B putTagBlacklist( ResourceLocation resLoc ) { return putTagBlacklist( resLoc, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a blacklist tag key based on the tag. Matches every block in the tag with the appropriate block state properties. */
        public B putTagBlacklist( TagKey<Block> tag, BlockStatePropertyMap properties ) { return putBlacklist( BlockStateKey.ofTag( tag, properties, true ) ); }
        
        /** Adds a blacklist tag key based on the tag. Matches every block in the tag with any block state properties. */
        public B putTagBlacklist( TagKey<Block> tag ) { return putTagBlacklist( tag, BlockStatePropertyMap.EMPTY ); }
    }
}