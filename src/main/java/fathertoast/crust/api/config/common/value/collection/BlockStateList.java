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
 * A fuzzy list used to iterate over block states.
 *
 * @see BlockStateKey
 * @see fathertoast.crust.api.config.common.field.collection.BlockStateListField
 * @see BlockStateValueList BlockStateValueList - A similar collection that allows values
 */
@ApiStatus.Experimental
public class BlockStateList extends FuzzyList<BlockState> {
    
    /** Constructs an empty list. Use this if you want to {@link #load} a list from file/NBT. */
    public BlockStateList() { super( BlockStateKey.PARSER ); }
    
    /**
     * Constructs a list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateList.Builder} is much easier.
     */
    @SafeVarargs
    public BlockStateList( FuzzyKey<BlockState>... keys ) { super( BlockStateKey.PARSER, keys ); }
    
    /**
     * Constructs a list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateList.Builder} is much easier.
     */
    public BlockStateList( Collection<FuzzyKey<BlockState>> keys ) { super( BlockStateKey.PARSER, keys ); }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public BlockStateList makeNew() { return new BlockStateList(); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing block state lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<B extends Builder<B>> extends AbstractBuilder<BlockState, BlockStateList, B> {
        
        /** @return A new fuzzy list reflecting the current state of this builder. */
        @Override
        public BlockStateList build() { return new BlockStateList( list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B add( String resLocAndProperties ) { return add( BlockStateKey.of( resLocAndProperties, false ) ); }
        
        /** Adds a key based on the resource location. Matches only the provided block with any block state properties. */
        public B add( ResourceLocation resLoc ) { return add( resLoc, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a key based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B add( ResourceLocation resLoc, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( resLoc, properties, false ) ); }
        
        /** Adds a key based on the registry object. Matches only the provided block with any block state properties. */
        public B add( RegistryObject<? extends Block> regObj ) { return add( regObj, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a key based on the registry object. Matches only the provided block with the appropriate block state properties. */
        public B add( RegistryObject<? extends Block> regObj, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( regObj, properties, false ) ); }
        
        /** Adds a key based on the resource key. Matches only the provided block with any block state properties. */
        public B add( ResourceKey<? extends Block> resKey ) { return add( resKey, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a key based on the resource key. Matches only the provided block with the appropriate block state properties. */
        public B add( ResourceKey<? extends Block> resKey, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( resKey, properties, false ) ); }
        
        /** Adds a key based on the block. Only suitable for vanilla stuff. Matches only the provided block with any block state properties. */
        public B add( Block block ) { return add( block, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a key based on the block. Only suitable for vanilla stuff. Matches only the provided block with the appropriate block state properties. */
        public B add( Block block, BlockStatePropertyMap properties ) { return add( BlockStateKey.of( block, properties, false ) ); }
        
        /** Adds a key based on the block state. Only suitable for vanilla stuff. Matches only the provided block state. */
        public B add( BlockState blockState ) { return add( BlockStateKey.of( blockState, false ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B addTag( String resLocAndProperties ) { return add( BlockStateKey.ofTag( resLocAndProperties, false ) ); }
        
        /** Adds a tag key based on the resource location. Matches every block in the tag with any block state properties. */
        public B addTag( ResourceLocation resLoc ) { return addTag( resLoc, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a tag key based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B addTag( ResourceLocation resLoc, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofTag( resLoc, properties, false ) ); }
        
        /** Adds a tag key based on the tag. Matches every block in the tag with any block state properties. */
        public B addTag( TagKey<Block> tag ) { return addTag( tag, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a tag key based on the tag. Matches every block in the tag with the appropriate block state properties. */
        public B addTag( TagKey<Block> tag, BlockStatePropertyMap properties ) { return add( BlockStateKey.ofTag( tag, properties, false ) ); }
    }
}