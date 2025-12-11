package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.BlockStateKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
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
 * A fuzzy set used to match block states.
 *
 * @see BlockStateKey
 * @see fathertoast.crust.api.config.common.field.collection.BlockStateSetField
 * @see BlockStateMap BlockStateMap - A similar collection that allows values
 */
@ApiStatus.Experimental
public class BlockStateSet extends FuzzySet<BlockState> {
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public BlockStateSet() { super( BlockStateKey.PARSER ); }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateSet.Builder} is much easier.
     */
    @SafeVarargs
    public BlockStateSet( FuzzyKey<BlockState>... keys ) { super( BlockStateKey.PARSER, keys ); }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateSet.Builder} is much easier.
     */
    public BlockStateSet( Collection<FuzzyKey<BlockState>> keys ) { super( BlockStateKey.PARSER, keys ); }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public BlockStateSet makeNew() { return new BlockStateSet(); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing block state sets smoother. */
    @ApiStatus.Experimental
    public static class Builder<B extends Builder<B>> extends AbstractBuilder<BlockState, BlockStateSet, B> {
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        @Override
        public BlockStateSet build() { return new BlockStateSet( list ); }
        
        
        // ---- Properties-Only Keys ---- //
        
        /** Adds a props-only key based on the resource location. Matches any block with the appropriate block state properties. */
        public B addProps( String resLocAndProperties ) { return add( BlockStateKey.of( resLocAndProperties, false ) ); }
        
        /** Adds a blacklist props-only key based on the resource location. Matches any block with the appropriate block state properties. */
        public B addPropsBlacklist( String resLocAndProperties ) { return add( BlockStateKey.of( resLocAndProperties, true ) ); }
        
        /** Adds a props-only key based on the resource location. Matches any block with the appropriate block state properties. */
        public B addProps( ResourceLocation resLoc, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( resLoc, properties, false ) ); }
        
        /** Adds a blacklist props-only key based on the resource location. Matches any block with the appropriate block state properties. */
        public B addPropsBlacklist( ResourceLocation resLoc, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( resLoc, properties, true ) ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B add( String resLocAndProperties ) { return add( BlockStateKey.of( resLocAndProperties, false ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B addBlacklist( String resLocAndProperties ) { return add( BlockStateKey.of( resLocAndProperties, true ) ); }
        
        /** Adds a key based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B add( ResourceLocation resLoc, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( resLoc, properties, false ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B addBlacklist( ResourceLocation resLoc, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( resLoc, properties, true ) ); }
        
        /** Adds a key based on the registry object. Matches only the provided block with the appropriate block state properties. */
        public B add( RegistryObject<? extends Block> regObj, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( regObj, properties, false ) ); }
        
        /** Adds a blacklist key based on the registry object. Matches only the provided block with the appropriate block state properties. */
        public B addBlacklist( RegistryObject<? extends Block> regObj, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( regObj, properties, true ) ); }
        
        /** Adds a key based on the resource key. Matches only the provided block with the appropriate block state properties. */
        public B add( ResourceKey<? extends Block> resKey, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( resKey, properties, false ) ); }
        
        /** Adds a blacklist key based on the resource key. Matches only the provided block with the appropriate block state properties. */
        public B addBlacklist( ResourceKey<? extends Block> resKey, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( resKey, properties, true ) ); }
        
        /** Adds a key based on the block. Only suitable for vanilla stuff. Matches only the provided block with the appropriate block state properties. */
        public B add( Block block, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( block, properties, false ) ); }
        
        /** Adds a blacklist key based on the block. Only suitable for vanilla stuff. Matches only the provided block with the appropriate block state properties. */
        public B addBlacklist( Block block, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( block, properties, true ) ); }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key based on the partial resource location. Matches every block in the namespace that starts with the partial path with the appropriate block state properties. */
        public B addWildcard( ResourceLocation partialResLoc, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofWildcard( partialResLoc, properties, false ) ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. Matches every block in the namespace that starts with the partial path with the appropriate block state properties. */
        public B addWildcardBlacklist( ResourceLocation partialResLoc, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofWildcard( partialResLoc, properties, true ) ); }
        
        /** Adds a wildcard key based on the namespace. Matches every block in the namespace with the appropriate block state properties. */
        public B addWildcard( String namespace, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofWildcard( namespace, properties, false ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace. Matches every block in the namespace with the appropriate block state properties. */
        public B addWildcardBlacklist( String namespace, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofWildcard( namespace, properties, true ) ); }
        
        /** Adds a wildcard key based on the namespace and partial path. Matches every block in the namespace that starts with the partial path with the appropriate block state properties. */
        public B addWildcard( String namespace, String partialPath, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofWildcard( namespace, partialPath, properties, false ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. Matches every block in the namespace that starts with the partial path with the appropriate block state properties. */
        public B addWildcardBlacklist( String namespace, String partialPath, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofWildcard( namespace, partialPath, properties, true ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B addTag( String resLocAndProperties ) { return add( BlockStateKey.ofTag( resLocAndProperties, false ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B addTagBlacklist( String resLocAndProperties ) { return add( BlockStateKey.ofTag( resLocAndProperties, true ) ); }
        
        /** Adds a tag key based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B addTag( ResourceLocation resLoc, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofTag( resLoc, properties, false ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B addTagBlacklist( ResourceLocation resLoc, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofTag( resLoc, properties, true ) ); }
        
        /** Adds a tag key based on the tag. Matches every block in the tag with the appropriate block state properties. */
        public B addTag( TagKey<Block> tag, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofTag( tag, properties, false ) ); }
        
        /** Adds a blacklist tag key based on the tag. Matches every block in the tag with the appropriate block state properties. */
        public B addTagBlacklist( TagKey<Block> tag, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofTag( tag, properties, true ) ); }
    }
}