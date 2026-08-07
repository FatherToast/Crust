package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.BlockStateKey;
import fathertoast.crust.api.config.common.value.collection.key.WeightedKey;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;

/**
 * A fuzzy weighted list used to randomly pick block states.
 *
 * @see BlockStateKey
 * @see fathertoast.crust.api.config.common.field.collection.BlockStateWeightedListField
 * @see BlockStateWeightedValueList BlockStateWeightedValueList - A similar collection that allows values
 */
@SuppressWarnings( "unused" )
public class BlockStateWeightedList extends FuzzyWeightedList<BlockState> {
    
    /** Constructs an empty weighted list. Use this if you want to {@link #load} a weighted list from file/NBT. */
    public BlockStateWeightedList() { super( BlockStateKey.PARSER ); }
    
    /**
     * Constructs a weighted list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateWeightedList.Builder} is much easier.
     */
    @SafeVarargs
    public BlockStateWeightedList( WeightedKey<BlockState>... keys ) { super( BlockStateKey.PARSER, keys ); }
    
    /**
     * Constructs a weighted list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateWeightedList.Builder} is much easier.
     */
    public BlockStateWeightedList( Collection<WeightedKey<BlockState>> keys ) { super( BlockStateKey.PARSER, keys ); }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public BlockStateWeightedList makeNew() { return new BlockStateWeightedList(); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry weighted lists smoother. */
    public static class Builder<B extends Builder<B>> extends AbstractBuilder<BlockState, BlockStateWeightedList, B> {
        
        /** @return A new fuzzy weighted list reflecting the current state of this builder. */
        @Override
        public BlockStateWeightedList build() { return new BlockStateWeightedList( list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B add( int weight, String resLocAndProperties ) { return add( weight, BlockStateKey.of( resLocAndProperties, false ) ); }
        
        /** Adds a key based on the resource location. Matches only the provided block with the appropriate block state properties. */
        public B add( int weight, ResourceLocation resLoc, BlockStatePropertyMap properties ) { return add( weight, BlockStateKey.of( resLoc, properties, false ) ); }
        
        /** Adds a key based on the resource location. Matches only the provided block with any block state properties. */
        public B add( int weight, ResourceLocation resLoc ) { return add( weight, resLoc, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a key based on the registry object. Matches only the provided block with the appropriate block state properties. */
        public B add( int weight, RegistryObject<? extends Block> regObj, BlockStatePropertyMap properties ) { return add( weight, BlockStateKey.of( regObj, properties, false ) ); }
        
        /** Adds a key based on the registry object. Matches only the provided block with any block state properties. */
        public B add( int weight, RegistryObject<? extends Block> regObj ) { return add( weight, regObj, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a key based on the resource key. Matches only the provided block with the appropriate block state properties. */
        public B add( int weight, ResourceKey<? extends Block> resKey, BlockStatePropertyMap properties ) { return add( weight, BlockStateKey.of( resKey, properties, false ) ); }
        
        /** Adds a key based on the resource key. Matches only the provided block with any block state properties. */
        public B add( int weight, ResourceKey<? extends Block> resKey ) { return add( weight, resKey, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a key based on the block. Only suitable for vanilla stuff. Matches only the provided block with the appropriate block state properties. */
        public B add( int weight, Block block, BlockStatePropertyMap properties ) { return add( weight, BlockStateKey.of( block, properties, false ) ); }
        
        /** Adds a key based on the block. Only suitable for vanilla stuff. Matches only the provided block with any block state properties. */
        public B add( int weight, Block block ) { return add( weight, block, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a key based on the block state. Only suitable for vanilla stuff. Matches only the provided block state. */
        public B add( int weight, BlockState blockState ) { return add( weight, BlockStateKey.of( blockState, false ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B addTag( int weight, String resLocAndProperties ) { return add( weight, BlockStateKey.ofTag( resLocAndProperties, false ) ); }
        
        /** Adds a tag key based on the resource location. Matches every block in the tag with the appropriate block state properties. */
        public B addTag( int weight, ResourceLocation resLoc, BlockStatePropertyMap properties ) { return add( weight, BlockStateKey.ofTag( resLoc, properties, false ) ); }
        
        /** Adds a tag key based on the resource location. Matches every block in the tag with any block state properties. */
        public B addTag( int weight, ResourceLocation resLoc ) { return addTag( weight, resLoc, BlockStatePropertyMap.EMPTY ); }
        
        /** Adds a tag key based on the tag. Matches every block in the tag with the appropriate block state properties. */
        public B addTag( int weight, TagKey<Block> tag, BlockStatePropertyMap properties ) { return add( weight, BlockStateKey.ofTag( tag, properties, false ) ); }
        
        /** Adds a tag key based on the tag. Matches every block in the tag with any block state properties. */
        public B addTag( int weight, TagKey<Block> tag ) { return addTag( weight, tag, BlockStatePropertyMap.EMPTY ); }
    }
}